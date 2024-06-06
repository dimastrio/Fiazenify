package id.dimas.fiazenify.ui.main.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.FragmentProfileBinding
import id.dimas.fiazenify.ui.auth.AuthActivity
import id.dimas.fiazenify.ui.auth.AuthViewModel
import id.dimas.fiazenify.ui.main.bottomsheet.ChangePasswordBottomSheet
import id.dimas.fiazenify.ui.main.bottomsheet.ChangeUsernameBottomSheet
import id.dimas.fiazenify.ui.main.bottomsheet.EmergencyFundBottomSheet
import id.dimas.fiazenify.ui.main.bottomsheet.FinancialCalculatorBottomSheet
import id.dimas.fiazenify.ui.main.bottomsheet.ProfileBottomSheet
import id.dimas.fiazenify.ui.main.bottomsheet.RetirementFundBottomSheet
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private var isConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unregisterListenerForInternetConnectivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForInternetConnectivity()
        onClickMenu()
        getProfileUser()
        setupRefresh()
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            getProfileUser()
        }
    }

    fun refresh() {
        getProfileUser()
    }


    private fun onClickMenu() {
        binding.menu.profile.setOnClickListener {
            checkPermissions(::setupBottomSheetMenu)
        }

        binding.menu.calculator.setOnClickListener {
            setupBottomSheetRecommend()
        }
        binding.menu.logout.setOnClickListener {
            authViewModel.logout().observe(viewLifecycleOwner) {
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }


    private fun getProfileUser() {
        authViewModel.getUser().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.apply {
                            swipeRefresh.isRefreshing = false
                            tvUsername.text = result.data.displayName
                            tvEmail.text = result.data.email
                            val uri = result.data.photoUrl
                            if (uri == null) {
                                binding.ivProfile.setImageResource(R.drawable.ic_image)
                            } else {
                                Glide.with(requireActivity()).load(uri).into(binding.ivProfile)
                            }
                        }
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }

    private fun setupBottomSheetRecommend() {
        val bottomSheet = FinancialCalculatorBottomSheet()
        bottomSheet.show(childFragmentManager, null)
        bottomSheet.bottomSheetCallback =
            object : FinancialCalculatorBottomSheet.BottomSheetCallback {
                override fun onSelectRetirementFund() {
                    val retirementBottomSheet = RetirementFundBottomSheet()
                    retirementBottomSheet.show(childFragmentManager, null)
                }

                override fun onSelectEmergencyFund() {
                    val emergencyBottomSheet = EmergencyFundBottomSheet()
                    emergencyBottomSheet.show(childFragmentManager, null)
                }
            }
    }


    private fun setupBottomSheetMenu() {
        val bottomSheet = ProfileBottomSheet()
        bottomSheet.show(childFragmentManager, null)
        bottomSheet.bottomSheetCallback = object : ProfileBottomSheet.BottomSheetCallback {

            override fun onSelectImage(uri: Uri) {
                if (isConnected) {
                    setupSelectImageProfile(uri)
                    bottomSheet.dismiss()
                } else {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Aktifkan Koneksi Internet",
                        Status.FAILED
                    )
                    bottomSheet.dismiss()
                }
            }

            override fun onSelectProfile() {
                if (isConnected) {
                    setupBottomSheetUsername()
//                bottomSheet.dismiss()
                } else {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Aktifkan Koneksi Internet",
                        Status.FAILED
                    )
                }
            }

            override fun onSelectPassword() {
                if (isConnected) {
                    setupBottomSheetPass()
//                bottomSheet.dismiss()
                } else {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Aktifkan Koneksi Internet",
                        Status.FAILED
                    )
                }
            }
        }
    }

    private fun setupSelectImageProfile(uri: Uri) {
        profileViewModel.updateProfilePhoto(uri).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null)
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Foto berhasil diubah"
                        )
                }

                is Result.Error -> {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Foto gagal diubah",
                        Status.FAILED
                    )

                }
            }
        }
    }

    private fun setupBottomSheetPass() {
        val bottomSheet = ChangePasswordBottomSheet()
        bottomSheet.show(childFragmentManager, null)
        bottomSheet.bottomSheetCallback = object : ChangePasswordBottomSheet.NewPassCallback {
            override fun onChange(oldPass: String, newPass: String) {
                observeChangePass(oldPass, newPass)
                bottomSheet.dismiss()
            }
        }
    }

    private fun setupBottomSheetUsername() {
        val bottomSheet = ChangeUsernameBottomSheet()
        bottomSheet.show(childFragmentManager, null)
        bottomSheet.bottomSheetCallback =
            object : ChangeUsernameBottomSheet.ChangeUsernameCallback {
                override fun onChange(newUsername: String) {
                    observeChangeUsername(newUsername)
                    bottomSheet.dismiss()
//                Helper.showToast(requireContext(),"Test")
                }

            }

    }

    private fun observeChangePass(oldPass: String, newPass: String) {
        profileViewModel.changePassword(oldPass, newPass).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Berhasil ganti password"
                        )
                    }
                }

                is Result.Error -> {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Password lama salah",
                        Status.FAILED
                    )
                }
            }
        }
    }

    private fun observeChangeUsername(newUsername: String) {
        profileViewModel.changeUsername(newUsername).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Berhasil ganti username"
                        )
                    }
                }

                is Result.Error -> {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Gagal ganti username",
                        Status.FAILED
                    )
                }
            }
        }
    }

//    private fun checkPermissionImage(){
//        val permissionReadCheck = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//        val permissionWriteCheck = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if ((permissionReadCheck == PackageManager.PERMISSION_DENIED) && (permissionWriteCheck == PackageManager.PERMISSION_DENIED)){
//            showPermissionDeniedDialog()
//        }else {
//            openGallery()
//        }
//    }

    private fun checkPermissions(initBottomSheet: () -> Unit) {
        if (checkPermissionsIsGranted(
                requireActivity(),
                Manifest.permission.CAMERA,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION
            )
        ) {
            initBottomSheet.invoke()
        }
    }

    private fun checkPermissionsIsGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        requestCode: Int
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, please allow permissions from App Settings.")
            .setPositiveButton("Pengaturan") { _, _ -> openAppSettings() }
            .setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent()
        val uri = Uri.fromParts("package", requireActivity().packageName, null)

        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = uri

        startActivity(intent)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i("infonetwork", "Available")
            isConnected = true
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Log.i("infonetwork", "Losing")
            isConnected = false
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i("infonetwork", "Lost")
            isConnected = false
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Log.i("infonetwork", "Unavailable")
            isConnected = false
        }
    }

    private fun listenForInternetConnectivity() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unregisterListenerForInternetConnectivity() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.unregisterNetworkCallback(networkCallback)
    }


    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }

}
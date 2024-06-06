package id.dimas.fiazenify.ui.main.bottomsheet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetProfileBinding

@AndroidEntryPoint
class ProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProfileBinding? = null
    private val binding get() = _binding!!

    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProfileBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheet() {
        binding.btnProfile.setOnClickListener {
            dismiss()
            bottomSheetCallback?.onSelectProfile()
        }

        binding.btnImage.setOnClickListener {
            openGallery()
        }

        binding.btnPassword.setOnClickListener {
            dismiss()
            bottomSheetCallback?.onSelectPassword()
        }
    }


    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data as Uri
                bottomSheetCallback?.onSelectImage(uri)
            }
        }


    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherGallery.launch(chooser)
    }


//    @AfterPermissionGranted(REQUEST_CODE_READ_EXTERNAL)
//    private fun checkStoragePermission(){
//        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)){
//            openGallery()
//        }else {
////            EasyPermissions.requestPermissions(this,"Aplikasi ini membutuhkan akses penyimpanan",
////                REQUEST_CODE_READ_EXTERNAL,Manifest.permission.READ_EXTERNAL_STORAGE)
//            showPermissionDeniedDialog()
//        }
//    }


//    private fun checkPermissions() {
//        if (checkPermissionsIsGranted(
//                requireActivity(),
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ),
//                REQUEST_CODE_PERMISSION
//            )
//        ) {
//            openGallery()
//        }
//    }

//    private fun checkPermissionsIsGranted(
//        activity: Activity,
//        permission: String,
//        permissions: Array<String>,
//        requestCode: Int
//    ): Boolean {
//        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
//        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//                showPermissionDeniedDialog()
//            } else {
//                ActivityCompat.requestPermissions(activity, permissions, requestCode)
//            }
//            false
//        } else {
//            true
//        }
//    }


    interface BottomSheetCallback {
        fun onSelectImage(uri: Uri)
        fun onSelectProfile()
        fun onSelectPassword()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
        private const val REQUEST_CODE_READ_EXTERNAL = 200
    }

}
package id.dimas.fiazenify.ui.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.MainActivity
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.FragmentLoginBinding
import id.dimas.fiazenify.ui.main.bottomsheet.ForgotPasswordBottomSheet
import id.dimas.fiazenify.util.Extension.isValidated
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import kotlinx.coroutines.FlowPreview


@FlowPreview
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var isConnected = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unregisterListenerForInternetConnectivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toRegisterFragment()
        toForgotPasswordBottomSheet()
        login()
        checkAuth()
        listenForInternetConnectivity()
    }


    private fun checkAuth() {
        viewModel.checkAuth().observe(viewLifecycleOwner) {
            if (it) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }


    private fun login() {
        binding.apply {
            btnLogin.setOnClickListener {

                etlEmail.isHelperTextEnabled = false
                etlPassword.isHelperTextEnabled = false

                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (isConnected) {
                    if (validateData(email, password)) {
                        observeLogin(email, password)
                    }
                } else {
                    binding.tvAlert.isVisible = true
                    binding.tvAlert.text = "Aktifkan Koneksi Internet"
                }

            }
        }
    }


    private fun toRegisterFragment() {
        binding.tvToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.auth_nav_host, RegisterFragment())
                addToBackStack(null)
                commit()

            }
        }
    }

    private fun toForgotPasswordBottomSheet() {
        binding.tvForgotPassword.isVisible = true
        binding.tvForgotPassword.setOnClickListener {
            val bottomSheet = ForgotPasswordBottomSheet()
            bottomSheet.show(childFragmentManager, null)
            bottomSheet.bottomSheetCallback =
                object : ForgotPasswordBottomSheet.BottomSheetCallback {
                    override fun onSendEmail(email: String) {
                        observeForgotPassword(email)
                        bottomSheet.dismiss()
                    }
                }
        }
    }

    private fun observeForgotPassword(email: String) {
        viewModel.forgotPassowrd(email).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        Helper.showSnackbarAuth(
                            requireContext(),
                            binding.root,
                            "Sukses kirim email"
                        )
                    }
                }

                is Result.Error -> {

                }

            }
        }
    }

    private fun observeLogin(email: String, password: String) {
        viewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    binding.tvAlert.isGone = true
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }

                is Result.Error -> {
                    binding.tvAlert.isVisible = true
                    binding.tvAlert.text = "Email atau password salah"
                }
            }
        }
    }

    private fun validateData(
        email: String,
        password: String,
    ): Boolean {
        return when {
            email.isBlank() -> {
                binding.etlEmail.isHelperTextEnabled = true
                binding.etlEmail.helperText = "Email tidak boleh kosong"
                binding.etlEmail.requestFocus()
                false
            }

            !email.isValidated() -> {
                binding.etlEmail.isHelperTextEnabled = true
                binding.etlEmail.helperText = "Email tidak valid"
                binding.etlEmail.requestFocus()
                false
            }

            password.isBlank() -> {
                binding.etlPassword.isHelperTextEnabled = true
                binding.etlPassword.helperText = "Password tidak boleh kosong"
                binding.etlPassword.requestFocus()
                false
            }

            password.length < 6 -> {
                binding.etlPassword.isHelperTextEnabled = true
                binding.etlPassword.helperText = "Password tidak boleh kurang dari 6 karakter"
                binding.etlPassword.requestFocus()
                false
            }

            password.length > 30 -> {
                binding.etlPassword.isHelperTextEnabled = true
                binding.etlPassword.helperText = "Password tidak boleh lebih dari 30 karakter"
                binding.etlPassword.requestFocus()
                false
            }

            else -> true
        }
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

}
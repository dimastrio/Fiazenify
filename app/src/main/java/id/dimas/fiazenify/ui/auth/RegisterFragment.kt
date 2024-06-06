package id.dimas.fiazenify.ui.auth

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
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
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.FragmentRegisterBinding
import id.dimas.fiazenify.util.Extension.isValidated
import id.dimas.fiazenify.util.Result
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var isConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unregisterListenerForInternetConnectivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register()
        toLogin()
        listenForInternetConnectivity()
    }

    private fun register() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        binding.apply {
            btnRegister.setOnClickListener {
                etlUsername.isHelperTextEnabled = false
                etlEmail.isHelperTextEnabled = false
                etlPassword.isHelperTextEnabled = false

                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (!isConnected) {
                    binding.tvAlert.isVisible = true
                    binding.tvAlert.text = "Aktifkan Koneksi Internet"
                } else {
                    if (validateData(username, email, password)) {
                        observeRegister(username, email, password)
                    }
                }
            }

        }
    }

    private fun toLogin() {
        binding.tvToLogin.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun observeRegister(
        username: String,
        email: String,
        password: String
    ) {
        viewModel.register(username, email, password)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        binding.tvAlert.isGone = true
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.auth_nav_host, LoginFragment())
                            commit()
                        }

                    }

                    is Result.Error -> {
                        binding.tvAlert.isVisible = true
                        binding.tvAlert.text = "Email sudah terdaftar"
                    }

                }

            }
    }

    private fun validateData(
        username: String,
        email: String,
        password: String,
    ): Boolean {
        return when {
            username.isBlank() -> {
                binding.etlUsername.isHelperTextEnabled = true
                binding.etlUsername.helperText = "Username tidak boleh kosong"
                binding.etlUsername.requestFocus()
                false
            }

            username.length > 20 -> {
                binding.etlUsername.isHelperTextEnabled = true
                binding.etlUsername.helperText = "Username tidak boleh lebih dari 20 karakter"
                binding.etlUsername.requestFocus()
                false
            }

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
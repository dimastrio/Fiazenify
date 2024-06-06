package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetForgotPasswordBinding

@AndroidEntryPoint
class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetForgotPasswordBinding? = null
    private val binding get() = _binding!!

    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetForgotPasswordBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendEmailVerification()
    }

    private fun sendEmailVerification() {
        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString()

            if (email.isBlank()) {
                binding.etlEmail.error = "Wajib diisi"
            } else {
                bottomSheetCallback?.onSendEmail(email)
                binding.etEmail.text?.clear()
            }
        }
    }

    interface BottomSheetCallback {

        fun onSendEmail(email: String)

    }
}
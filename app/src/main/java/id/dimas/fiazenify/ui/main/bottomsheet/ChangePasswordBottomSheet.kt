package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.dimas.fiazenify.databinding.BottomSheetChangePasswordBinding
import id.dimas.fiazenify.ui.main.profile.ProfileViewModel

class ChangePasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    var bottomSheetCallback: NewPassCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetChangePasswordBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changePassword()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changePassword() {
        binding.apply {
            btnSave.setOnClickListener {

                etlOldPassword.isHelperTextEnabled = false
                etlNewPassword.isHelperTextEnabled = false

                val oldPass = etOldPassword.text.toString()
                val newPass = etNewPassword.text.toString()
                if (validateData(oldPass, newPass))
                    bottomSheetCallback?.onChange(oldPass, newPass)
                clearView()
            }
        }
    }

    private fun validateData(oldPassword: String, newPassword: String): Boolean {
        return when {
            oldPassword.isBlank() -> {
                binding.apply {
                    etlOldPassword.helperText = "Password lama tidak boleh kosong"
                    etlOldPassword.isHelperTextEnabled = true
                    etlOldPassword.requestFocus()
                }
                false
            }

            oldPassword.length < 6 -> {
                binding.apply {
                    etlOldPassword.helperText = "Password tidak boleh kurang dari 6 karakter"
                    etlOldPassword.isHelperTextEnabled = true
                    etlOldPassword.requestFocus()
                }
                false
            }

            oldPassword.length > 30 -> {
                binding.etlOldPassword.isHelperTextEnabled = true
                binding.etlOldPassword.helperText = "Password tidak boleh lebih dari 30 karakter"
                binding.etlOldPassword.requestFocus()
                false
            }

            newPassword.isBlank() -> {
                binding.apply {
                    etlNewPassword.helperText = "Password lama tidak boleh kosong"
                    etlNewPassword.isHelperTextEnabled = true
                    etlNewPassword.requestFocus()
                }
                false
            }

            newPassword.length < 6 -> {
                binding.apply {
                    etlNewPassword.helperText = "Password baru tidak boleh kurang dari 6 karakter"
                    etlNewPassword.isHelperTextEnabled = true
                    etlNewPassword.requestFocus()
                }
                false
            }

            newPassword.length > 30 -> {
                binding.etlNewPassword.isHelperTextEnabled = true
                binding.etlNewPassword.helperText =
                    "Password baru tidak boleh lebih dari 30 karakter"
                binding.etlNewPassword.requestFocus()
                false
            }

            else -> true
        }
    }

    private fun clearView() {
        binding.apply {
            etOldPassword.text?.clear()
            etNewPassword.text?.clear()

            etlOldPassword.isHelperTextEnabled = false
            etlNewPassword.isHelperTextEnabled = false

        }
    }

    interface NewPassCallback {
        fun onChange(oldPass: String, newPass: String)
    }
}
package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.dimas.fiazenify.databinding.BottomSheetChangeUsernameBinding

class ChangeUsernameBottomSheet : BottomSheetDialogFragment() {


    private var _binding: BottomSheetChangeUsernameBinding? = null
    private val binding get() = _binding!!

    var bottomSheetCallback: ChangeUsernameCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetChangeUsernameBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeUsername()
    }


    private fun changeUsername() {
        binding.btnSave.setOnClickListener {

            binding.etlNewUsername.isHelperTextEnabled = false
            val newUsername = binding.etNewUsername.text.toString()

            if (validateData(newUsername)) {
                bottomSheetCallback?.onChange(newUsername)
                clearView()
            }

        }
    }

    private fun validateData(username: String): Boolean {
        return when {
            username.isBlank() -> {
                binding.etlNewUsername.helperText = "Username tidak boleh kosong"
                binding.etlNewUsername.isHelperTextEnabled = true
                binding.etlNewUsername.requestFocus()
                false
            }

            username.length > 20 -> {
                binding.etlNewUsername.helperText = "Username tidak boleh lebih dari 20 karakter"
                binding.etlNewUsername.isHelperTextEnabled = true
                binding.etlNewUsername.requestFocus()
                false
            }

            else -> true
        }
    }

    private fun clearView() {
        binding.apply {
            etNewUsername.text?.clear()
            etlNewUsername.isHelperTextEnabled = false

        }
    }

    interface ChangeUsernameCallback {
        fun onChange(newUsername: String)
    }


}
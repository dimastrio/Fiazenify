package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetEmergencyFundBinding
import id.dimas.fiazenify.util.NumberTextWatcher
import id.dimas.fiazenify.util.amountFormat

@AndroidEntryPoint
class EmergencyFundBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEmergencyFundBinding? = null
    private val binding get() = _binding!!

//    private val transactionViewModel: TransactionViewModel by viewModels()

//    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEmergencyFundBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calculateFund()
        formatCurrent()
    }


    private fun calculateFund() {
        binding.btnSave.setOnClickListener {

            binding.etlPersonTotal.isHelperTextEnabled = false
            binding.etlCashOutflow.isHelperTextEnabled = false

            val personTotal = binding.etPersonTotal.text.toString()
            val cashOutflow = binding.etCashOutflow.text.toString().filter { it.isDigit() }

            if (validateData(personTotal, cashOutflow)) {
                val result = (3 * personTotal.toInt()) * cashOutflow.toInt()
                showResult(result)
            }


        }
    }

    private fun validateData(personTotal: String, cashOutFlow: String): Boolean {
        return when {

            personTotal.isBlank() -> {
                binding.apply {
                    etlPersonTotal.helperText = "Jumlah orang tidak boleh kosong"
                    etlPersonTotal.isHelperTextEnabled = true
                    etlPersonTotal.requestFocus()
                }
                false
            }

            personTotal.toInt() > 30 -> {
                binding.apply {
                    etlPersonTotal.helperText = "Jumlah orang tidak boleh lebih dari 30"
                    etlPersonTotal.isHelperTextEnabled = true
                    etlPersonTotal.requestFocus()
                }
                false
            }

            cashOutFlow.isBlank() -> {
                binding.apply {
                    etlCashOutflow.helperText = "Nominal Pengeluaran tidak boleh kosong"
                    etlCashOutflow.isHelperTextEnabled = true
                    etlCashOutflow.requestFocus()
                }
                false
            }

            cashOutFlow.filter { it.isDigit() }.toLong() < 1000000L -> {
                binding.apply {
                    etlCashOutflow.helperText = "Minimal Rp 100.000"
                    etlCashOutflow.isHelperTextEnabled = true
                    etlCashOutflow.requestFocus()
                }
                false
            }

            cashOutFlow.filter { it.isDigit() }.toLong() > 200000000L -> {
                binding.apply {
                    etlCashOutflow.helperText = "Maksimal Rp 200.000.000"
                    etlCashOutflow.isHelperTextEnabled = true
                    etlCashOutflow.requestFocus()
                }
                false
            }


            else -> true
        }
    }

    private fun showResult(result: Int) {
        binding.apply {
            etlPersonTotal.isGone = true
            etlCashOutflow.isGone = true
            btnSave.isGone = true


            tvDescription.isVisible = true
            tvAlertMessage.isVisible = true
            tvResult.isVisible = true
            btnCancel.isVisible = true


            tvResult.text = "Rp. ${amountFormat(result)}"
            btnCancel.setOnClickListener {
                dismiss()
            }

        }
    }

    private fun formatCurrent() {


        val edAmount = binding.etCashOutflow
        edAmount.addTextChangedListener(NumberTextWatcher(edAmount))
//        edAmount.hint = "Rp 0"


    }


}
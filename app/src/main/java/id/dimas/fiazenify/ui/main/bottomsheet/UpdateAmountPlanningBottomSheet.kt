package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetUpdateAmountPlanningBinding
import id.dimas.fiazenify.ui.main.planner.PlanningViewModel
import id.dimas.fiazenify.util.NumberTextWatcher

@AndroidEntryPoint
class UpdateAmountPlanningBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetUpdateAmountPlanningBinding? = null
    private val binding get() = _binding!!

    private val planningViewModel: PlanningViewModel by viewModels()

    var addAmountCallback: AddAmountPlanningCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetUpdateAmountPlanningBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateAmount()
        setupBottomSheet()
        formatCurrent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheet() {
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun updateAmount() {
        binding.btnAdded.setOnClickListener {
            binding.etlPlanningAmount.isHelperTextEnabled = false

            val value = binding.etPlanningAmount.text.toString()
            if (validateData(value)) {
                val amount = value.filter { it.isDigit() }.toInt()
                addAmountCallback?.onIncrease(amount)
                clearView()
            }
        }

        binding.btnReduced.setOnClickListener {
            binding.etlPlanningAmount.isHelperTextEnabled = false

            val value = binding.etPlanningAmount.text.toString()
            if (validateData(value)) {
                val amount = value.filter { it.isDigit() }.toInt()
                addAmountCallback?.onDecrease(amount)
                clearView()
            }
        }
    }

    private fun validateData(amount: String): Boolean {
        return when {
            amount.isBlank() -> {
                binding.apply {
                    etlPlanningAmount.helperText = "Nominal tidak boleh kosong"
                    etlPlanningAmount.isHelperTextEnabled = true
                    etlPlanningAmount.requestFocus()
                }
                false
            }

            amount.filter { it.isDigit() }.toLong() > 2000000000L -> {
                binding.apply {
                    etlPlanningAmount.helperText = "Maksimal Rp. 2.000.000.000"
                    etlPlanningAmount.isHelperTextEnabled = true
                    etlPlanningAmount.requestFocus()
                }
                false
            }

            amount.filter { it.isDigit() }.toLong() < 100L -> {
                binding.apply {
                    etlPlanningAmount.helperText = "Minimal Rp. 100"
                    etlPlanningAmount.isHelperTextEnabled = true
                    etlPlanningAmount.requestFocus()
                }
                false
            }

            else -> true
        }
    }

    private fun clearView() {
        binding.etPlanningAmount.text?.clear()


        binding.etlPlanningAmount.isHelperTextEnabled = false
    }


    private fun formatCurrent() {
//        val value: BigDecimal = NumberTextWatcher().parseCurrencyValue(editText?.text.toString())


        val edAmount = binding.etPlanningAmount
        edAmount.addTextChangedListener(NumberTextWatcher(edAmount))
        edAmount.hint = "Rp 0"

//        edAmount.addTextChangedListener(NumberTextWatcher().MoneyTextWatcher(editText))

    }

    interface AddAmountPlanningCallback {
        fun onIncrease(data: Int)
        fun onDecrease(data: Int)

    }
}
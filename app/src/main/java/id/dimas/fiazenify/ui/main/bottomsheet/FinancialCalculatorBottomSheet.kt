package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetFinancialCalculatorBinding
import id.dimas.fiazenify.ui.main.transaction.TransactionViewModel

@AndroidEntryPoint
class FinancialCalculatorBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFinancialCalculatorBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by viewModels()

    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFinancialCalculatorBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        binding.btnRetirementFund.setOnClickListener {
            dismiss()
            bottomSheetCallback?.onSelectRetirementFund()
        }

        binding.btnEmergencyFund.setOnClickListener {
            dismiss()
            bottomSheetCallback?.onSelectEmergencyFund()
        }
    }

    interface BottomSheetCallback {
        fun onSelectRetirementFund()
        fun onSelectEmergencyFund()
    }
}
package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetAddBinding
import id.dimas.fiazenify.ui.main.planner.AddPlanningFragment
import id.dimas.fiazenify.ui.main.transaction.AddTransactionFragment

@AndroidEntryPoint
class AddBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddBinding? = null
    private val binding get() = _binding!!

    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddBinding.inflate(layoutInflater, container, false)
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
        val addTransaction = AddTransactionFragment()
        val addPlanning = AddPlanningFragment()


        binding.btnTransaction.setOnClickListener {
            bottomSheetCallback?.onSelectTransaction()
//            requireParentFragment().parentFragmentManager.beginTransaction().apply {
//                add(R.id.main_nav_host, addTransaction)
//                addToBackStack(null)
//                commit()
//            }
//            dismiss()
        }

        binding.btnPlanning.setOnClickListener {
            bottomSheetCallback?.onSelectPlanning()
//            requireParentFragment().parentFragmentManager.beginTransaction().apply {
//                add(R.id.main_nav_host, addPlanning)
//                addToBackStack(null)
//                commit()
//            }
//            dismiss()
        }
    }

    private fun onTransactionClicked() {
        binding.btnTransaction.setOnClickListener {
//            bottomSheetCallback?.onSelectTransaction()

        }
    }

    private fun onPlannerClicked() {
        binding.btnPlanning.setOnClickListener {
//            bottomSheetCallback?.onSelectPlanning()
        }
    }

    interface BottomSheetCallback {
        fun onSelectTransaction()
        fun onSelectPlanning()
    }

}
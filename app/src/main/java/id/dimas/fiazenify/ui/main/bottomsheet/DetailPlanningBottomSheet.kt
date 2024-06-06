package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.BottomSheetDetailPlanningBinding
import id.dimas.fiazenify.ui.main.planner.EditPlanningFragment
import id.dimas.fiazenify.ui.main.planner.PlanningViewModel
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status
import id.dimas.fiazenify.util.amountFormat
import id.dimas.fiazenify.util.percentFormat

@AndroidEntryPoint
class DetailPlanningBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDetailPlanningBinding? = null
    private val binding get() = _binding!!

    private val planningViewModel: PlanningViewModel by viewModels()

    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetailPlanningBinding.inflate(layoutInflater, container, false)
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
        val data = arguments?.getString("planningId")
        observePlanningDetail(data!!)

        val editPlanningFragment = EditPlanningFragment()
        val bundle = Bundle()
        bundle.putString("planningId", data)
        editPlanningFragment.arguments = bundle

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnDelete.setOnClickListener { bottomSheetCallback?.onDelete() }
        binding.btnEdit.setOnClickListener {
            requireParentFragment().parentFragmentManager.beginTransaction().apply {
                add(R.id.main_nav_host, editPlanningFragment)
                addToBackStack(null)
                commit()
            }
            dismiss()
        }

    }

    private fun observePlanningDetail(id: String) {
        planningViewModel.getPlanningById(id).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.tvTitle.text = result.data.title
                        binding.tvCurrentAmount.text =
                            "Rp. ${amountFormat(result.data.currentAmount)}"
                        binding.tvPercent.text = percentFormat(result.data.percent)
                        binding.tvTargetAmount.text =
                            "Rp. ${amountFormat(result.data.targetAmount)}"
                        binding.tvNote.text = result.data.note
                    }
                }

                is Result.Error -> {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Terjadi Kesalahan",
                        Status.FAILED
                    )
                }
            }
        }
    }


    interface BottomSheetCallback {
        fun onDelete()
        fun onUpdate()
    }

}
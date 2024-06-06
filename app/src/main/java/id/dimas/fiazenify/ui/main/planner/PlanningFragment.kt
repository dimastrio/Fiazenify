package id.dimas.fiazenify.ui.main.planner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.data.model.Planning
import id.dimas.fiazenify.databinding.FragmentPlanningBinding
import id.dimas.fiazenify.ui.main.adapter.PlanningAdapter
import id.dimas.fiazenify.ui.main.bottomsheet.DetailPlanningBottomSheet
import id.dimas.fiazenify.ui.main.bottomsheet.UpdateAmountPlanningBottomSheet
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status

@AndroidEntryPoint
class PlanningFragment : Fragment() {

    private var _binding: FragmentPlanningBinding? = null
    private val binding get() = _binding!!

    private val planningViewModel: PlanningViewModel by viewModels()

    private val isNavigationVisible: Boolean get() = true

    private val planningAdapter by lazy {
        PlanningAdapter(
            ::onPlannerClicked,
            ::onAddAmountClicked
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlanningBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerview()
        observePlanning()
        setupRefresh()
        setupBottomNavigation()
        addPlanning()

    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            observePlanning()
        }
    }

    fun refresh() {
        observePlanning()
    }


    private fun setupRecyclerview() {
        binding.rvPlanner.adapter = planningAdapter
        binding.rvPlanner.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        observePlanning()
    }

    private fun observePlanning() {
        planningViewModel.getPlanning().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")
//                    binding.swipeRefresh.isRefreshing = false
                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.swipeRefresh.isRefreshing = false
                        planningAdapter.submitList(result.data)
                    }
                }

                is Result.Error -> {

                }


            }
        }
    }

    private fun addPlanning() {
        binding.btnAdd.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                add(R.id.main_nav_host, AddPlanningFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun onPlannerClicked(planning: Planning) {
        val detailPlanningBottomSheet = DetailPlanningBottomSheet()
        val bundle = Bundle()
        bundle.putString("planningId", planning.id)
        detailPlanningBottomSheet.arguments = bundle

        detailPlanningBottomSheet.show(childFragmentManager, null)
        detailPlanningBottomSheet.isCancelable = true
        detailPlanningBottomSheet.bottomSheetCallback =
            object : DetailPlanningBottomSheet.BottomSheetCallback {
                override fun onDelete() {
                    observeDeletePlanning(planning.id)
                    detailPlanningBottomSheet.dismiss()
                }

                override fun onUpdate() {
                    detailPlanningBottomSheet.dismiss()
                }

            }
//
//        parentFragmentManager.beginTransaction().apply {
//            add(R.id.main_nav_host, editPlanningFragment)
//            addToBackStack(null)
//            commit()
//        }


    }

    private fun observeDeletePlanning(id: String) {
        planningViewModel.deletePlanningById(id).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")
                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.swipeRefresh.isRefreshing = false
                        planningAdapter.submitList(emptyList())
                        observePlanning()
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Berhasil menghapus rencana"
                        )

                    }
                }

                is Result.Error -> {
                    Helper.showSnackbar(requireContext(), binding.root, "Gagal menghapus rencana")
                }
            }
        }
    }

    private fun onAddAmountClicked(planning: Planning) {
        val updateAmountPlanning = UpdateAmountPlanningBottomSheet()
        val bundle = Bundle()
        bundle.putString("id", planning.id)
        updateAmountPlanning.arguments = bundle

        updateAmountPlanning.show(childFragmentManager, null)
        updateAmountPlanning.isCancelable = true
        updateAmountPlanning.addAmountCallback =
            object : UpdateAmountPlanningBottomSheet.AddAmountPlanningCallback {
                override fun onIncrease(data: Int) {
                    observeIncreasePlanningAmount(
                        planning.id,
                        planning.currentAmount,
                        planning.percent,
                        planning.targetAmount,
                        data
                    )

                    updateAmountPlanning.dismiss()
                }

                override fun onDecrease(data: Int) {

                    observeDecreasePlanningAmount(
                        planning.id,
                        planning.currentAmount,
                        planning.percent,
                        planning.targetAmount,
                        data
                    )

                    updateAmountPlanning.dismiss()
                }
            }
    }

    private fun observeIncreasePlanningAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ) {
        planningViewModel.increaseCurrentAmount(
            id,
            currentAmount,
            percent,
            targetAmount,
            newCurrentAmount
        )
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        observePlanning()
                        Helper.showSnackbar(requireContext(), binding.root, "Berhasil Tambah Dana")
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

    private fun observeDecreasePlanningAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ) {
        planningViewModel.decreaseCurrentAmount(
            id,
            currentAmount,
            percent,
            targetAmount,
            newCurrentAmount
        )
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        observePlanning()
                        Helper.showSnackbar(requireContext(), binding.root, "Berhasil Kurangi Dana")
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

    private fun setupBottomNavigation() {
        requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar).isVisible =
            isNavigationVisible
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).isVisible =
            isNavigationVisible
    }


}


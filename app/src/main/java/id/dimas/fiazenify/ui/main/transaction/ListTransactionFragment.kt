package id.dimas.fiazenify.ui.main.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.data.model.Transaction
import id.dimas.fiazenify.databinding.FragmentListTransactionBinding
import id.dimas.fiazenify.ui.main.adapter.TransactionAdapter
import id.dimas.fiazenify.ui.main.bottomsheet.DetailTransactionBottomSheet
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class ListTransactionFragment : Fragment() {

    private var _binding: FragmentListTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionAdapter by lazy { TransactionAdapter(::onTransactionClicked) }

    private val transactionViewModel: TransactionViewModel by viewModels()

    private val isNavigationVisible: Boolean get() = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupRefresh()
        setupBottomNavigation()
        setupToolbar()
        onFilterDateClicked()
//        Helper.showToast(requireContext(), "${parentFragmentManager.backStackEntryCount}")

    }


    private fun setupRecyclerView() {
        binding.rvTransaction.adapter = transactionAdapter
        binding.rvTransaction.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        observeTransaction()
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            observeTransaction()
        }
    }

    fun refresh() {
        observeTransaction()
    }

    private fun setupToolbar() {
        binding.toolbar.toolbarTitle.text = "Riwayat Transaksi"
        binding.toolbar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun observeTransaction() {
        transactionViewModel.getTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.swipeRefresh.isRefreshing = false
                        binding.tvDateFilter.isGone = true
                        transactionAdapter.submitList(result.data)
                    }
                }

                is Result.Error -> {

                }


            }
        }
    }

    private fun onTransactionClicked(transaction: Transaction) {
        val detailTransactionBottomSheet = DetailTransactionBottomSheet()
        val bundle = Bundle()
        bundle.putString("transactionId", transaction.id)
        detailTransactionBottomSheet.arguments = bundle

//        observeTransactionDetail(transaction.id)

        detailTransactionBottomSheet.show(childFragmentManager, null)
        detailTransactionBottomSheet.isCancelable = true
        detailTransactionBottomSheet.bottomSheetCallback =
            object : DetailTransactionBottomSheet.BottomSheetCallback {
                override fun onDelete() {
                    observeDeleteTransaction(transaction.id)
                    detailTransactionBottomSheet.dismiss()
                }

                override fun onUpdate() {
                    detailTransactionBottomSheet.dismiss()
                }

            }
    }

    private fun observeDeleteTransaction(id: String) {
        transactionViewModel.deleteTransaction(id).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")

                }

                is Result.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    transactionAdapter.submitList(emptyList())
                    observeTransaction()
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Berhasil menghapus catatan"
                    )
//                    Helper.showToast(requireContext(), "Jumlah item ${transactionAdapter.itemCount}")
                }

                is Result.Error -> {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Gagal Hapus",
                        Status.FAILED
                    )
                }
            }
        }
    }

    private fun onFilterDateClicked() {
        binding.ivDate.setOnClickListener {
//            val dateBottomSheet = DateBottomSheet()
//            dateBottomSheet.show(childFragmentManager, null)
//            dateBottomSheet.dateBottomSheetCallback= object : DateBottomSheet.DateBottomSheetCallback{
//                override fun onSucces(dateStart: String, dateEnd: String) {
//                    observeTransactionByDate(dateStart, dateEnd)
//                    Helper.showToast(requireContext(),"$dateStart dan $dateEnd")
//                }
//            }


            val datePickerRange = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Pilih Tanggal")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()

            datePickerRange.show(childFragmentManager, null)
            datePickerRange.addOnPositiveButtonClickListener {
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateStart = "${simpleDateFormat.format(it.first)} 00:00:00"
                val dateEnd = "${simpleDateFormat.format(it.second)} 23:59:00"
                observeTransactionByDate(dateStart, dateEnd)
                binding.tvDateFilter.text =
                    "${simpleDateFormat.format(it.first)} s.d. ${simpleDateFormat.format(it.second)}"
            }
        }
    }

    private fun observeTransactionByDate(dateStart: String, dateEnd: String) {
        transactionViewModel.getTransactionByDate(dateStart, dateEnd)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.swipeRefresh.isRefreshing = true
                    }

                    is Result.Success -> {
                        if (result.data != null) {
                            binding.tvDateFilter.isVisible = true
                            binding.swipeRefresh.isRefreshing = false
                            transactionAdapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {

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
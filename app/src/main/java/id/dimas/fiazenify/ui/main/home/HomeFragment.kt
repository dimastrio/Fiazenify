package id.dimas.fiazenify.ui.main.home

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
import id.dimas.fiazenify.data.model.Transaction
import id.dimas.fiazenify.databinding.FragmentHomeBinding
import id.dimas.fiazenify.ui.auth.AuthViewModel
import id.dimas.fiazenify.ui.main.adapter.TransactionAdapter
import id.dimas.fiazenify.ui.main.bottomsheet.DetailTransactionBottomSheet
import id.dimas.fiazenify.ui.main.transaction.ListTransactionFragment
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status
import id.dimas.fiazenify.util.amountFormat

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val transactionAdapter by lazy { TransactionAdapter(::onTransactionClicked) }

    private val transactions = mutableListOf<Transaction>()

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val isNavigationVisible: Boolean get() = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTransaction()
        setupBottomNavigation()
        setupRefresh()
        getUsername()
        toListTransaction()
        observeAppBarTransaction()
    }

    override fun onStart() {
        super.onStart()
        observeAppBarTransaction()
    }

    private fun getUsername() {
        authViewModel.getUsername().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.profile.tvUsername.text = "Hai! ${result.data.displayName}"
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }

    private fun setupRefresh() {
        binding.appBar.addOnOffsetChangedListener { _, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset == 0
        }

        binding.swipeRefresh.setOnRefreshListener {
            observeTransaction()
            observeAppBarTransaction()
            getUsername()
        }
    }

    fun refresh() {
        getUsername()
        observeTransaction()
        observeAppBarTransaction()
    }


    private fun setupRecyclerView() {
        binding.rvTransaction.adapter = transactionAdapter
        binding.rvTransaction.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        observeTransaction()
        observeAppBarTransaction()
    }

    private fun toListTransaction() {
        binding.tvAllTransaction.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                add(R.id.main_nav_host, ListTransactionFragment())
                addToBackStack(null)
                commit()
            }
        }
    }


    private fun observeTransaction() {
        homeViewModel.getTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.swipeRefresh.isRefreshing = false
                        transactionAdapter.submitList(result.data)
                    }

                }

                is Result.Error -> {
//                    if (result.message != null){
//                        if ("host" in result.message || "connect" in result.message) {
//                            Helper.showToast(requireContext(), "Periksa koneksi Anda")
//                        } else {
//                            Helper.showToast(requireContext(), result.message)
//                        }
//                    }

                    if (result.data != null) {
                        binding.swipeRefresh.isRefreshing = false
//                        transactionAdapter.submitList(result.data)
                    }
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

//        parentFragmentManager.beginTransaction().apply {
//            add(R.id.main_nav_host, detailTransactionFragment)
//            addToBackStack(null)
//            commit()
//        }

    }

//    private fun onAddTransactionClicked(){
//        val addTransactionFragment = AddTransactionFragment()
//
//        addTransactionFragment.addTransactionCallback = object : AddTransactionFragment.AddTransactionCallback{
//            override fun onSucces() {
//                Helper.showSnackbar(requireContext(),binding.root,"Transaksi berhasil dicatat")
//            }
//
//            override fun onFailed() {
//                Helper.showSnackbar(requireContext(), binding.root, "Transaksi gagal dicatat")
//            }
//
//        }
//    }

    private fun observeDeleteTransaction(id: String) {
        homeViewModel.deleteTransaction(id).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")

                }

                is Result.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    transactionAdapter.submitList(emptyList())
                    observeTransaction()
                    observeAppBarTransaction()
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

    private fun setupBottomNavigation() {
        requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar).isVisible =
            isNavigationVisible
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).isVisible =
            isNavigationVisible
    }


    private fun observeAppBarTransaction() {
        homeViewModel.getTransactionIn().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")
                }

                is Result.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    if (result.data != null) {
//                        val formatCurrency = NumberFormat.getCurrencyInstance(Locale("in","ID")).format(result.data)
//                        binding.budget.tvIncome.text = formatCurrency.substringBefore(",")
                        binding.budget.tvIncome.text = "Rp. ${amountFormat(result.data)}"
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

        homeViewModel.getTransactionOut().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")
                }

                is Result.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    if (result.data != null) {
//                        val formatCurrency = NumberFormat.getCurrencyInstance(Locale("in","ID")).format(result.data)
//                        binding.budget.tvOutcome.text = formatCurrency.substringBefore(",")
                        binding.budget.tvOutcome.text = "Rp. ${amountFormat(result.data)}"
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

        homeViewModel.getTransactionTotal().observe(viewLifecycleOwner) { result ->

            when (result) {
                is Result.Loading -> {
//                    Helper.showToast(requireContext(), "Loading")
                }

                is Result.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    if (result.data != null) {
//                        val formatCurrency = NumberFormat.getCurrencyInstance(Locale("in","ID")).format(result.data)
//                        binding.budget.tvBalance.text = formatCurrency.substringBefore(",")
                        binding.budget.tvBalance.text = "Rp. ${amountFormat(result.data)}"
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

        homeViewModel.getMonthTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.budget.tvMonth.text = result.data
                    }
                }

                is Result.Error -> {

                }
            }
        }


    }

}
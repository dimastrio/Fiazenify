package id.dimas.fiazenify.ui.main.transaction

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.data.model.Category
import id.dimas.fiazenify.databinding.FragmentEditTransactionBinding
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.NumberTextWatcher
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status

@AndroidEntryPoint
class EditTransactionFragment : Fragment() {

    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by viewModels()

    private var categoryNames: String = ""
    private var type: String = ""
    private var docId: String = ""
    private val isNavigationVisible: Boolean get() = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        observeCategory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateTransaction()
        setupBottomNavigation()
        setupEditTransaction()
        setupToolbar()
        formatCurrent()
//        Helper.showToast(requireContext(), "${parentFragmentManager.backStackEntryCount}")

    }

    private fun setupToolbar() {
        binding.toolbar.toolbarTitle.text = "Ubah Catatan Transaksi"
        binding.toolbar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
            clearView()
        }
    }

    private fun updateTransaction() {
        binding.content.apply {
            binding.btnSave.setOnClickListener {

                etlTransactionTitle.isHelperTextEnabled = false
                etlTransactionCategory.isHelperTextEnabled = false
                etlTransactionAmount.isHelperTextEnabled = false
                etlTransactionNote.isHelperTextEnabled = false

                val transactionId = arguments?.getString("id")
                val title = etTransactionTitle.text.toString()
                val amount = etTransactionAmount.text.toString()
                val note = etTransactionNote.text.toString()
                val category = etTransactionCategory.text.toString()
                tvAlertType.isGone = true

                type = if (rbIn.isChecked) {
                    "Pemasukan"
                } else if (rbOut.isChecked) {
                    "Pengeluaran"
                } else {
                    tvAlertType.isVisible = true
                    return@setOnClickListener
                }

                if (validateData(title, category, amount, note)) {
                    observeUpdateTransaction(
                        id = transactionId!!,
                        title = title,
                        type = type,
                        category = categoryNames,
                        amount = amount.filter { it.isDigit() }.toInt(),
                        note = note
                    )
                }
            }
        }
    }

    private fun observeUpdateTransaction(
        id: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String
    ) {
        transactionViewModel.updateTransaction(id, title, type, category, amount, note)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        clearView()
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Berhasil Mengubah Transaksi"
                        )

                        requireActivity().onBackPressed()
                    }

                    is Result.Error -> {

                    }

                }
            }
    }


    private fun setupEditTransaction() {
        val transactionId = arguments?.getString("id")

        transactionViewModel.getTransactionById(transactionId!!)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        if (result.data != null) {

//                            when (result.data.type) {
//                                "IN" -> {
//                                    type = "IN"
//                                    binding.content.rgType.check(binding.content.rgType.getChildAt(1).id)
//                                }
//                                "OUT" -> {
//                                    type = "OUT"
//                                    binding.content.rgType.check(binding.content.rgType.getChildAt(2).id)
//                                }
//                            }

                            if (result.data.type == "Pemasukan") {
                                type = "Pemasukan"
                                binding.content.rbIn.isChecked = true
                            } else if (result.data.type == "Pengeluaran") {
                                type = "Pengeluaran"
                                binding.content.rbOut.isChecked = true
                            }

                            categoryNames = result.data.category

                            binding.content.apply {
                                etTransactionTitle.setText(result.data.title)
                                etTransactionCategory.setText(result.data.category)
                                etTransactionAmount.setText(result.data.amount.toString())
                                etTransactionNote.setText(result.data.note)
                            }
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

    private fun setupCategory(category: List<Category>) {
        val list: ArrayList<String?> = arrayListOf()

        for (i in category.map { it.name }) {
            list.add(i)
        }

        val categoryAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_category,
            list
        )

        binding.content.etTransactionCategory.apply {
            setAdapter(categoryAdapter)
            setOnClickListener {
                if (!TextUtils.isEmpty(this.text.toString())) {
                    categoryAdapter.filter.filter(null)
                }
            }
            setOnItemClickListener { adapter, _, position, _ ->

                val categoryName = adapter.getItemAtPosition(position) as String

                categoryNames = categoryName
//                setText(categoryName)


            }

        }
    }

    private fun observeCategory() {
        transactionViewModel.getCategory().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        setupCategory(result.data)
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

    private fun validateData(
        title: String,
        category: String,
        amount: String,
        note: String
    ): Boolean {
        return when {
            title.isBlank() -> {
                binding.content.apply {
                    etlTransactionTitle.helperText = "Judul tidak boleh kosong"
                    etlTransactionTitle.isHelperTextEnabled = true
                    etlTransactionTitle.requestFocus()
                }
                false
            }

            title.length > 50 -> {
                binding.content.apply {
                    etlTransactionTitle.helperText = "Judul maksimal 50 karakter"
                    etlTransactionTitle.isHelperTextEnabled = true
                    etlTransactionTitle.requestFocus()
                }
                false
            }

            category.isBlank() -> {
                binding.content.apply {
                    etlTransactionCategory.helperText = "Kategori tidak boleh kosong"
                    etlTransactionCategory.isHelperTextEnabled = true
                    etlTransactionCategory.requestFocus()
                }
                false
            }

            amount.isBlank() -> {
                binding.content.apply {
                    etlTransactionAmount.helperText = "Nominal tidak boleh kosong"
                    etlTransactionAmount.isHelperTextEnabled = true
                    etlTransactionAmount.requestFocus()
                }
                false
            }

            amount.filter { it.isDigit() }.toLong() > 2000000000L -> {
                binding.content.apply {
                    etlTransactionAmount.helperText = "Maksimal Rp. 2.000.000.000"
                    etlTransactionAmount.isHelperTextEnabled = true
                    etlTransactionAmount.requestFocus()
                }
                false
            }

            amount.filter { it.isDigit() }.toLong() < 100L -> {
                binding.content.apply {
                    etlTransactionAmount.helperText = "Minimal Rp. 100"
                    etlTransactionAmount.isHelperTextEnabled = true
                    etlTransactionAmount.requestFocus()
                }
                false
            }

            note.isBlank() -> {
                binding.content.apply {
                    etlTransactionNote.helperText = "Catatan tidak boleh kosong"
                    etlTransactionNote.isHelperTextEnabled = true
                    etlTransactionNote.requestFocus()
                }
                false
            }

            note.length > 70 -> {
                binding.content.apply {
                    etlTransactionNote.helperText = "Catatan maksimal 70 karakter"
                    etlTransactionNote.isHelperTextEnabled = true
                    etlTransactionNote.requestFocus()
                }
                false
            }

            else -> true
        }
    }

    private fun clearView() {
        binding.content.apply {
            etTransactionTitle.text?.clear()
            rgType.clearCheck()
            etTransactionCategory.text?.clear()
            etTransactionAmount.text?.clear()
            etTransactionNote.text?.clear()

            etlTransactionTitle.isHelperTextEnabled = false
            etlTransactionCategory.isHelperTextEnabled = false
            etlTransactionAmount.isHelperTextEnabled = false
            etlTransactionNote.isHelperTextEnabled = false
            binding.content.tvAlertType.isGone = true
        }
    }

    private fun formatCurrent() {
//        val value: BigDecimal = NumberTextWatcher().parseCurrencyValue(editText?.text.toString())


        val edAmount = binding.content.etTransactionAmount
        edAmount.addTextChangedListener(NumberTextWatcher(edAmount))
        edAmount.hint = "Rp 0"

//        edAmount.addTextChangedListener(NumberTextWatcher().MoneyTextWatcher(editText))

    }


}
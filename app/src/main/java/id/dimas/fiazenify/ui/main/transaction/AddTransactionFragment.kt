package id.dimas.fiazenify.ui.main.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.data.model.Category
import id.dimas.fiazenify.databinding.FragmentAddTransactionBinding
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.NumberTextWatcher
import id.dimas.fiazenify.util.Result


@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels()

    private val created: Timestamp = Timestamp.now()

    private val uid = ""
    private val docId = ""

    private var categoryNames: String = ""
    private var type: String = ""

    var addTransactionCallback: AddTransactionCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCategory()
        addTransaction()
        setupToolbar()
        formatCurrent()

    }

    private fun setupToolbar() {
        binding.toolbar.toolbarTitle.text = "Tambah Catatan Transaksi"
        binding.toolbar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
            clearView()
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
            setOnItemClickListener { adapter, _, position, _ ->
                val categoryName = adapter.getItemAtPosition(position) as String

                categoryNames = categoryName
//                setText(categoryName)

            }

        }
    }

    private fun addTransaction() {
        binding.content.apply {
            binding.btnSave.setOnClickListener {

                etlTransactionTitle.isHelperTextEnabled = false
                etlTransactionCategory.isHelperTextEnabled = false
                etlTransactionAmount.isHelperTextEnabled = false
                etlTransactionNote.isHelperTextEnabled = false

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
                    observeAddTransaction(
                        id = docId,
                        uid = uid,
                        title = title,
                        type = type,
                        category = categoryNames,
                        amount = amount.filter { it.isDigit() }.toInt(),
                        note = note,
                        created = created
                    )
                }
            }
        }

    }


    private fun observeAddTransaction(
        id: String,
        uid: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String,
        created: Timestamp?
    ) {
        viewModel.addTransaction(id, uid, title, type, category, amount, note, created!!)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
//                        addTransactionCallback?.onSucces()
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Transaksi berhasil dicatat"
                        )
//                        Helper.showToast(requireContext(), "Berhasil Menambahkan")
//                        addTransactionCallback?.onSucces()
                        requireActivity().onBackPressed()
                        clearView()
                    }

                    is Result.Error -> {
                        addTransactionCallback?.onFailed()
                        requireActivity().onBackPressed()
                    }
                }
            }
    }


    private fun observeCategory() {
        viewModel.getCategory().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        setupCategory(result.data)
                    }
                }

                is Result.Error -> {
                    if (result.data != null) {
                        setupCategory(result.data)
                    }
                }
            }
        }
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

    fun clearView() {
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

    interface AddTransactionCallback {

        fun onSucces()
        fun onFailed()
    }


}
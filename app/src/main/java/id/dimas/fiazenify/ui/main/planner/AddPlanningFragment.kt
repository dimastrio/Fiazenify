package id.dimas.fiazenify.ui.main.planner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.FragmentAddPlanningBinding
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.NumberTextWatcher
import id.dimas.fiazenify.util.Result

@AndroidEntryPoint
class AddPlanningFragment : Fragment() {

    private var _binding: FragmentAddPlanningBinding? = null
    private val binding get() = _binding!!

    private val planningViewModel: PlanningViewModel by viewModels()

    private val uid = ""
    private val docId = ""
    private val percent = 0.0
    private val currentAmount = 0

    private val isNavigationVisible: Boolean get() = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddPlanningBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        addPlanning()
        formatCurrent()
        setupBottomNavigation()
    }


    private fun setupToolbar() {
        binding.toolbar.toolbarTitle.text = "Tambah Rencana"
        binding.toolbar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
            clearView()
        }
    }

    private fun addPlanning() {
        binding.content.apply {
            binding.btnSave.setOnClickListener {

                etlPlanningTitle.isHelperTextEnabled = false
                etlPlanningAmount.isHelperTextEnabled = false
                etlPlanningNote.isHelperTextEnabled = false

                val title = etPlanningTitle.text.toString()
                val targetAmount = etPlanningAmount.text.toString()
                val note = etPlanningNote.text.toString()

                if (validateData(title, targetAmount, note)) {
                    observeAddPlanning(
                        id = docId,
                        uid = uid,
                        title = title,
                        currentAmount = currentAmount,
                        percent = percent,
                        targetAmount = targetAmount.filter { it.isDigit() }.toInt(),
                        note = note
                    )
                }

            }
        }
    }

    private fun observeAddPlanning(
        id: String,
        uid: String,
        title: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        note: String
    ) {
        planningViewModel.addPlanning(id, uid, title, currentAmount, percent, targetAmount, note)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        Helper.showSnackbar(
                            requireContext(),
                            binding.root,
                            "Berhasil menambah rencana"
                        )
                        requireActivity().onBackPressed()
                        clearView()
                    }

                    is Result.Error -> {

                    }


                }
            }
    }

    private fun validateData(
        title: String,
        amount: String,
        note: String
    ): Boolean {
        return when {
            title.isBlank() -> {
                binding.content.apply {
                    etlPlanningTitle.helperText = "Judul tidak boleh kosong"
                    etlPlanningTitle.isHelperTextEnabled = true
                    etlPlanningTitle.requestFocus()
                }
                false
            }

            title.length > 50 -> {
                binding.content.apply {
                    etlPlanningTitle.helperText = "Judul maksimal 50 karakter"
                    etlPlanningTitle.isHelperTextEnabled = true
                    etlPlanningTitle.requestFocus()
                }
                false
            }

            amount.isBlank() -> {
                binding.content.apply {
                    etlPlanningAmount.helperText = "Harga tidak boleh kosong"
                    etlPlanningAmount.isHelperTextEnabled = true
                    etlPlanningAmount.requestFocus()
                }
                false
            }

            amount.filter { it.isDigit() }.toLong() > 2000000000L -> {
                binding.content.apply {
                    etlPlanningAmount.helperText = "Maksimal Rp. 2.000.000.000"
                    etlPlanningAmount.isHelperTextEnabled = true
                    etlPlanningAmount.requestFocus()
                }
                false
            }

            amount.filter { it.isDigit() }.toLong() < 100L -> {
                binding.content.apply {
                    etlPlanningAmount.helperText = "Minimal Rp. 100"
                    etlPlanningAmount.isHelperTextEnabled = true
                    etlPlanningAmount.requestFocus()
                }
                false
            }

            note.isBlank() -> {
                binding.content.apply {
                    etlPlanningNote.helperText = "Catatan tidak boleh kosong"
                    etlPlanningNote.isHelperTextEnabled = true
                    etlPlanningNote.requestFocus()
                }
                false
            }

            note.length > 70 -> {
                binding.content.apply {
                    etlPlanningNote.helperText = "Catatan maksimal 70 karakter"
                    etlPlanningNote.isHelperTextEnabled = true
                    etlPlanningNote.requestFocus()
                }
                false
            }

            else -> true
        }
    }

    private fun formatCurrent() {
//        val value: BigDecimal = NumberTextWatcher().parseCurrencyValue(editText?.text.toString())


        val edAmount = binding.content.etPlanningAmount
        edAmount.addTextChangedListener(NumberTextWatcher(edAmount))
        edAmount.hint = "Rp 0"

//        edAmount.addTextChangedListener(NumberTextWatcher().MoneyTextWatcher(editText))

    }

    private fun setupBottomNavigation() {
        requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar).isVisible =
            isNavigationVisible
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).isVisible =
            isNavigationVisible
    }

    private fun clearView() {
        binding.content.apply {
            etPlanningTitle.text?.clear()
            etPlanningAmount.text?.clear()
            etPlanningNote.text?.clear()

            etlPlanningTitle.isHelperTextEnabled = false
            etlPlanningAmount.isHelperTextEnabled = false
            etlPlanningNote.isHelperTextEnabled = false


        }
    }

}
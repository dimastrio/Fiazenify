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
import id.dimas.fiazenify.databinding.FragmentEditPlanningBinding
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.NumberTextWatcher
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status

@AndroidEntryPoint
class EditPlanningFragment : Fragment() {

    private var _binding: FragmentEditPlanningBinding? = null
    private val binding get() = _binding!!

    private val planningViewModel: PlanningViewModel by viewModels()

    private val isNavigationVisible: Boolean get() = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditPlanningBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNavigation()
        setupEditPlanning()
        updatePlanning()
        setupToolbar()
        formatCurrent()
    }

    private fun setupToolbar() {
        binding.toolbar.toolbarTitle.text = "Ubah Rencana"
        binding.toolbar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
            clearView()
        }
    }


    private fun setupEditPlanning() {
        val planningId = arguments?.getString("planningId")

        planningViewModel.getPlanningById(planningId!!).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.content.apply {
                            etPlanningTitle.setText(result.data.title)
                            etPlanningAmount.setText(result.data.targetAmount.toString())
                            etPlanningNote.setText(result.data.note)

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

    private fun updatePlanning() {
        binding.content.apply {
            binding.btnSave.setOnClickListener {

                etlPlanningTitle.isHelperTextEnabled = false
                etlPlanningAmount.isHelperTextEnabled = false
                etlPlanningNote.isHelperTextEnabled = false

                val planningId = arguments?.getString("planningId")
                val title = etPlanningTitle.text.toString()
                val targetAmount = etPlanningAmount.text.toString()
                val note = etPlanningNote.text.toString()

                if (validateData(title, targetAmount, note)) {
                    observeUpdatePlanning(
                        id = planningId!!,
                        title = title,
                        targetAmount = targetAmount.filter { it.isDigit() }.toInt(),
                        note = note
                    )
                }
            }
        }
    }

    private fun observeUpdatePlanning(id: String, title: String, targetAmount: Int, note: String) {
        planningViewModel.updatePlanning(id, title, targetAmount, note)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        clearView()
                        Helper.showSnackbar(requireContext(), binding.root, "Berhasil Mengubah")
                        requireActivity().onBackPressed()
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

    private fun formatCurrent() {
//        val value: BigDecimal = NumberTextWatcher().parseCurrencyValue(editText?.text.toString())


        val edAmount = binding.content.etPlanningAmount
        edAmount.addTextChangedListener(NumberTextWatcher(edAmount))
        edAmount.hint = "Rp 0"

//        edAmount.addTextChangedListener(NumberTextWatcher().MoneyTextWatcher(editText))

    }

}
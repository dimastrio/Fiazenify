package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.BottomSheetRetirementFundBinding
import id.dimas.fiazenify.util.NumberTextWatcher
import id.dimas.fiazenify.util.currencyFormat
import kotlin.math.pow

class RetirementFundBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRetirementFundBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRetirementFundBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calculateRetirementFund()
        formatCurrent()
    }

    private fun onClickCalculate() {

    }

    private fun calculateRetirementFund() {
        binding.btnSave.setOnClickListener {
            val ageNow = binding.etAgeNow.text.toString().toInt()
            val ageRetirement = binding.etAgeRetirement.text.toString().toInt()
            val returnInvest = binding.etReturnInvest.text.toString().toDouble()
            val monthlyExpense =
                binding.etMonthlyExpense.text.toString().filter { it.isDigit() }.toInt()
            val lifeExpectancy = binding.etLifeExpectancy.text.toString().toInt()
            val inflationRate = 0.04 // inflasi


            val annualExpense =
                (monthlyExpense * 12) * (1 + inflationRate).pow(ageRetirement - ageNow) // pengeluaran tahunan setelah pensiun

            val yearsOfRetirement = lifeExpectancy - ageRetirement


            val adjustedReturnRate = (1 + (returnInvest * 0.01)) / (1 + inflationRate) - 1 //
            val presentValueFactor =
                ((1 - (1 + adjustedReturnRate).pow(-yearsOfRetirement)) / adjustedReturnRate) * (1 + adjustedReturnRate)

            val retirementFund = annualExpense * presentValueFactor

            val monthlyPayment = retirementFund / ((ageRetirement - ageNow) * 12)

            if (validateData(
                    ageNow.toString(),
                    ageRetirement.toString(),
                    lifeExpectancy.toString(),
                    returnInvest.toString(),
                    monthlyExpense.toString()
                )
            ) {
            }

            showResult(
                retirementFund,
                ageRetirement,
                lifeExpectancy,
                returnInvest.toInt(),
                monthlyPayment
            )
//            Helper.showToast(requireContext(), "${currencyFormat(monthlySa)}")

            binding.etAgeNow.text?.clear()
            binding.etAgeRetirement.text?.clear()
            binding.etReturnInvest.text?.clear()
            binding.etMonthlyExpense.text?.clear()
            binding.etLifeExpectancy.text?.clear()


        }
    }

    private fun validateData(
        ageNow: String,
        ageRetirement: String,
        lifeExpectancy: String,
        returnInvest: String,
        monthlyExpense: String
    ): Boolean {
        return when {

            ageNow.isBlank() -> {
                binding.apply {
                    etlAgeNow.helperText = "Tidak boleh kosong"
                    etlAgeNow.isHelperTextEnabled = true
                    etlAgeNow.requestFocus()
                }
                false
            }

            ageNow.length > 50L -> {
                binding.apply {
                    etlAgeNow.helperText = "Maksimal 50"
                    etlAgeNow.isHelperTextEnabled = true
                    etlAgeNow.requestFocus()
                }
                false
            }

            ageRetirement.isBlank() -> {
                binding.apply {
                    etlAgeRetirement.helperText = "Tidak boleh kosong"
                    etlAgeRetirement.isHelperTextEnabled = true
                    etlAgeRetirement.requestFocus()
                }
                false
            }

            ageRetirement.length > 100L -> {
                binding.apply {
                    etlAgeRetirement.helperText = "Maksimal 100"
                    etlAgeRetirement.isHelperTextEnabled = true
                    etlAgeRetirement.requestFocus()
                }
                false
            }

            lifeExpectancy.isBlank() -> {
                binding.apply {
                    etlLifeExpectancy.helperText = "Tidak boleh kosong"
                    etlLifeExpectancy.isHelperTextEnabled = true
                    etlLifeExpectancy.requestFocus()
                }
                false
            }

            lifeExpectancy.length > 50L -> {
                binding.apply {
                    etlLifeExpectancy.helperText = "Maksimal 50"
                    etlLifeExpectancy.isHelperTextEnabled = true
                    etlLifeExpectancy.requestFocus()
                }
                false
            }

            returnInvest.isBlank() -> {
                binding.apply {
                    etlReturnInvest.helperText = "Tidak boleh kosong"
                    etlReturnInvest.isHelperTextEnabled = true
                    etlReturnInvest.requestFocus()
                }
                false
            }

            returnInvest.length > 200L -> {
                binding.apply {
                    etlReturnInvest.helperText = "Maksimal 200"
                    etlReturnInvest.isHelperTextEnabled = true
                    etlReturnInvest.requestFocus()
                }
                false
            }

            monthlyExpense.isBlank() -> {
                binding.apply {
                    etlMonthlyExpense.helperText = "Tidak boleh kosong"
                    etlMonthlyExpense.isHelperTextEnabled = true
                    etlMonthlyExpense.requestFocus()
                }
                false
            }

            monthlyExpense.filter { it.isDigit() }.toLong() < 100000L -> {
                binding.apply {
                    etlMonthlyExpense.helperText = "Minimal Rp. 100.000"
                    etlMonthlyExpense.isHelperTextEnabled = true
                    etlMonthlyExpense.requestFocus()
                }
                false
            }

            monthlyExpense.filter { it.isDigit() }.toLong() > 200000000L -> {
                binding.apply {
                    etlMonthlyExpense.helperText = "Maksimal Rp 200.000.000"
                    etlMonthlyExpense.isHelperTextEnabled = true
                    etlMonthlyExpense.requestFocus()
                }
                false
            }

            else -> true
        }
    }

    private fun showResult(
        retirementFund: Double,
        ageRetirement: Int,
        lifeExpectancy: Int,
        returnInvest: Int,
        monthlySaving: Double
    ) {
        binding.apply {
            etlAgeNow.isGone = true
            etlAgeRetirement.isGone = true
            etlLifeExpectancy.isGone = true
            etlReturnInvest.isGone = true
            etlMonthlyExpense.isGone = true
            tvPercent.isGone = true
            btnSave.isGone = true

            tvMessage1.isVisible = true
            tvMessage2.isVisible = true
            tvMessage3.isVisible = true
            tvMessage4.isVisible = true
            tvResult.isVisible = true
            tvResult2.isVisible = true
            btnCancel.isVisible = true

            tvResult.text = "Rp. ${currencyFormat(retirementFund)}"
            tvMessage1.text =
                getString(
                    R.string.retirement_message1,
                    "$ageRetirement",
                    "$lifeExpectancy",
                    "$returnInvest"
                )
            tvResult2.text = "Rp. ${currencyFormat(monthlySaving)}"
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }


    private fun formatCurrent() {


        val edAmount = binding.etMonthlyExpense
        edAmount.addTextChangedListener(NumberTextWatcher(edAmount))
//        edAmount.hint = "Rp 0"


    }
}
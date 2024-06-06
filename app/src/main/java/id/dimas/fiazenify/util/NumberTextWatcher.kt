package id.dimas.fiazenify.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import java.util.Objects

class NumberTextWatcher(editText: EditText) : TextWatcher {

    private val locale: Locale = Locale("id", "ID")
    private val formatter: DecimalFormat = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    private var editTextWeakReference: WeakReference<EditText>? = null

    init {
        editTextWeakReference = WeakReference(editText)
        formatter.maximumFractionDigits = 0
        formatter.roundingMode = RoundingMode.FLOOR
        val symbol = DecimalFormatSymbols(locale)
        symbol.currencySymbol = symbol.currencySymbol + " "
        formatter.decimalFormatSymbols = symbol
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        val edText: EditText? = editTextWeakReference?.get()
        if (edText == null || edText.text.toString().isEmpty()) {
            return
        }
        edText.removeTextChangedListener(this)

        val parsed: BigDecimal = parseCurrencyValue(edText.text.toString())
        val formatted: String = formatter.format(parsed)

        edText.setText(formatted)
        edText.setSelection(formatted.length)
        edText.addTextChangedListener(this)

    }

    private fun parseCurrencyValue(value: String): BigDecimal {
        try {
            val replaceRegex = java.lang.String.format(
                "[%s,.\\s]",
                Objects.requireNonNull(formatter.currency).getSymbol(locale)
            )
            var currencyValue = value.replace(replaceRegex.toRegex(), "")
            currencyValue = if ("" == currencyValue) "0" else currencyValue
            return BigDecimal(currencyValue)
        } catch (e: Exception) {
            Log.e("App", e.message, e)
        }
        return BigDecimal.ZERO
    }


}
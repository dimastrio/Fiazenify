package id.dimas.fiazenify.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import id.dimas.fiazenify.R

object Helper {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun formatCurrent(editText: EditText) {
//        val value: BigDecimal = NumberTextWatcher().parseCurrencyValue(editText?.text.toString())


        editText.addTextChangedListener(NumberTextWatcher(editText))
        editText.hint = "Rp 0"

//        edAmount.addTextChangedListener(NumberTextWatcher().MoneyTextWatcher(editText))

    }

    @SuppressLint("RestrictedApi")
    fun showSnackbar(context: Context, view: View, text: String, status: String = Status.SUCCESS) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        val customSnackView = LayoutInflater.from(context).inflate(R.layout.layout_snackbar, null)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams

        params.gravity = Gravity.TOP
        params.setMargins(20, 100, 20, 0)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE


        snackbar.setAnchorView(R.id.fab)

        snackbarView.layoutParams = params
        snackbarView.setBackgroundColor(Color.TRANSPARENT)

        val snackbarLayout = snackbarView as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)

        val card = customSnackView.findViewById<MaterialCardView>(R.id.cv_snackbar)
        val message = customSnackView.findViewById<MaterialTextView>(R.id.tv_message)
        val btnClose = customSnackView.findViewById<ShapeableImageView>(R.id.btn_close)

        when (status) {
            Status.SUCCESS -> card.setCardBackgroundColor(context.getColor(R.color.colorBgSuccess))
            Status.FAILED -> card.setCardBackgroundColor(context.getColor(R.color.colorBgFailed))
        }

        message.text = text
        btnClose.setOnClickListener {
            snackbar.dismiss()
        }

        snackbarLayout.addView(customSnackView, 0)
        snackbar.show()
    }

    @SuppressLint("RestrictedApi")
    fun showSnackbarAuth(
        context: Context,
        view: View,
        text: String,
        status: String = Status.SUCCESS
    ) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        val customSnackView = LayoutInflater.from(context).inflate(R.layout.layout_snackbar, null)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams

        params.gravity = Gravity.TOP
        params.setMargins(20, 100, 20, 0)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE

        snackbarView.layoutParams = params
        snackbarView.setBackgroundColor(Color.TRANSPARENT)

        val snackbarLayout = snackbarView as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)

        val card = customSnackView.findViewById<MaterialCardView>(R.id.cv_snackbar)
        val message = customSnackView.findViewById<MaterialTextView>(R.id.tv_message)
        val btnClose = customSnackView.findViewById<ShapeableImageView>(R.id.btn_close)

        when (status) {
            Status.SUCCESS -> card.setCardBackgroundColor(context.getColor(R.color.colorBgSuccess))
            Status.FAILED -> card.setCardBackgroundColor(context.getColor(R.color.colorBgFailed))
        }

        message.text = text
        btnClose.setOnClickListener {
            snackbar.dismiss()
        }

        snackbarLayout.addView(customSnackView, 0)
        snackbar.show()
    }


//    fun stringToTimestamp(string: String?): Date? {
//
//        return if (string != null){
//            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
//            val date = Date()
//
//            dateFormat.parse(string)
//        }else null
//
////        return dateFormat.format(date)
//    }


}
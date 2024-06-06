package id.dimas.fiazenify.util

import com.google.firebase.Timestamp
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun timestampToString(timestamp: Timestamp?): String? {
    return run {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
        timestamp?.toDate()?.let { dateFormat.format(it) }
    }
}

fun convertTimestampToString(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = Date(timestamp)
    return dateFormat.format(date)
}

fun convertStringToTimestamp(dateString: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.parse(dateString)
    return date.time
}

fun stringToTimestamp(string: String?): Date? {
    return if (string != null) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
        dateFormat.parse(string)
    } else null
}

fun amountFormat(number: Int): String {
    val numberFormat: NumberFormat = DecimalFormat("#,###")
    return numberFormat.format(number)
}

fun amountFormat(number: Long): String {
    val numberFormat: NumberFormat = DecimalFormat("#,###")
    return numberFormat.format(number)
}

fun currencyFormat(number: Double): String {
//    val country = "ID"
//    val language = "id"
    val numberFormat: NumberFormat = DecimalFormat("#,###")
    return numberFormat.format(number)
//    return NumberFormat.getCurrencyInstance(Locale(language,country)).format(number)
}

fun percentFormat(data: Double): String {
    val decimalFormat = DecimalFormat("#.##")

    return "${decimalFormat.format(data)}%"
}
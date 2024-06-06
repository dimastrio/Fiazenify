package id.dimas.fiazenify.util

import android.util.Patterns

object Extension {

    fun String.isValidated(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}
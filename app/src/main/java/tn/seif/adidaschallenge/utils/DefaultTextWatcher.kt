package tn.seif.adidaschallenge.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * An implementation of [TextWatcher] used to make listening to specific text events more concise.
 *
 * @property beforeTextChanged - A callback to be called when [TextWatcher.beforeTextChanged] is triggered.
 * @property onTextChanged - A callback to be called when [TextWatcher.onTextChanged] is triggered.
 * @property afterTextChanged - A callback to be called when [TextWatcher.afterTextChanged] is triggered.
 */
class DefaultTextWatcher(
    private val beforeTextChanged: ((CharSequence?) -> Unit)? = null,
    private val onTextChanged: ((CharSequence?) -> Unit)? = null,
    private val afterTextChanged: ((CharSequence?) -> Unit)? = null,
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTextChanged?.invoke(s)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged?.invoke(s)
    }

    override fun afterTextChanged(s: Editable?) {
        afterTextChanged?.invoke(s)
    }
}
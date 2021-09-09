package tn.seif.adidaschallenge.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.databinding.ViewNavigationBarBinding
import tn.seif.adidaschallenge.utils.DefaultTextWatcher
import tn.seif.adidaschallenge.utils.DefaultTransitionListener

/**
 * View presenting the navigation bar of the application containing an animated search input.
 */
class NavigationBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Indicates if the search input is visible to the user.
     */
    val isSearchActive
        get() = vb.closeButton.isVisible

    /**
     * Get the search input text.
     */
    val searchText
        get() = vb.searchBox.text.toString()

    private val vb by lazy {
        ViewNavigationBarBinding.inflate(LayoutInflater.from(context))
    }

    private val showSearchViewTransition by lazy {
        AutoTransition().apply {
            addListener(DefaultTransitionListener(onTransitionEnd = {
                searchStateListener?.onSearchShown()
                vb.searchBox.addTextChangedListener(searchTextWatcher)
                vb.searchBox.requestFocus()
                openKeyboard()
            }))
        }
    }

    private val hideSearchViewTransition by lazy {
        AutoTransition().apply {
            addListener(DefaultTransitionListener(onTransitionStart = {
                vb.searchBox.removeTextChangedListener(searchTextWatcher)
                vb.searchBox.clearFocus()
                closeKeyboard()
            }, onTransitionEnd = {
                searchStateListener?.onSearchHidden()
                vb.searchBox.text.clear()
            }))
        }
    }

    private val defaultConstraints: ConstraintSet
    private val expandedSearchConstrains: ConstraintSet
    private var searchStateListener: SearchStateListener? = null

    private var searchAfterTextChangedListener: ((CharSequence?) -> Unit)? = null
    private val searchTextWatcher = DefaultTextWatcher(afterTextChanged = {
        searchAfterTextChangedListener?.invoke(it)
    })

    init {
        addView(vb.root)
        defaultConstraints = ConstraintSet().apply { clone(vb.root) }
        expandedSearchConstrains = ConstraintSet().apply {
            clone(context, R.layout.view_navigation_bar_search_expanded)
        }
        setupAnimations()
    }

    /**
     * Disables to search button.
     */
    fun disableSearch() {
        vb.searchButton.isEnabled = false
    }

    /**
     * Enables the search button.
     */
    fun enableSearch() {
        vb.searchButton.isEnabled = true
    }

    /**
     * Sets an instance of [SearchStateListener].
     */
    fun setSearchStateListener(searchStateListener: SearchStateListener) {
        this.searchStateListener = searchStateListener
    }

    /**
     * Sets a callback to be triggered when text changes in the search input.
     */
    fun setSearchAfterTextChangedListener(afterTextChanged: (CharSequence?) -> Unit) {
        this.searchAfterTextChangedListener = afterTextChanged
    }

    private fun setupAnimations() {
        vb.searchButton.setOnClickListener {
            showSearchView()
        }
        vb.closeButton.setOnClickListener {
            hideSearchView()
        }
    }

    private fun showSearchView() {
        TransitionManager
            .beginDelayedTransition(vb.root, showSearchViewTransition)
        expandedSearchConstrains.applyTo(vb.root)
    }

    private fun hideSearchView() {
        TransitionManager
            .beginDelayedTransition(vb.root, hideSearchViewTransition)
        defaultConstraints.applyTo(vb.root)
    }

    private fun openKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(vb.searchBox, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(vb.searchBox.windowToken, 0)
    }

    interface SearchStateListener {
        fun onSearchShown()
        fun onSearchHidden()
    }
}

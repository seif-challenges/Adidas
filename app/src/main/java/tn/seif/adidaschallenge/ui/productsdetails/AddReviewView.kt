package tn.seif.adidaschallenge.ui.productsdetails

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import tn.seif.adidaschallenge.databinding.ViewAddReviewBinding
import tn.seif.adidaschallenge.ui.dialogs.CustomDialog
import tn.seif.adidaschallenge.ui.dialogs.DialogFactory
import tn.seif.adidaschallenge.utils.DefaultTextWatcher

/**
 * A view representing a star rating bar with an input to add a review.
 * Used as a body view for a [CustomDialog]. See [DialogFactory.createAddReviewDialog].
 */
class AddReviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val vb by lazy {
        ViewAddReviewBinding.inflate(LayoutInflater.from(context))
    }

    private val rating: Float
        get() = vb.addReviewRating.rating

    private val message: String
        get() = vb.addReviewMessage.text.toString()

    private var onAddReviewClicked: ((Float, String) -> Unit)? = null

    init {
        addView(vb.root)
        vb.addReviewRating.setOnRatingBarChangeListener { _, rating, _ ->
            vb.addReviewButton.isEnabled = rating > 0 || message.isNotEmpty()
        }
        vb.addReviewMessage.addTextChangedListener(DefaultTextWatcher(afterTextChanged = {
            vb.addReviewButton.isEnabled = rating > 0 || message.isNotEmpty()
        }))
        vb.addReviewButton.setOnClickListener {
            onAddReviewClicked?.invoke(rating, message)
        }
    }

    /**
     * Sets a callback to be triggered when the add review button is clicked.
     */
    fun setAddReviewListener(onAddReviewClicked: (Float, String) -> Unit) {
        this.onAddReviewClicked = onAddReviewClicked
    }

    /**
     * Sets the loading state.
     *
     * @param isLoading - true show loader, false hide it.
     */
    fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            startLoader()
        } else {
            stopLoader()
        }
    }

    /**
     * Stops the loader if running and shows the error message.
     */
    fun showError() {
        stopLoader()
        vb.addReviewError.visibility = VISIBLE
    }

    /**
     * Stops the loader if running, and hide the error message.
     */
    fun hideError() {
        stopLoader()
        vb.addReviewError.visibility = GONE
    }

    /**
     * Reset the view state.
     */
    fun reset() {
        vb.addReviewMessage.text.clear()
        vb.addReviewError.visibility = GONE
        vb.addReviewDataLayout.visibility = VISIBLE
        vb.addReviewRating.rating = 0f
        stopLoader()
    }

    private fun startLoader() {
        vb.addReviewDataLayout.visibility = INVISIBLE
        vb.progressIndicator.visibility = VISIBLE
        vb.addReviewError.visibility = GONE
        vb.addReviewButton.isEnabled = false
    }

    private fun stopLoader() {
        vb.addReviewDataLayout.visibility = VISIBLE
        vb.progressIndicator.visibility = GONE
        vb.addReviewButton.isEnabled = true
    }
}
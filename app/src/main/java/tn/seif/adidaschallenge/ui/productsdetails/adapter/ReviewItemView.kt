package tn.seif.adidaschallenge.ui.productsdetails.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.databinding.ItemViewReviewBinding
import tn.seif.adidaschallenge.utils.BindableView

/**
 * An item view used by [ReviewsAdapter] to present a [Review] inside a recycler view.
 */
class ReviewItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BindableView<Review> {

    private val vb by lazy {
        ItemViewReviewBinding.inflate(LayoutInflater.from(context))
    }

    init {
        addView(vb.root)
    }

    override fun bind(item: Review) {
        vb.reviewRating.rating = item.rating
        vb.reviewMessage.text = item.text
    }
}

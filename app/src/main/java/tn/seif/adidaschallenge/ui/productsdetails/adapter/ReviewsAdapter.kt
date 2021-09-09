package tn.seif.adidaschallenge.ui.productsdetails.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.utils.GenericViewHolder

/**
 * Recycler view adapter responsible of displaying the list of reviews.
 */
class ReviewsAdapter : ListAdapter<Review, GenericViewHolder<ReviewItemView, Review>>(DIFF_CALL) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<ReviewItemView, Review> {
        return GenericViewHolder(ReviewItemView(parent.context))
    }

    override fun onBindViewHolder(
        holder: GenericViewHolder<ReviewItemView, Review>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALL = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }
}
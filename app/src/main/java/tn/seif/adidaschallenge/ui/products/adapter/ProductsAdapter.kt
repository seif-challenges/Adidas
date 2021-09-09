package tn.seif.adidaschallenge.ui.products.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.utils.GenericViewHolder

/**
 * Recycler view adapter responsible of displaying the list of products.
 */
class ProductsAdapter(
    private val productClickListener: (Product) -> Unit
) : ListAdapter<Product, GenericViewHolder<ProductItemView, Product>>(DIFF_CALL) {

    override fun onBindViewHolder(
        holder: GenericViewHolder<ProductItemView, Product>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<ProductItemView, Product> {
        val itemView = ProductItemView(parent.context).apply {
            setClickListener(productClickListener)
        }
        return GenericViewHolder(itemView)
    }

    companion object {
        private val DIFF_CALL = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}

package tn.seif.adidaschallenge.ui.products.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.databinding.ItemViewProductBinding
import tn.seif.adidaschallenge.utils.BindableView

/**
 * An item view used by [ProductsAdapter] to present a [Product] inside a recycler view.
 */
class ProductItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BindableView<Product> {

    private lateinit var product: Product
    private var onClick: ((Product) -> Unit)? = null

    private val vb by lazy {
        ItemViewProductBinding.inflate(LayoutInflater.from(context))
    }

    init {
        addView(vb.root)
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        vb.addCartButton.setOnClickListener {
            Toast.makeText(context, context.getString(R.string.feature_coming_soon), Toast.LENGTH_LONG).show()
        }
        setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun bind(item: Product) {
        this.product = item
        vb.productName.text = item.name.replaceFirstChar { it.uppercase() }
        vb.productDescription.text = item.description.replaceFirstChar { it.uppercase() }
        vb.productPrice.text = context.getString(R.string.product_item_price_format, item.currency, item.price)
        Glide.with(context)
            .load(item.imgUrl)
            .placeholder(R.drawable.ic_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(vb.productImage)
    }

    /**
     * Sets a callback to be triggered when the item view is clicked.
     */
    fun setClickListener(onClick: (Product) -> Unit) {
        this.onClick = onClick
    }
}

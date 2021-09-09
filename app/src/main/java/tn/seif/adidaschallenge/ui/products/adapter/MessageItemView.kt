package tn.seif.adidaschallenge.ui.products.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import tn.seif.adidaschallenge.databinding.ItemViewMessageBinding
import tn.seif.adidaschallenge.utils.BindableView

/**
 * An item view used by [MessagesAdapter] to show a text message (products count and a message) inside a recycler view.
 */
class MessageItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BindableView<MessageModel> {

    lateinit var messageModel: MessageModel
        private set

    private val vb by lazy {
        ItemViewMessageBinding.inflate(LayoutInflater.from(context))
    }

    init {
        addView(vb.root)
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun bind(item: MessageModel) {
        messageModel = item
        vb.productsCount.text = "${item.count}"
        vb.message.text = item.message
    }
}

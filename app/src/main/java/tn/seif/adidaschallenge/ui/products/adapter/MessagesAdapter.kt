package tn.seif.adidaschallenge.ui.products.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import tn.seif.adidaschallenge.utils.GenericViewHolder

/**
 * Recycler view adapter responsible of displaying the result count message.
 * The adapter only supports showing one message view and can't be used for multiple ones because it keeps a reference to the inflated view.
 */
class MessagesAdapter : ListAdapter<MessageModel, GenericViewHolder<MessageItemView, MessageModel>>(DIFF_CALL) {

    // Keep reference of the item view, to update directly without redrawing it.
    // This is safe because there will always be only one view.
    lateinit var messageItemView: MessageItemView

    /**
     * Checks if [messageItemView] has ben initialized or not.
     */
    fun isInitialized(): Boolean {
        return this::messageItemView.isInitialized
    }

    override fun onBindViewHolder(
        holder: GenericViewHolder<MessageItemView, MessageModel>,
        position: Int
    ) {
        getItem(position)?.let { holder.view.bind(it) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<MessageItemView, MessageModel> {
        messageItemView = MessageItemView(parent.context)
        return GenericViewHolder(messageItemView)
    }

    companion object {
        private val DIFF_CALL = object : DiffUtil.ItemCallback<MessageModel>() {
            override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
                return oldItem.count == newItem.count && oldItem.message == newItem.message
            }

            override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
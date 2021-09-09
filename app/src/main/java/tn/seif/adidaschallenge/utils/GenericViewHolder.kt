package tn.seif.adidaschallenge.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic view holder, used to facilitate binding of a view in a [RecyclerView.Adapter].
 *
 * @param V - The type of view the view holder is holding.
 * @param O - The type of object the view holder and its view supports binding.
 * @property view - An instance of the view to bind to. Must implement [BindableView].
 */
class GenericViewHolder<V, O : Any>(val view: V) : RecyclerView.ViewHolder(view)
        where V : View, V : BindableView<O> {

    fun bind(item: O) {
        view.bind(item)
    }
}

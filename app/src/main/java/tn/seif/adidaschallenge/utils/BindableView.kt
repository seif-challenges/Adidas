package tn.seif.adidaschallenge.utils

/**
 * Interface that should be implemented by views that support binding.
 * Used by [GenericViewHolder]
 */
interface BindableView<O: Any> {
    /**
     * Binds the [item] to the view.
     */
    fun bind(item: O)
}
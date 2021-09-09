package tn.seif.adidaschallenge.ui.main

/**
 * Interface that should be implemented by fragments supporting refresh from [MainActivity].
 */
interface RefreshableFragment {
    /**
     * Refresh the currently displayed data.
     */
    fun refresh()
}
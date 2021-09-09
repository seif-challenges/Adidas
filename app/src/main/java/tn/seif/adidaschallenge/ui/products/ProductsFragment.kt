package tn.seif.adidaschallenge.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.PluralsRes
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.databinding.FragmentProductsBinding
import tn.seif.adidaschallenge.mvvm.base.BaseMvvmFragment
import tn.seif.adidaschallenge.mvvm.viewModel
import tn.seif.adidaschallenge.ui.custom.NavigationBarView
import tn.seif.adidaschallenge.ui.main.RefreshableFragment
import tn.seif.adidaschallenge.ui.products.adapter.MessageModel
import tn.seif.adidaschallenge.ui.products.adapter.MessagesAdapter
import tn.seif.adidaschallenge.ui.products.adapter.ProductsAdapter
import tn.seif.adidaschallenge.utils.models.State

/**
 * Fragment presenting the list of products and a [NavigationBarView] supporting search.
 */
class ProductsFragment : BaseMvvmFragment<FragmentProductsBinding, ProductsViewModel>(), RefreshableFragment {

    private val productsAdapter by lazy {
        ProductsAdapter {
            findNavController().navigate(
                ProductsFragmentDirections.actionProductsFragmentToProductDetailsFragment(
                    it.id
                )
            )
        }
    }

    private val messagesAdapter by lazy {
        MessagesAdapter()
    }

    // Using a concat adapter to be able to show the result message along with the list of products.
    private val concatAdapter by lazy {
        ConcatAdapter(messagesAdapter, productsAdapter)
    }

    @PluralsRes
    private var resultMessageString: Int = R.plurals.product_awaits_your

    override val vm: ProductsViewModel by viewModel(ProductsViewModel::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetch the products on the creation of the fragment, to prevent loading each time the fragment's view is recreated
        // (ex: When navigating back to this fragment)
        vm.getProducts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // These will perform actions on the fragment's views, and thus must only be called after ensuring the fragment's view creation.
        setupViews()
        observeOutputs()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductsBinding {
        return FragmentProductsBinding.inflate(inflater, container, false)
    }

    override fun refresh() {
        vm.getProducts()
    }

    private fun observeOutputs() {
        vm.products.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    if (!vb.swipeRefreshLayout.isRefreshing)
                        startLoader()
                    vb.navBarLayout.disableSearch()
                }
                is State.Success -> {
                    vb.swipeRefreshLayout.isRefreshing = false
                    if (!vb.navBarLayout.isSearchActive && it.result.isEmpty()) {
                        handleEmptyState()
                        return@observe
                    }
                    stopLoader()
                    vb.navBarLayout.enableSearch()
                    // Bind directly to the view instance to prevent re-drawing of the view
                    // which will cause the layout manager to re-arrange it,
                    // resulting in a weird UI behavior. It will always be one view.
                    updateCountMessage(it.result.size)
                    productsAdapter.submitList(it.result)
                    vb.productsRv.scrollToPosition(0)
                }
                is State.Error -> {
                    vb.navBarLayout.disableSearch()
                    vb.swipeRefreshLayout.isRefreshing = false
                    handleError()
                }
            }
        }
    }

    private fun updateCountMessage(size: Int? = null) {
        if (!messagesAdapter.isInitialized() && size == null)
            throw IllegalStateException("Can't initialize adapter with null count!")

        size?.let {
            // if the size is not null, create a new MessageModel with the size and the message
            // and if the message adapter has not been initialized, submit a list to do so
            // otherwise update the item view binding it to the new model.
            val message = resources.getQuantityString(resultMessageString, it)
            val messageModel = MessageModel(it, message)
            if (messagesAdapter.isInitialized()) {
                messagesAdapter.messageItemView.bind(messageModel)
            } else {
                messagesAdapter.submitList(listOf(messageModel))
            }
        } ?: run {
            // If the size is null, only the message need to be updated
            // get the already used MessageModel from the adapter, and update the message value
            // then update the item view by rebinding it with the model.
            val messageModel = messagesAdapter.messageItemView.messageModel.apply {
                message = resources.getQuantityString(resultMessageString, count)
            }
            messagesAdapter.messageItemView.bind(messageModel)
        }
    }

    private fun startLoader() {
        vb.productsRv.visibility = View.GONE
        vb.emptyLayout.root.visibility = View.GONE
        vb.errorLayout.root.visibility = View.GONE
        vb.loader.visibility = View.VISIBLE
        vb.loader.startLoader()
    }

    private fun stopLoader() {
        vb.productsRv.visibility = View.VISIBLE
        vb.emptyLayout.root.visibility = View.GONE
        vb.errorLayout.root.visibility = View.GONE
        vb.loader.visibility = View.GONE
        vb.loader.stopLoader()
    }

    private fun handleError() {
        vb.loader.visibility = View.GONE
        vb.productsRv.visibility = View.GONE
        vb.emptyLayout.root.visibility = View.GONE
        vb.errorLayout.root.visibility = View.VISIBLE
        vb.loader.stopLoader()
    }

    private fun handleEmptyState() {
        vb.loader.visibility = View.GONE
        vb.productsRv.visibility = View.GONE
        vb.errorLayout.root.visibility = View.GONE
        vb.emptyLayout.root.visibility = View.VISIBLE
        vb.loader.stopLoader()
    }

    private fun setupViews() {
        vb.productsRv.adapter = concatAdapter
        vb.emptyLayout.reportButton.setOnClickListener {
            Toast.makeText(context, getString(R.string.feature_coming_soon), Toast.LENGTH_LONG).show()
        }
        vb.navBarLayout.setSearchStateListener(object : NavigationBarView.SearchStateListener {
            override fun onSearchShown() {
                resultMessageString = R.plurals.product_found
                updateCountMessage()
            }

            override fun onSearchHidden() {
                resultMessageString = R.plurals.product_awaits_your
                vm.getProducts()
            }
        })
        vb.navBarLayout.setSearchAfterTextChangedListener {
            vm.getProducts(it.toString())
        }
        vb.swipeRefreshLayout.setOnRefreshListener {
            vm.getProducts(vb.navBarLayout.searchText)
        }
    }
}
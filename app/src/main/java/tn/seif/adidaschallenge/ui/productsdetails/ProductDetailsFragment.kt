package tn.seif.adidaschallenge.ui.productsdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.android.support.AndroidSupportInjection
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.databinding.FragmentProductDetailsBinding
import tn.seif.adidaschallenge.mvvm.base.BaseMvvmFragment
import tn.seif.adidaschallenge.mvvm.viewModel
import tn.seif.adidaschallenge.ui.background.ReviewSyncBackgroundService
import tn.seif.adidaschallenge.ui.dialogs.DialogFactory
import tn.seif.adidaschallenge.ui.main.RefreshableFragment
import tn.seif.adidaschallenge.ui.productsdetails.adapter.ReviewsAdapter
import tn.seif.adidaschallenge.utils.NetworkListener
import tn.seif.adidaschallenge.utils.models.State
import tn.seif.adidaschallenge.utils.round
import javax.inject.Inject

/**
 * Fragment presenting the details of a [Product] and the list of its [Review]s.
 */
class ProductDetailsFragment : BaseMvvmFragment<FragmentProductDetailsBinding, ProductDetailsViewModel>(), RefreshableFragment {

    @Inject
    lateinit var dialogFactory: DialogFactory

    @Inject
    lateinit var networkListener: NetworkListener

    override val vm: ProductDetailsViewModel by viewModel(ProductDetailsViewModel::class)
    private val args: ProductDetailsFragmentArgs by navArgs()

    private val reviewsAdapter by lazy {
        ReviewsAdapter()
    }

    // Keep the reference of the add review view, so that it can be used to display the different
    // states of adding a review (adding, loading, error) on the same view.
    private val addReviewView by lazy {
        AddReviewView(requireContext()).apply {
            setAddReviewListener { rating, message ->
                vm.addReview(args.productId, rating, message)
            }
        }
    }

    private val addReviewDialog by lazy {
        dialogFactory.createAddReviewDialog(addReviewView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

        // Fetch the product and its reviews on the creation of the fragment, to prevent loading each time the fragment's view is recreated
        // (ex: When navigating back to this fragment)
        vm.getProductById(args.productId)
        vm.getProductReviews(args.productId)
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
    ): FragmentProductDetailsBinding {
        return FragmentProductDetailsBinding.inflate(inflater, container, false)
    }

    override fun refresh() {
        vm.getProductById(args.productId)
        vm.getProductReviews(args.productId)
    }

    private fun setupViews() {
        vb.reviewsRecycler.adapter = reviewsAdapter
        vb.addCartButton.setOnClickListener {
            Toast.makeText(context, getString(R.string.feature_coming_soon), Toast.LENGTH_LONG).show()
        }
        vb.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        vb.emptyBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        vb.addReviewButton.setOnClickListener {
            addReviewDialog.show(parentFragmentManager, ADD_REVIEW_DIALOG_TAG)
        }
    }

    private fun observeOutputs() {
        vm.product.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Loading -> startLoader()
                is State.Success -> handleProductSuccess(state.result)
                is State.Error -> handleError()
            }
        }

        vm.reviews.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Loading -> startReviewsLoader()
                is State.Success -> handleReviewsSuccess(state.result)
                is State.Error -> handleReviewsError()
            }
        }

        vm.addReview.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Loading -> startAddReviewLoader()
                is State.Success -> {
                    resetAddReviewView()
                    // refresh the list of reviews.
                    vm.getProductReviews(args.productId)
                    if (networkListener.networkAvailability.value == false) {
                        dialogFactory.createAddReviewOfflineDialog()
                            .show(parentFragmentManager, ADD_REVIEW_OFFLINE_DIALOG_TAG)
                        return@observe
                    }
                    // Start syncing
                    ReviewSyncBackgroundService.runService(requireContext())
                }
                is State.Error -> handleAddReviewError()
            }
        }
    }

    private fun startAddReviewLoader() {
        addReviewView.setLoadingState(true)
    }

    private fun resetAddReviewView() {
        addReviewView.reset()
        addReviewDialog.dismiss()
    }

    private fun handleAddReviewError() {
        addReviewView.showError()
    }

    private fun handleProductSuccess(product: Product) {
        stopLoader()
        vb.productDescription.text = product.description
        Glide.with(requireContext())
            .load(product.imgUrl)
            .placeholder(R.drawable.ic_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(vb.productImage)

        val nameString = product.name.replaceFirstChar { it.uppercase() }
        vb.productDetailsTitleName.text = nameString
        vb.productName.text = nameString

        val priceString = getString(R.string.product_details_price_format, product.currency, product.price)
        vb.productPrice.text = priceString
        vb.productDetailsTitlePrice.text = priceString
        vb.detailsLayout.visibility = View.VISIBLE
    }

    private fun handleReviewsSuccess(reviews: List<Review>) {
        stopReviewsLoader()
        var average = reviews.map { it.rating }.average()
        if (reviews.isEmpty()) {
            handleReviewsEmptyState()
            average = 0.0
        }

        val overallReview = average.round(2)
        vb.overallReview.text = "$overallReview"
        vb.overallStar.rating = (overallReview / 5).toFloat()
        vb.reviewsCount.text = resources.getQuantityString(
            R.plurals.reviews,
            reviews.count(),
            reviews.count()
        )
        reviewsAdapter.submitList(reviews)
    }

    private fun startReviewsLoader() {
        vb.reviewsRecycler.visibility = View.GONE
        vb.emptyReviews.visibility = View.GONE
        vb.errorReviews.visibility = View.GONE
        vb.reviewLoader.visibility = View.VISIBLE
        vb.reviewLoader.startLoader()
    }

    private fun stopReviewsLoader() {
        vb.reviewsRecycler.visibility = View.VISIBLE
        vb.emptyReviews.visibility = View.GONE
        vb.errorReviews.visibility = View.GONE
        vb.reviewLoader.visibility = View.GONE
        vb.reviewLoader.stopLoader()
    }

    private fun handleReviewsError() {
        vb.reviewsRecycler.visibility = View.GONE
        vb.emptyReviews.visibility = View.GONE
        vb.reviewLoader.visibility = View.GONE
        vb.errorReviews.visibility = View.VISIBLE
        vb.reviewLoader.stopLoader()
    }

    private fun handleReviewsEmptyState() {
        vb.emptyReviews.visibility = View.VISIBLE
        vb.reviewsRecycler.visibility = View.GONE
    }

    private fun startLoader() {
        vb.loader.visibility = View.VISIBLE
        vb.emptyLayout.visibility = View.GONE
        vb.detailsLayout.visibility = View.GONE
        vb.errorLayout.root.visibility = View.GONE
        vb.loader.startLoader()
    }

    private fun stopLoader() {
        vb.detailsLayout.visibility = View.VISIBLE
        vb.emptyLayout.visibility = View.GONE
        vb.loader.visibility = View.GONE
        vb.errorLayout.root.visibility = View.GONE
        vb.loader.stopLoader()
    }

    private fun handleError() {
        vb.emptyLayout.visibility = View.VISIBLE
        vb.errorLayout.root.visibility = View.VISIBLE
        vb.loader.visibility = View.GONE
        vb.detailsLayout.visibility = View.GONE
        vb.loader.stopLoader()
    }

    companion object {
        private const val ADD_REVIEW_DIALOG_TAG = "REVIEW_DIALOG"
        private const val ADD_REVIEW_OFFLINE_DIALOG_TAG = "ADD_REVIEW_OFFLINE_DIALOG"
    }
}

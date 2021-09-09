package tn.seif.adidaschallenge.mvvm

import tn.seif.adidaschallenge.mvvm.base.BaseKnownViewModels
import tn.seif.adidaschallenge.mvvm.base.ViewModelClass
import tn.seif.adidaschallenge.ui.products.ProductsViewModel
import tn.seif.adidaschallenge.ui.productsdetails.ProductDetailsViewModel
import javax.inject.Inject
import javax.inject.Provider

/**
 * An subclass of [BaseKnownViewModels], holding reference to all view models needed by the app.
 */
class KnownViewModels @Inject constructor(
    @ViewModelClass(ProductsViewModel::class)
    val productViewModel: Provider<ProductsViewModel>,
    @ViewModelClass(ProductDetailsViewModel::class)
    val productDetailsViewModel: Provider<ProductDetailsViewModel>
) : BaseKnownViewModels()
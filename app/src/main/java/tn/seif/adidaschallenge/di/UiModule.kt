package tn.seif.adidaschallenge.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import tn.seif.adidaschallenge.ui.main.MainActivity
import tn.seif.adidaschallenge.ui.productsdetails.ProductDetailsFragment

/**
 * Activities and fragments should be registered in here so Dagger knows it will have to provide injections to them.
 */
@Module
interface UiModule {
    /* region Activities */
    @ContributesAndroidInjector
    fun contributeMainActivity(): MainActivity?
    /* endregion */

    /* region Fragments */
    @ContributesAndroidInjector
    fun contributeProductDetailsFragment(): ProductDetailsFragment?
    /* endregion */
}
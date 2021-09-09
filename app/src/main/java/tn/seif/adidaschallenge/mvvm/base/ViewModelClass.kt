package tn.seif.adidaschallenge.mvvm.base

import kotlin.reflect.KClass

/**
 * An annotation used by [BaseKnownViewModels] to get the member view model class.
 *
 * @property kClass - The class of the view model.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class ViewModelClass(val kClass: KClass<*>)
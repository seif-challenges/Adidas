package tn.seif.adidaschallenge.mvvm.base

import javax.inject.Provider
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Class responsible of indexing available view models in the app.
 */
abstract class BaseKnownViewModels {

    /**
     * A hashmap of all available view models in the app indexed by the view model class name.
     *
     * @throws - A [MemberIsNotProviderException] if a member of the subclass is not of type [Provider].
     * @throws - A [MissingAnnotationException] if a member of the subclass is not annotated with [ViewModelClass].
     * @throws - A [DuplicateViewModelException] if a duplicate member was found in the subclass.
     */
    private val knownViewModelsMap: Map<String, Provider<*>> by lazy {
        val mutableMap = mutableMapOf<String, Provider<*>>()

        this::class.memberProperties.forEach {
            val value = it.getter.call(this)
            if (value is Provider<*>) {
                val annotation = it.findAnnotation<ViewModelClass>()
                annotation?.let { viewModelClass ->
                    val key = viewModelClass.kClass.java.name
                    if (mutableMap.containsKey(key)) {
                        throw DuplicateViewModelException(key, this.javaClass.simpleName)
                    } else {
                        mutableMap[key] = value
                    }
                } ?: throw MissingAnnotationException()
            } else {
                throw MemberIsNotProviderException()
            }
        }
        return@lazy mutableMap
    }

    /**
     * Find and returns the view model instance [T] from the indexed map.
     *
     * @param T - The type of the view model.
     * @param viewModelKey - The class name of view model used as index.
     * @return - An instance of the view model.
     * @throws - A [NotFoundViewModelException] if the view model can't be found in the indexed map.
     */
    @Suppress("UNCHECKED_CAST")
    @PublishedApi
    internal fun <T : BaseViewModel> findViewModel(viewModelKey: String): T {
        if (knownViewModelsMap.containsKey(viewModelKey)) {
            return knownViewModelsMap[viewModelKey]?.get() as T
        }
        throw NotFoundViewModelException(viewModelKey, this.javaClass.simpleName)
    }

    companion object {
        /**
         * Search and return the instance of a view model index in the map using it's class name.
         *
         * @param VM - The type of the view model.
         * @param KV - The type the subclass inheriting [BaseKnownViewModels] used by the application.
         * @param instance - The instance of subclass inheriting [BaseKnownViewModels] used by the application.
         * @param vmClass - The [KClass] of the view model.
         * @return - An instance of the view model if found.
         * @throws - A [NullClassNameException] if the qualifiedName of of the view model class is empty. See [KClass.qualifiedName].
         * @throws - A [NotFoundViewModelException] if the view model can't be found in the indexed map.
         */
        fun <VM : BaseViewModel, KV : BaseKnownViewModels> getViewModel(
            instance: KV,
            vmClass: KClass<VM>
        ): VM =
            vmClass.qualifiedName?.let {
                return instance.findViewModel(it)
            } ?: throw NullClassNameException()
    }

    class NotFoundViewModelException(
        viewModelName: String,
        className: String
    ) : Exception("View model [$viewModelName] was not found in [$className]")

    class DuplicateViewModelException(
        type: String,
        className: String
    ) : Exception("Multiple instances of [$type] in [$className]")

    class NullClassNameException : Exception()

    class MemberIsNotProviderException : Exception("All members of known view models should be of type Provider")

    class MissingAnnotationException : Exception("All member of known view models should be annotated with ViewModelClass annotation")
}
package tn.seif.adidaschallenge.utils

import androidx.transition.Transition

/**
 * An implementation of [Transition.TransitionListener] used to make listening to specific transition events more concise.
 *
 * @property onTransitionStart - A callback to be called when [Transition.TransitionListener.onTransitionStart] is called.
 * @property onTransitionEnd - A callback to be called when [Transition.TransitionListener.onTransitionEnd] is called.
 * @property onTransitionCancel - A callback to be called when [Transition.TransitionListener.onTransitionCancel] is called.
 * @property onTransitionPause - A callback to be called when [Transition.TransitionListener.onTransitionPause] is called.
 * @property onTransitionResume - A callback to be called when [Transition.TransitionListener.onTransitionResume] is called.
 */
class DefaultTransitionListener(
    private val onTransitionStart: ((Transition) -> Unit)? = null,
    private val onTransitionEnd: ((Transition) -> Unit)? = null,
    private val onTransitionCancel: ((Transition) -> Unit)? = null,
    private val onTransitionPause: ((Transition) -> Unit)? = null,
    private val onTransitionResume: ((Transition) -> Unit)? = null
) : Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) {
        onTransitionStart?.invoke(transition)
    }

    override fun onTransitionEnd(transition: Transition) {
        onTransitionEnd?.invoke(transition)
    }

    override fun onTransitionCancel(transition: Transition) {
        onTransitionCancel?.invoke(transition)
    }

    override fun onTransitionPause(transition: Transition) {
        onTransitionPause?.invoke(transition)
    }

    override fun onTransitionResume(transition: Transition) {
        onTransitionResume?.invoke(transition)
    }
}

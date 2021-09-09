package tn.seif.adidaschallenge.ui.custom

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.databinding.ViewDotsLoaderBinding

/**
 * View presenting a loading animation.
 */
class DotsLoader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val vb by lazy {
        ViewDotsLoaderBinding.inflate(LayoutInflater.from(context))
    }

    private val firstAnimation by lazy {
        ObjectAnimator.ofPropertyValuesHolder(
            vb.first,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f),
            PropertyValuesHolder.ofFloat("alpha", 1f)
        ).apply {
            duration = 750
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
    }

    private val secondAnimation by lazy {
        ObjectAnimator.ofPropertyValuesHolder(
            vb.second,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f),
            PropertyValuesHolder.ofFloat("alpha", 1f)
        ).apply {
            duration = 750
            startDelay = 250
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
    }

    private val thirdAnimation by lazy {
        ObjectAnimator.ofPropertyValuesHolder(
            vb.third,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f),
            PropertyValuesHolder.ofFloat("alpha", 1f)
        ).apply {
            duration = 750
            startDelay = 500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
    }
    private var hasLogo: Boolean = true

    init {
        addView(vb.root)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.DotsLoader,
                0,
                0
            )
            hasLogo = typedArray.getBoolean(R.styleable.DotsLoader_hasLogo, true)
            typedArray.recycle()
        }
    }

    /**
     * Starts the loading animation
     */
    fun startLoader() {
        vb.adidasLogo.visibility = if (hasLogo) VISIBLE else GONE
        firstAnimation.start()
        secondAnimation.start()
        thirdAnimation.start()
    }

    /**
     * Stops the loading animation
     */
    fun stopLoader() {
        vb.adidasLogo.visibility = GONE
        firstAnimation.end()
        secondAnimation.end()
        thirdAnimation.end()
    }
}
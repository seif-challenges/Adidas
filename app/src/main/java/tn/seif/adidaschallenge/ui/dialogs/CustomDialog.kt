package tn.seif.adidaschallenge.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.databinding.ViewCustomDialogBinding
import tn.seif.adidaschallenge.ui.dialogs.CustomDialog.DialogContainer

/**
 * A custom dialog extending [DialogFragment] having [DialogContainer] as the content view.
 *
 * @property setup - A callback for setting dialog properties.
 */
open class CustomDialog(private val setup: ((DialogContainer) -> Unit)) : DialogFragment() {

    private lateinit var dialogContainer: DialogContainer

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Setup dialog
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = true
        // Inflate the view
        context?.let {
            dialogContainer = DialogContainer(it).apply {
                dialogReference = this@CustomDialog
            }.apply(setup)
        }?.let { dialog.setContentView(dialogContainer) }
        // Give back the dialog
        return dialog
    }

    override fun onStart() {
        super.onStart()
        // Fix dialog window sizing and styling
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes = attributes.apply {
                height = WindowManager.LayoutParams.MATCH_PARENT
                width = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        dialogContainer.resetBodyView()
        super.onDismiss(dialog)
    }

    // endregion

    // region container
    class DialogContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : FrameLayout(context, attrs, defStyleAttr) {

        lateinit var dialogReference: CustomDialog

        private val vb by lazy {
            ViewCustomDialogBinding.inflate(LayoutInflater.from(context))
        }

        init {
            addView(vb.root)
            // Set dialog background tint
            setBackgroundResource(R.color.dialog_background_tint)
            post {
                // Pop in the dialog
                vb.dialogContainer.run {
                    scaleX = DIALOG_POP_IN_START_SCALE
                    scaleY = DIALOG_POP_IN_START_SCALE
                    animate().scaleX(1F)
                        .scaleY(1F)
                        .setDuration(DIALOG_POP_IN_DURATION)
                        .setInterpolator(OvershootInterpolator(DIALOG_POP_IN_OVERSHOOT))
                        .start()
                }
                vb.dialogCloseButton.setOnClickListener {
                    dialogReference.dismiss()
                }
            }
        }

        /* region Setters */
        /**
         * Sets the title text of the dialog.
         */
        fun setTitle(title: String) {
            vb.dialogTitle.apply {
                text = title
                visibility = View.VISIBLE
            }
        }

        /**
         * Sets the subtitle text of the dialog.
         */
        fun setSubtitle(subtitle: String) {
            vb.dialogSubtitle.apply {
                text = subtitle
                visibility = View.VISIBLE
            }
        }

        /**
         * Sets the message text of the dialog.
         */
        fun setMessage(message: String) {
            vb.dialogMessage.apply {
                text = message
                visibility = View.VISIBLE
            }
        }

        /**
         * Sets the button text and callback.
         */
        fun setButton(label: String, onClick: (dialog: CustomDialog) -> Unit) {
            vb.dialogButton.apply {
                text = label
                setOnClickListener {
                    onClick.invoke(dialogReference)
                }
                visibility = View.VISIBLE
            }
        }

        /**
         * Sets the body view of the dialog.
         */
        fun setBodyView(view: View) {
            vb.dialogBodyView.apply {
                addView(view)
                visibility = View.VISIBLE
            }
        }

        /**
         * Clears the body view.
         */
        fun resetBodyView() {
            vb.dialogBodyView.removeAllViews()
        }

        /**
         * Set dialog cancelable or not.
         */
        fun setDismissable(isDismissable: Boolean) {
            dialogReference.isCancelable = isDismissable
            vb.dialogCloseButton.visibility = if (isDismissable) View.VISIBLE else View.GONE
        }

        companion object {
            private const val DIALOG_POP_IN_START_SCALE = 0.5F
            private const val DIALOG_POP_IN_DURATION = 250L
            private const val DIALOG_POP_IN_OVERSHOOT = 1.25F
        }
    }
    /* endregion */
}
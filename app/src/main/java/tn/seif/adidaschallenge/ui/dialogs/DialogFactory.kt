package tn.seif.adidaschallenge.ui.dialogs

import android.view.View
import tn.seif.adidaschallenge.R
import javax.inject.Inject

/**
 * A factory used for creating dialogs needed in the app.
 */
class DialogFactory @Inject constructor() {

    fun createOfflineInfoDialog(onRefreshClicked: (CustomDialog) -> Unit): CustomDialog {
        return CustomDialog {
            it.run {
                setTitle(context.getString(R.string.offline_dialog_title))
                setMessage(context.getString(R.string.offline_dialog_message))
                setButton(context.getString(R.string.offline_dialog_refresh), onRefreshClicked)
                setDismissable(true)
            }
        }
    }

    fun createAddReviewDialog(
        reviewBodyView: View
    ): CustomDialog {
        return CustomDialog {
            it.run {
                setTitle(context.getString(R.string.add_review_dialog_title))
                setBodyView(reviewBodyView)
                setDismissable(true)
            }
        }
    }

    fun createAddReviewOfflineDialog(): CustomDialog {
        return CustomDialog {
            it.run {
                setSubtitle(context.getString(R.string.add_review_offline_dialog_message))
                setButton(context.getString(R.string.ok)) { dialog ->
                    dialog.dismiss()
                }
                setDismissable(false)
            }
        }
    }
}
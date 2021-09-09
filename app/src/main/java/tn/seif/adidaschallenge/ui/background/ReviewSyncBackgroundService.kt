package tn.seif.adidaschallenge.ui.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import tn.seif.adidaschallenge.App
import tn.seif.adidaschallenge.R
import tn.seif.adidaschallenge.data.local.AppDatabase
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.data.repositories.ReviewsRepo
import tn.seif.adidaschallenge.di.AppModule
import tn.seif.adidaschallenge.di.DatabaseModule
import tn.seif.adidaschallenge.di.background.DaggerBackgroundServiceComponent
import tn.seif.adidaschallenge.ui.splash.SplashActivity
import tn.seif.adidaschallenge.utils.models.Answer
import javax.inject.Inject

/**
 * A background service responsible of syncing pending reviews.
 */
class ReviewSyncBackgroundService : LifecycleService() {

    @Inject
    lateinit var reviewsRepo: ReviewsRepo

    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        // Inject the background service Dagger component.
        DaggerBackgroundServiceComponent.builder()
            .appModule(AppModule(application as App))
            .databaseModule(DatabaseModule(AppDatabase.build(this)))
            .build()
            .inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // If the background service is already running don't start scanning again.
        if (!isRunning) {
            startScanning()
            isRunning = true
        }
        return START_STICKY
    }

    private fun startScanning() {
        // Show a sticky notification if the build version is higher than Android Oreo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showScanningNotification()
        }
        scanPendingReviews()
    }

    private fun scanPendingReviews() {
        lifecycleScope.launch(IO) {
            when (val answer = reviewsRepo.getPendingReviews()) {
                is Answer.Success -> startSyncing(answer.result)
                is Answer.Failure -> {
                    stopSelf()
                    /* Do nothing, error should already be logged to Crashlytics. */
                    /* No need to inform the user about it */
                }
            }
        }
    }

    private fun startSyncing(pendingReviews: List<Review>) {
        // No reviews to sync, stop background service.
        if (pendingReviews.isEmpty()) {
            stopSelf()
            return
        }

        lifecycleScope.launch(IO) {
            var counter = 0
            // Initiate the notification message with 0.
            updateProgressNotification(pendingReviews.size, counter)
            pendingReviews.forEach {
                when (reviewsRepo.syncReview(it)) {
                    is Answer.Success -> {
                        counter++
                        updateProgressNotification(pendingReviews.size, counter)
                    }
                    is Answer.Failure -> {
                        stopSelf()
                        /* Do nothing, error should already be logged to Crashlytics.*/
                        /* No need to inform the user about it*/
                    }
                }
            }
            // Syncing is done, stop background service.
            stopSelf()
        }
    }

    private fun updateProgressNotification(itemsToSyncCount: Int, syncedCount: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val notificationBuilder = NotificationCompat.Builder(this, packageName)
        val notification = notificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_adidas_logo_white)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(getString(R.string.syncing_notification_title))
            .setContentText(
                getString(
                    R.string.syncing_notification_message,
                    syncedCount,
                    itemsToSyncCount
                )
            )
            .setProgress(itemsToSyncCount, syncedCount, false)
            .build()
        startForeground(ID, notification)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showScanningNotification() {
        val notificationIntent = Intent(this, SplashActivity::class.java)
        val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flag)

        val channelName = getString(R.string.syncing_notification_channel_name)
        val channel =
            NotificationChannel(packageName, channelName, NotificationManager.IMPORTANCE_NONE)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, packageName)

        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_adidas_logo_white)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentText(getString(R.string.scanning_pending_notification_title))
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(ID, notification)
    }

    companion object {
        private const val ID = 15285

        /**
         * Runs the background service.
         */
        fun runService(context: Context) {
            val serviceIntent =
                Intent(context, ReviewSyncBackgroundService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}

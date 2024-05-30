package com.pvp.app

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.api.WorkService
import com.pvp.app.common.SplashScreenUtil.useStyledExit
import com.pvp.app.ui.screen.layout.LayoutScreenBootstrap
import com.pvp.app.ui.screen.layout.LayoutViewModel
import com.pvp.app.ui.theme.CalendarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Activity : AppCompatActivity() {

    @Inject
    lateinit var workService: WorkService

    override fun onCreate(stateApp: Bundle?) {
        super.onCreate(stateApp)

        val screen = installSplashScreen()
            .useStyledExit {
                if (!areNotificationsEnabled(this)) {
                    showNotificationPermissionDialog(this)
                }
            }

        setContent {
            val model: LayoutViewModel = hiltViewModel()

            screen.setKeepOnScreenCondition { model.state.value.isLoading }

            CalendarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LayoutScreenBootstrap()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        workService.initiateActivityWorker()
    }

    private fun areNotificationsEnabled(context: Context): Boolean {
        val enabled = NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()

        if (!enabled) {
            return false
        }

        val manager = context.getSystemService(NotificationManager::class.java)

        for (channel in manager.notificationChannels) {
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                return false
            }
        }

        return true
    }

    private fun openNotificationSettingsForApp(context: Context) {
        val intent = Intent()
            .apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

                addCategory(Intent.CATEGORY_DEFAULT)

                data = Uri.parse("package:" + context.packageName)
            }

        context.startActivity(intent)
    }

    private fun showNotificationPermissionDialog(context: Context) {
        AlertDialog
            .Builder(context)
            .setTitle(applicationContext.getString(R.string.notifications_request_dialog_title))
            .setMessage(applicationContext.getString(R.string.notifications_request_dialog_message))
            .setPositiveButton(
                applicationContext.getString(R.string.notifications_request_dialog_button_settings)
            ) { _, _ ->
                openNotificationSettingsForApp(context)
            }
            .setNegativeButton(
                applicationContext.getString(R.string.notifications_request_dialog_button_cancel),
                null
            )
            .show()
    }
}
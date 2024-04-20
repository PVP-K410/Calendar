package com.pvp.app

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.ui.screen.layout.LayoutScreenBootstrap
import com.pvp.app.ui.theme.CalendarTheme
import com.pvp.app.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Activity : ComponentActivity() {

    override fun onCreate(stateApp: Bundle?) {
        super.onCreate(stateApp)

        installSplashScreen()
            .onAppStartDo {
                if (!isNotificationEnabled(this)) {
                    showNotificationPermissionDialog(this)
                }
            }


        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()

            CalendarTheme(model = themeViewModel) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LayoutScreenBootstrap()
                }
            }
        }
    }

    private fun isNotificationEnabled(context: Context): Boolean {
        val enabled = NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()

        if (!enabled) {
            return false
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
            .setTitle("Enable Notifications")
            .setMessage("Enable notifications to get reminders for tasks!")
            .setPositiveButton("Go to Settings") { _, _ ->
                openNotificationSettingsForApp(context)
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    private fun SplashScreen.onAppStartDo(block: () -> Unit) {
        fun rotate(
            screen: SplashScreenViewProvider,
            onEnd: () -> Unit
        ) {
            ObjectAnimator
                .ofFloat(
                    screen.iconView,
                    "rotationY",
                    0f,
                    360f
                )
                .apply {
                    duration = 1500

                    start()

                    doOnEnd { onEnd() }
                }
        }

        fun scale(
            property: String,
            screen: SplashScreenViewProvider
        ) {
            ObjectAnimator
                .ofFloat(
                    screen.iconView,
                    property,
                    1f,
                    0.2f,
                    0.75f,
                    0f
                )
                .apply {
                    duration = 1500

                    start()
                }
        }

        setOnExitAnimationListener { screen ->
            rotate(screen) {
                screen.remove()

                block()
            }

            scale(
                "scaleX",
                screen
            )

            scale(
                "scaleY",
                screen
            )
        }
    }
}
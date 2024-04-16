package com.pvp.app

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
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.screen.layout.LayoutScreenBootstrap
import com.pvp.app.ui.theme.CalendarTheme
import com.pvp.app.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Activity : ComponentActivity() {

    override fun onCreate(stateApp: Bundle?) {
        super.onCreate(stateApp)

        if (!isNotificationEnabled(this)) {
            showNotificationPermissionDialog(this)
        }

        prepareRoutes()

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            CalendarTheme (model = themeViewModel) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LayoutScreenBootstrap()
                }
            }
        }
    }

    /**
     * Initialize route collections to avoid lazy initialization problems
     */
    private fun prepareRoutes() {
        run {
            Route.routesAuthenticated
            Route.routesDrawer
            Route.routesUnauthenticated
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
        AlertDialog.Builder(context)
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
}
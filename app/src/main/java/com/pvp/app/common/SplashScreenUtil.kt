package com.pvp.app.common

import android.animation.ObjectAnimator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider

object SplashScreenUtil {

    /**
     * Use a styled exit animation for the splash screen and execute a block of code when the
     * animation ends.
     *
     * @param block The block of code to execute when the animation ends.
     *
     * @return The splash screen for chaining.
     */
    fun SplashScreen.useStyledExit(block: () -> Unit): SplashScreen {
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

        return this
    }
}
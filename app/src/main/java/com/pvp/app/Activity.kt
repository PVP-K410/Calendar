package com.pvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.pvp.app.ui.ApplicationScreen
import com.pvp.app.ui.view.vm.MainViewModel
import com.pvp.app.ui.screen.Layout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val viewModel by viewModels<MainViewModel>()

            viewModel.bootstrap()
        }

        setContent {
            Layout()
        }
    }
}
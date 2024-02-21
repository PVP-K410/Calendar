package com.pvp.app.ui.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.pvp.app.ui.ApplicationScreen
import com.pvp.app.ui.view.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val viewModel by viewModels<MainViewModel>()

            viewModel.bootstrap()
        }

        setContent {
            ApplicationScreen()
        }
    }
}
package com.meng.flasher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.meng.flasher.core.FlashStatus
import com.meng.flasher.core.FlashViewModel
import com.meng.flasher.ui.screen.AboutDialog
import com.meng.flasher.ui.screen.FlashScreen
import com.meng.flasher.ui.screen.HomeScreen
import com.meng.flasher.ui.theme.MengFlasherTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FlashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MengFlasherTheme {
                MengFlasherApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MengFlasherApp(viewModel: FlashViewModel) {
    val state by viewModel.state.collectAsState()
    var showAbout by remember { mutableStateOf(false) }

    // File picker launcher
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri -> viewModel.startFlash(uri) }
        }
        // Cancel → tetap di screen yang ada (tidak perlu action)
    }

    fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePicker.launch(intent)
    }

    // Navigasi: HOME jika IDLE, FLASH jika sudah mulai
    if (state.status == FlashStatus.IDLE) {
        HomeScreen(
            onStartFlash = { launchFilePicker() },
            onAbout = { showAbout = true }
        )
    } else {
        FlashScreen(
            state = state,
            onAbout = { showAbout = true },
            onReboot = { viewModel.reboot() },
            onFlashNew = {
                viewModel.reset()
                launchFilePicker()
            }
        )
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

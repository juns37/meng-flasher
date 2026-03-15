package com.meng.flasher.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meng.flasher.R
import com.meng.flasher.core.FlashState
import com.meng.flasher.core.FlashStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashScreen(
    state: FlashState,
    onAbout: () -> Unit,
    onReboot: () -> Unit,
    onFlashNew: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showRebootDialog by remember { mutableStateOf(false) }

    // Auto-show reboot dialog when done
    LaunchedEffect(state.status) {
        if (state.status == FlashStatus.DONE) showRebootDialog = true
    }

    // Auto scroll to bottom
    LaunchedEffect(state.logs.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val title = when (state.status) {
        FlashStatus.FLASHING      -> stringResource(R.string.flashing)
        FlashStatus.DONE          -> stringResource(R.string.flashing_done)
        FlashStatus.ERROR         -> stringResource(R.string.failed)
        FlashStatus.IDLE          -> stringResource(R.string.app_name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    IconButton(onClick = onAbout) {
                        Icon(Icons.Default.Info, contentDescription = stringResource(R.string.about))
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Log area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                state.logs.forEach { line ->
                    val color = when {
                        line.contains("error", ignoreCase = true) ||
                        line.contains("failed", ignoreCase = true) ->
                            MaterialTheme.colorScheme.error
                        line.contains("done", ignoreCase = true) ||
                        line.contains("selesai", ignoreCase = true) ->
                            MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onBackground
                    }
                    Text(
                        text = line,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = color,
                        lineHeight = 18.sp
                    )
                }
            }

            // Bottom bar - tombol Flash New jika tidak sedang flashing
            if (state.status != FlashStatus.FLASHING) {
                Surface(
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onFlashNew) {
                            Text(stringResource(R.string.flash_new))
                        }
                    }
                }
            }
        }
    }

    // Reboot dialog
    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            title = { Text(stringResource(R.string.reboot_complete_title)) },
            text = { Text(stringResource(R.string.reboot_complete_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    showRebootDialog = false
                    onReboot()
                }) {
                    Text(stringResource(R.string.yes), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRebootDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}

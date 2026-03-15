package com.meng.flasher.core

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FlashStatus { IDLE, FLASHING, DONE, ERROR }

data class FlashState(
    val status: FlashStatus = FlashStatus.IDLE,
    val logs: List<String> = emptyList(),
    val errorMsg: String = ""
)

class FlashViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(FlashState())
    val state = _state.asStateFlow()

    private var worker: Worker? = null

    fun startFlash(uri: Uri) {
        _state.value = FlashState(status = FlashStatus.FLASHING)

        worker = Worker(
            context = getApplication(),
            uri = uri,
            onLog = { line ->
                _state.value = _state.value.copy(
                    logs = _state.value.logs + line
                )
            },
            onDone = {
                _state.value = _state.value.copy(status = FlashStatus.DONE)
            },
            onError = { msg ->
                _state.value = _state.value.copy(
                    status = FlashStatus.ERROR,
                    errorMsg = msg
                )
            }
        ).also { it.start() }
    }

    fun reboot() { worker?.reboot() }

    fun reset() { _state.value = FlashState() }

    val isFlashing get() = _state.value.status == FlashStatus.FLASHING
}

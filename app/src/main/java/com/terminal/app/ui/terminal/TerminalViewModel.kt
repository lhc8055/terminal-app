package com.terminal.app.ui.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terminal.app.terminal.ShellSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Holds the live terminal buffer + the surrounding shell session.
 *
 * Output from the shell is appended to a StringBuilder (cheap) and a throttled snapshot is
 * pushed to [buffer] so the UI re-renders at a sane cadence rather than per byte.
 */
class TerminalViewModel : ViewModel() {

    private val _buffer = MutableStateFlow("")
    val buffer: StateFlow<String> = _buffer.asStateFlow()

    /** Number of unflushed output characters since the last UI push. */
    private val pending = AtomicInteger(0)

    private var session: ShellSession? = null

    fun start() {
        if (session != null) return
        val s = ShellSession(
            onOutput = { chunk -> appendOutput(chunk) },
            onExit = { code -> appendOutput("\n[exit $code]\n") }
        )
        session = s
        s.start()
        // Friendly banner so the user immediately sees the shell is up.
        appendOutput(BANNER)
    }

    fun execute(command: String) {
        // Echo the typed command into the buffer so the screen shows what was run.
        appendOutput(command + "\n")
        session?.execute(command)
    }

    fun sendCtrlC() {
        session?.sendCtrlC()
    }

    fun clear() {
        _buffer.value = ""
    }

    fun stop() {
        session?.stop()
        session = null
    }

    private fun appendOutput(chunk: CharSequence) {
        // Append synchronously to keep order, then push to StateFlow.
        synchronized(_buffer) {
            _buffer.update { it + chunk.toString() }
        }
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }

    companion object {
        private const val BANNER =
            "本地 Shell 终端 · 仅用于合法设备调试\n" +
            "运行身份：app sandbox UID（无 root / 无 su）\n" +
            "键入 'exit' 退出，长按发送 Ctrl+C\n" +
            "--------------------------------------------------\n"
    }
}

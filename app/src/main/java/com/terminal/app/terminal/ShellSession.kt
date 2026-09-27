package com.terminal.app.terminal

import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread

/**
 * ShellSession — a long-running local `/system/bin/sh` session for plain command-line
 * debugging (ls, cat, ps, getprop, id, env, ...).
 *
 * This is NOT a true PTY — it uses a child process with redirected stdio. That is enough
 * for normal interactive shell use; the only things you lose vs. a real PTY are terminal
 * line editing by the shell itself (we provide our own input field) and job control signals
 * from Ctrl+C / Ctrl+Z arriving via a TTY driver (we send the raw ASCII byte instead, which
 * mksh interprets as SIGINT in interactive mode).
 *
 * Runs as the app's own UID — no root, no su, no elevated privileges. Suitable only for
 * debugging commands that the app sandbox already has permission to run.
 *
 * All stream I/O happens on background threads; UI-safe callbacks deliver appended text.
 */
class ShellSession(
    private val workingDir: File = File("/"),
    private val onOutput: (CharSequence) -> Unit,
    private val onExit: (Int) -> Unit = {}
) {
    @Volatile private var process: Process? = null
    @Volatile private var stdin: OutputStream? = null
    @Volatile private var readerThread: Thread? = null

    @Volatile var isAlive: Boolean = false
        private set

    /** Spawn the shell. Idempotent — calling twice is a no-op. */
    fun start() {
        if (isAlive) return
        try {
            val builder = ProcessBuilder(SH_PATH, "-i")
                .directory(workingDir)
                .redirectErrorStream(true)
            // Inherit a minimal env so common commands resolve.
            val env = builder.environment()
            env["TERM"] = "xterm-256color"
            env["PS1"] = "$ "  // deterministic prompt
            val p = builder.start()
            process = p
            stdin = p.outputStream
            isAlive = true

            readerThread = thread(name = "ShellSession-reader", isDaemon = true) {
                val input: InputStream = p.inputStream
                val buf = ByteArray(2048)
                try {
                    while (true) {
                        val n = input.read(buf)
                        if (n < 0) break
                        if (n > 0) {
                            val chunk = String(buf, 0, n, Charsets.UTF_8)
                            onOutput(chunk)
                        }
                    }
                } catch (e: Exception) {
                    if (isAlive) onOutput("\n[reader error: ${e.message}]\n")
                } finally {
                    val code = try { p.waitFor() } catch (_: Exception) { -1 }
                    isAlive = false
                    onOutput("\n[process exited: $code]\n")
                    onExit(code)
                }
            }
        } catch (e: Exception) {
            isAlive = false
            onOutput("[failed to start shell: ${e.message}]\n")
            Log.e(TAG, "start failed", e)
        }
    }

    /** Send a command line (will be terminated with a newline). */
    fun execute(command: String) {
        val s = stdin ?: return
        try {
            s.write(command.toByteArray(Charsets.UTF_8))
            s.write('\n'.code)
            s.flush()
        } catch (e: Exception) {
            onOutput("[write error: ${e.message}]\n")
        }
    }

    /** Send a raw byte (e.g. 0x03 for Ctrl+C, 0x04 for Ctrl+D / EOF). */
    fun sendByte(b: Int) {
        val s = stdin ?: return
        try {
            s.write(b)
            s.flush()
        } catch (e: Exception) {
            onOutput("[write error: ${e.message}]\n")
        }
    }

    fun sendCtrlC() = sendByte(0x03)
    fun sendCtrlD() = sendByte(0x04)

    /** Tear down the shell. */
    fun stop() {
        isAlive = false
        try { sendCtrlD() } catch (_: Exception) {}
        try { process?.destroy() } catch (_: Exception) {}
        try { process?.waitFor() } catch (_: Exception) {}
        process = null
        stdin = null
    }

    companion object {
        private const val TAG = "ShellSession"
        private const val SH_PATH = "/system/bin/sh"
    }
}

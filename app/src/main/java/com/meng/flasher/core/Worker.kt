package com.meng.flasher.core

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.*

class Worker(
    private val context: Context,
    val uri: Uri,
    private val onLog: (String) -> Unit,
    private val onDone: () -> Unit,
    private val onError: (String) -> Unit
) : Thread() {

    private val filesDir = context.filesDir.absolutePath
    private lateinit var filePath: String
    private lateinit var binaryPath: String

    override fun run() {
        filePath = "$filesDir/${DocumentFile.fromSingleUri(context, uri)?.name}"
        binaryPath = "$filesDir/META-INF/com/google/android/update-binary"

        runStep({ cleanup() }, "Tidak dapat membersihkan lingkungan") ?: return
        if (!rootAvailable()) { onError("Tidak dapat memperoleh izin root"); return }
        runStep({ copy() }, "Tidak dapat menyalin file") ?: return
        if (!File(filePath).exists()) { onError("Tidak dapat menyalin file"); return }
        runStep({ getBinary() }, "Tidak bisa mendapatkan file yang dapat dieksekusi") ?: return
        try { patch() } catch (_: IOException) {}
        runStep({ flash() }, "Terjadi kesalahan saat mem-flash") ?: return
        onDone()
    }

    private fun runStep(action: () -> Unit, errorMsg: String): Unit? {
        return try { action(); Unit }
        catch (e: IOException) { onError(errorMsg); null }
    }

    private fun cleanup() {
        exec(su = false, cmd = "rm -rf $filesDir/*")
    }

    private fun copy() {
        context.contentResolver.openInputStream(uri)!!.use { input ->
            FileOutputStream(File(filePath)).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun getBinary() {
        exec(su = false, cmd = "unzip \"$filePath\" \"*/update-binary\" -d $filesDir")
        if (!File(binaryPath).exists()) throw IOException("binary not found")
    }

    private fun patch() {
        val mkbootfsPath = "$filesDir/mkbootfs"
        AssetsUtil.exportFiles(context, "mkbootfs", mkbootfsPath)
        exec(su = false, cmd = "sed -i '/\$BB chmod -R 755 tools bin;/i cp -f $mkbootfsPath \$AKHOME/tools;' $binaryPath")
    }

    private fun flash() {
        val process = ProcessBuilder("su").redirectErrorStream(true).start()
        val writer = OutputStreamWriter(process.outputStream)
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        writer.write("export POSTINSTALL=$filesDir\n")
        writer.write("sh $binaryPath 3 1 \"$filePath\"&& touch $filesDir/done\nexit\n")
        writer.flush()

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            line?.let { if (it.startsWith("ui_print")) onLog(it.replace("ui_print", "")) }
        }

        reader.close(); writer.close(); process.destroy()
        if (!File("$filesDir/done").exists()) throw IOException("flash failed")
    }

    fun reboot() {
        exec(su = true, cmd = "svc power reboot")
    }

    fun rootAvailable(): Boolean {
        return try {
            val result = execReturn(su = true, cmd = "id")
            result?.contains("root") == true
        } catch (e: IOException) { false }
    }

    private fun exec(su: Boolean, cmd: String) { execReturn(su, cmd) }

    private fun execReturn(su: Boolean, cmd: String): String? {
        val process = ProcessBuilder(if (su) "su" else "sh").redirectErrorStream(true).start()
        val writer = OutputStreamWriter(process.outputStream)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        writer.write("$cmd\nexit\n"); writer.flush()
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) sb.appendLine(line)
        writer.close(); reader.close(); process.destroy()
        return sb.toString()
    }
}

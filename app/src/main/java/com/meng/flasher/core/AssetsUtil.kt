package com.meng.flasher.core

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object AssetsUtil {
    fun exportFiles(context: Context, assetPath: String, destPath: String) {
        val files = context.assets.list(assetPath)
        if (!files.isNullOrEmpty()) {
            File(destPath).mkdirs()
            files.forEach { exportFiles(context, "$assetPath/$it", "$destPath/$it") }
        } else {
            context.assets.open(assetPath).use { input ->
                FileOutputStream(File(destPath)).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

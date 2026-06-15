package com.rekernel.manager.kernel

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object KernelDetector {

    suspend fun detect(): KernelStatus = withContext(Dispatchers.IO) {
        var status = KernelStatus(isChecking = true)

        try {
            val procVersion = File("/proc/version")
            if (procVersion.canRead()) {
                val version = procVersion.readText().trim()
                status = status.copy(kernelVersion = extractKernelVersion(version))
            }

            val rekernelProc = File("/proc/rekernel")
            if (rekernelProc.canRead()) {
                val content = rekernelProc.readText()
                status = status.copy(
                    rekernelLoaded = true,
                    rekernelVersion = extractRekernelVersion(content),
                    rekernelAuthor = extractRekernelAuthor(content)
                )
            }

            if (rekernelProc.exists() && !rekernelProc.canRead()) {
                try {
                    val shell = Shell.getShell()
                    if (shell.isRoot) {
                        val result = Shell.cmd("cat /proc/rekernel").exec()
                        if (result.isSuccess) {
                            val content = result.out.joinToString("\n")
                            status = status.copy(
                                rekernelLoaded = true,
                                rekernelVersion = extractRekernelVersion(content),
                                rekernelAuthor = extractRekernelAuthor(content)
                            )
                        }
                    }
                } catch (_: Exception) { }
            }

            try {
                val shell = Shell.getShell()
                if (shell.isRoot) {
                    status = status.copy(
                        follkernelLoaded = true,
                        rootAvailable = true
                    )
                }
            } catch (_: Exception) {
                try {
                    val result = Shell.cmd("su -c id").exec()
                    if (result.isSuccess && result.out.any { it.contains("uid=0") }) {
                        status = status.copy(
                            follkernelLoaded = true,
                            rootAvailable = true
                        )
                    }
                } catch (_: Exception) { }
            }
        } catch (e: Exception) {
            status = status.copy(checkError = e.message)
        }

        status.copy(isChecking = false)
    }

    private fun extractKernelVersion(raw: String): String {
        val match = Regex("Linux version ([^\\s]+)").find(raw)
        return match?.groupValues?.get(1) ?: raw.take(50)
    }

    private fun extractRekernelVersion(content: String): String {
        val match = Regex("Version:\\s*(.+)").find(content)
        return match?.groupValues?.get(1)?.trim() ?: "Unknown"
    }

    private fun extractRekernelAuthor(content: String): String {
        val match = Regex("Author:\\s*(.+)").find(content)
        return match?.groupValues?.get(1)?.trim() ?: ""
    }
}

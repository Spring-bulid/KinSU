package com.rekernel.manager.kernel

data class KernelStatus(
    val rekernelLoaded: Boolean = false,
    val rekernelVersion: String = "",
    val rekernelAuthor: String = "",
    val follkernelLoaded: Boolean = false,
    val kernelVersion: String = "",
    val rootAvailable: Boolean = false,
    val isChecking: Boolean = false,
    val checkError: String? = null
)

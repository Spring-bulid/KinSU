package com.rekernel.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rekernel.manager.kernel.KernelDetector
import com.rekernel.manager.kernel.KernelStatus
import com.rekernel.manager.ui.theme.AppSettings
import com.rekernel.manager.ui.theme.ColorMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppSettings())
    val uiState: StateFlow<AppSettings> = _uiState

    private val _selectedPage = MutableStateFlow(0)
    val selectedPage: StateFlow<Int> = _selectedPage

    private val _kernelStatus = MutableStateFlow(KernelStatus())
    val kernelStatus: StateFlow<KernelStatus> = _kernelStatus

    init {
        checkKernel()
    }

    fun setSelectedPage(page: Int) {
        _selectedPage.value = page
    }

    fun setColorMode(mode: ColorMode) {
        _uiState.value = _uiState.value.copy(colorMode = mode)
    }

    fun setUiMode(uiMode: String) {
        _uiState.value = _uiState.value.copy(uiMode = uiMode)
    }

    fun checkKernel() {
        viewModelScope.launch {
            _kernelStatus.value = KernelDetector.detect()
        }
    }
}

package me.weishu.follkernel.data.repository

import me.weishu.follkernel.data.model.Module
import me.weishu.follkernel.data.model.ModuleUpdateInfo

interface ModuleRepository {
    suspend fun getModules(): Result<List<Module>>
    suspend fun checkUpdate(module: Module): Result<ModuleUpdateInfo>
}

package me.weishu.follkernel.data.repository

import me.weishu.follkernel.data.model.RepoModule

interface ModuleRepoRepository {
    suspend fun fetchModules(): Result<List<RepoModule>>
}

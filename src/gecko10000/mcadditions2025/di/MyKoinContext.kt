package gecko10000.mcadditions2025.di

import gecko10000.mcadditions2025.MCAdditions2025
import org.koin.core.Koin
import org.koin.dsl.koinApplication

object MyKoinContext {
    internal lateinit var koin: Koin
    fun init(plugin: MCAdditions2025) {
        koin = koinApplication(createEagerInstances = false) {
            modules(pluginModules(plugin))
        }.koin
        koin.createEagerInstances()
    }
}

package gecko10000.mcadditions2025.di

import gecko10000.mcadditions2025.MCAdditions2025
import gecko10000.mcadditions2025.addons.disableirongolems.DisableIronGolems
import gecko10000.mcadditions2025.addons.holloweggs.HollowEggs
import gecko10000.mcadditions2025.addons.partialkeepinv.PartialKeepInv
import gecko10000.mcadditions2025.addons.spawnermarathoner.SpawnerMarathoner
import org.koin.dsl.module

fun pluginModules(plugin: MCAdditions2025) = module {
    single { plugin }
    single(createdAtStart = true) { SpawnerMarathoner() }
    single(createdAtStart = true) { HollowEggs() }
    single(createdAtStart = true) { DisableIronGolems() }
    single(createdAtStart = true) { PartialKeepInv() }
}

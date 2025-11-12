package gecko10000.mcadditions2025.config

import kotlinx.serialization.Serializable

@Serializable
data class ConfigHolder(
    val spawnerMarathoner: gecko10000.mcadditions2025.addons.spawnermarathoner.Config
    = gecko10000.mcadditions2025.addons.spawnermarathoner.Config(),
)

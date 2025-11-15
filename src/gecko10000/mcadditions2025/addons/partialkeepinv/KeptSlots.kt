package gecko10000.mcadditions2025.addons.partialkeepinv

import kotlinx.serialization.Serializable

@Serializable
data class KeptSlots(
    val slots: List<Int> = listOf(),
)

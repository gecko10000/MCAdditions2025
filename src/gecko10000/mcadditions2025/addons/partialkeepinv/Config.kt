package gecko10000.mcadditions2025.addons.partialkeepinv

import gecko10000.geckolib.extensions.MM
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

@Serializable
data class Config(
    private val inventoryName: String = "<dark_green>Keepinv slots (<remaining> left)"
) {
    fun inventoryName(remaining: Int): Component {
        return MM.deserialize(
            inventoryName,
            Placeholder.unparsed("remaining", remaining.toString())
        )
    }
}

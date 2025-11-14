package gecko10000.mcadditions2025.addons.holloweggs

import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.withDefaults
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.EntityType
import org.bukkit.entity.EntityType.*

@Serializable
data class Config(
    val nexoItemPrefix: String = "hollow_egg_",
    val requiredCatchCount: Int = 16,
    private val eggName: String = "<dark_green>Hollow Egg (<green><caught>/<total> <entity></green>)",
    private val eggLore: List<String> = listOf(
        "<dark_aqua><aqua><remaining></aqua> <entity> remaining"
    ),
    val catchableMobs: Set<EntityType> = setOf(
        BLAZE,
        BREEZE,
        CHICKEN,
        COW,
        CREEPER,
        DROWNED,
        ENDERMAN,
        GLOW_SQUID,
        GOAT,
        GUARDIAN,
        HOGLIN,
        HORSE,
        IRON_GOLEM,
        LLAMA,
        MAGMA_CUBE,
        MOOSHROOM,
        PHANTOM,
        PIG,
        PILLAGER,
        RABBIT,
        SHEEP,
        SKELETON,
        SLIME,
        SPIDER,
        SQUID,
        WITCH,
        ZOMBIE,
        ZOMBIFIED_PIGLIN,
    )
) {

    private fun doPlaceholders(input: String, entityKey: String, caught: Int): Component {
        val remaining = requiredCatchCount - caught
        return MM.deserialize(
            input,
            Placeholder.component("entity", Component.translatable(entityKey)),
            Placeholder.unparsed("caught", caught.toString()),
            Placeholder.unparsed("remaining", remaining.toString()),
            Placeholder.unparsed("total", requiredCatchCount.toString())
        ).withDefaults()
    }

    fun eggName(entityKey: String, caught: Int) = doPlaceholders(eggName, entityKey, caught)
    fun eggLore(entityKey: String, caught: Int) = eggLore.map { doPlaceholders(it, entityKey, caught) }
}

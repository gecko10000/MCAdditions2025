package gecko10000.mcadditions2025.addons.holloweggs

import com.destroystokyo.paper.ParticleBuilder
import com.nexomc.nexo.api.NexoItems
import gecko10000.geckolib.extensions.MM
import gecko10000.mcadditions2025.MCAdditions2025
import gecko10000.mcadditions2025.di.MyKoinComponent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.entity.Snowball
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class HollowEggs : Listener, MyKoinComponent {

    private val plugin: MCAdditions2025 by inject()
    private val config: Config
        get() = plugin.config.hollowEggs
    private val LAUNCHER_KEY = NamespacedKey(plugin, "launcher_id")
    private val MOB_KEY = NamespacedKey(plugin, "egg_mob")
    private val COUNT_KEY = NamespacedKey(plugin, "egg_count")

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    // Throws it like an egg.
    @EventHandler
    private fun PlayerInteractEvent.onThrowHollowEgg() {
        val action = this.action
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return
        val hand = this.hand ?: return
        if (this.useItemInHand() == Event.Result.DENY) return
        val usedItem = this.item ?: return
        val nexoId = NexoItems.idFromItem(usedItem) ?: return
        if (!nexoId.startsWith(config.nexoItemPrefix)) return
        this.isCancelled = true
        val snowballItem = usedItem.clone()
        this.player.inventory.setItem(hand, usedItem.subtract())
        player.world.playSound(Sound.sound { it.type(Key.key("entity.snowball.throw")) }, player)
        player.launchProjectile(Snowball::class.java, null) {
            it.item = snowballItem
            it.persistentDataContainer.set(LAUNCHER_KEY, PersistentDataType.STRING, player.uniqueId.toString())
        }
    }

    private fun failReturn(egg: Snowball, uuid: UUID) {
        val warningMessage = String.format(
            "Could not return hollow egg {} to player {} (not online)",
            egg.item, uuid
        )
        plugin.logger.warning(warningMessage)
    }

    private fun tryReturn(egg: Snowball, message: Component?) {
        val launcherUUIDString = egg.persistentDataContainer.get(LAUNCHER_KEY, PersistentDataType.STRING) ?: return
        val launcherUUID = UUID.fromString(launcherUUIDString)
        val launcher = plugin.server.getPlayer(launcherUUID)
        if (launcher == null) {
            failReturn(egg, launcherUUID)
            return
        }
        message?.let { launcher.sendMessage(it) }
        launcher.give(egg.item)
    }

    @EventHandler(ignoreCancelled = true)
    private fun ProjectileHitEvent.onHollowEggHit() {
        val egg = this.entity as? Snowball ?: return

        // Some other snowball
        if (!egg.persistentDataContainer.has(LAUNCHER_KEY)) {
            return
        }

        this.isCancelled = true
        val item = egg.item.clone()
        egg.remove()

        // Nothing was hit
        val hitEntity = this.hitEntity
        if (hitEntity == null) {
            tryReturn(egg, null)
            return
        }

        // Uncatchable mob
        val hitType = hitEntity.type
        if (hitType !in config.catchableMobs) {
            val message = MM.deserialize(
                "<red>You can't catch a <mob>.",
                Placeholder.component("mob", Component.translatable(hitType.translationKey()))
            )
            tryReturn(
                egg,
                message,
            )
            return
        }

        // Spawn egg doesn't exist for some reason
        val spawnEgg = plugin.server.itemFactory.getSpawnEgg(hitType)
        if (spawnEgg == null) {
            plugin.logger.severe("Spawn egg does not exist for $hitType")
            tryReturn(egg, null)
            return
        }

        // Incorrect mob
        val storedMobType = item.persistentDataContainer.get(MOB_KEY, PersistentDataType.STRING)
        val translationKey = hitType.translationKey()
        if (storedMobType != null && translationKey != storedMobType) {
            tryReturn(
                egg, MM.deserialize(
                    "<red>This egg is bound to <mob>!",
                    Placeholder.component("mob", Component.translatable(storedMobType))
                )
            )
            return
        }

        val caughtMobs = item.persistentDataContainer.getOrDefault(COUNT_KEY, PersistentDataType.INTEGER, 0)
        val newAmount = caughtMobs + 1
        val droppedItem: ItemStack
        if (newAmount >= config.requiredCatchCount)
            droppedItem = ItemStack.of(spawnEgg)
        else {
            droppedItem = item
            droppedItem.editMeta { meta ->
                meta.persistentDataContainer.set(MOB_KEY, PersistentDataType.STRING, translationKey)
                meta.persistentDataContainer.set(COUNT_KEY, PersistentDataType.INTEGER, newAmount)
                meta.itemName(config.eggName(translationKey, newAmount))
                meta.lore(config.eggLore(translationKey, newAmount))
            }
        }
        hitEntity.remove()
        val itemLoc = egg.location.clone()
        egg.world.dropItem(itemLoc, droppedItem)
        for (i in 0..<100) {
            val rand = Random.nextDouble() * 2 * PI
            val x = sin(rand) * Random.nextDouble()
            val y = cos(rand) * Random.nextDouble()
            val loc = hitEntity.location.clone().add(0.0, 0.1, 0.0)
            ParticleBuilder(Particle.SOUL)
                .location(loc)
                .count(0)
                .offset(x, 0.0, y)
                .extra(0.1)
                .spawn()
        }
    }

}

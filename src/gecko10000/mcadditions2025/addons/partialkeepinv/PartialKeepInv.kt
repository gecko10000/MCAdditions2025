package gecko10000.mcadditions2025.addons.partialkeepinv

import gecko10000.mcadditions2025.MCAdditions2025
import gecko10000.mcadditions2025.di.MyKoinComponent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject

class PartialKeepInv : Listener, MyKoinComponent {

    private companion object {
        const val PERMISSION_PREFIX = "partialkeepinv.amount."
        val json = Json
    }

    private val plugin: MCAdditions2025 by inject()

    private val SLOTS_KEY = NamespacedKey(plugin, "keepinv_slots")

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun PlayerDeathEvent.onPlayerDeath() {
        val keptSlots = getEnabledSlots(this.player)
        val keptSlotCount = getAvailableSlotCount(this.player)
        val actualKeptSlots = keptSlots.slots.take(keptSlotCount)
        for (i in actualKeptSlots) {
            val itemInSlot = this.player.inventory.getItem(i) ?: continue
            if (this.drops.remove(itemInSlot)) {
                this.itemsToKeep.add(itemInSlot)
            }
        }
    }

    fun disableSlot(player: Player, slot: Int) {
        val keptSlots = getEnabledSlots(player)
        val newKeptSlots = keptSlots.copy(slots = keptSlots.slots.minus(slot))
        setEnabledSlots(player, newKeptSlots)
    }

    fun tryEnableSlot(player: Player, slot: Int) {
        val keptSlots = getEnabledSlots(player)
        val total = getAvailableSlotCount(player)
        if (keptSlots.slots.size >= total) return
        val newKeptSlots = keptSlots.copy(slots = keptSlots.slots.plus(slot).distinct())
        setEnabledSlots(player, newKeptSlots)
    }

    fun getEnabledSlots(player: Player): KeptSlots {
        val keptSlotsJson = player.persistentDataContainer.get(SLOTS_KEY, PersistentDataType.STRING)
        keptSlotsJson ?: return KeptSlots()
        return json.decodeFromString<KeptSlots>(keptSlotsJson)
    }

    private fun setEnabledSlots(player: Player, keptSlots: KeptSlots) {
        val keptSlotsJson = json.encodeToString(keptSlots)
        player.persistentDataContainer.set(SLOTS_KEY, PersistentDataType.STRING, keptSlotsJson)
    }

    fun getAvailableSlotCount(player: Player): Int {
        return player.effectivePermissions
            .filter { it.value }
            .map { it.permission }
            .filter { it.startsWith(PERMISSION_PREFIX) }
            .map { it.substringAfter(PERMISSION_PREFIX) }
            .mapNotNull { it.toIntOrNull() }
            .maxOrNull() ?: 0
    }

}

package gecko10000.mcadditions2025.addons.partialkeepinv

import com.nexomc.nexo.api.NexoItems
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.mcadditions2025.MCAdditions2025
import gecko10000.mcadditions2025.di.MyKoinComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject

class KeepInvGUI(player: Player) : GUI(player), MyKoinComponent {

    private companion object {
        const val SIZE = 54
    }

    private val plugin: MCAdditions2025 by inject()
    private val partialKeepInv: PartialKeepInv by inject()

    private val config: Config
        get() = plugin.config.partialKeepInv

    override fun createInventory(): InventoryGUI {
        val keptSlots = partialKeepInv.getEnabledSlots(player)
        val allowedSlots = partialKeepInv.getAvailableSlotCount(player)
        val remaining = allowedSlots - keptSlots.slots.size
        val inventory = InventoryGUI(Bukkit.createInventory(this, SIZE, config.inventoryName(remaining)))
        inventory.fill(0, SIZE, FILLER)
        for (i in 0..<9) {
            inventory.addButton(button(keptSlots, i, null), i + 45)
        }
        for (i in 0..<27) {
            inventory.addButton(button(keptSlots, i + 9, null), i + 18)
        }
        for (i in 0..<4) {
            val slot = when (i) {
                0 -> EquipmentSlot.HEAD
                1 -> EquipmentSlot.CHEST
                2 -> EquipmentSlot.LEGS
                3 -> EquipmentSlot.FEET
                else -> throw Exception("What")
            }
            inventory.addButton(button(keptSlots, i + 36, slot), i)
        }
        inventory.addButton(button(keptSlots, 40, EquipmentSlot.OFF_HAND), 6)

        return inventory
    }

    private fun button(keptSlots: KeptSlots, slot: Int, equipmentSlot: EquipmentSlot?): ItemButton {
        val isSlotEnabled = keptSlots.slots.contains(slot)
        val item: ItemStack
        if (equipmentSlot != null) {
            val nexoItemId = equipmentSlot.name.lowercase() + "_" + (if (isSlotEnabled) "green" else "red")
            item = NexoItems.itemFromId(nexoItemId)!!.build()
        } else if (isSlotEnabled) {
            item = ItemStack.of(Material.LIME_STAINED_GLASS_PANE)
        } else {
            item = ItemStack.of(Material.RED_STAINED_GLASS_PANE)
        }
        item.editMeta {
            it.isHideTooltip = true
        }
        return ItemButton.create(item) {
            if (isSlotEnabled) {
                partialKeepInv.disableSlot(player, slot)
            } else {
                partialKeepInv.tryEnableSlot(player, slot)
            }
            KeepInvGUI(player)
        }
    }

}

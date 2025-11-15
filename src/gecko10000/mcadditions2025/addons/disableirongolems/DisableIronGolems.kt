package gecko10000.mcadditions2025.addons.disableirongolems

import gecko10000.mcadditions2025.MCAdditions2025
import gecko10000.mcadditions2025.di.MyKoinComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.koin.core.component.inject

class DisableIronGolems : Listener, MyKoinComponent {

    private val plugin: MCAdditions2025 by inject()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(ignoreCancelled = true)
    private fun CreatureSpawnEvent.onIronGolemSpawn() {
        if (this.spawnReason != CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE) return
        this.isCancelled = true
    }

}

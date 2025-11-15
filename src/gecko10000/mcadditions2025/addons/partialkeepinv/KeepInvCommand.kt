package gecko10000.mcadditions2025.addons.partialkeepinv

import net.strokkur.commands.annotations.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("keepinv")
@Aliases("ki")
@Permission("partialkeepinv.command")
class KeepInvCommand {

    @Executes
    fun baseCommand(sender: CommandSender, @Executor player: Player) {
        KeepInvGUI(player)
    }
}

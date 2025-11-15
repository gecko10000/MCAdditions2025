package gecko10000.mcadditions2025

import gecko10000.geckolib.extensions.parseMM
import net.strokkur.commands.annotations.Aliases
import net.strokkur.commands.annotations.Command
import net.strokkur.commands.annotations.Executes
import net.strokkur.commands.annotations.Permission
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

@Command("mcadditions")
@Aliases("mca")
@Permission("mcadditions.command")
class MCAdditionsCommand {

    private val plugin = JavaPlugin.getPlugin(MCAdditions2025::class.java)

    @Executes("reload")
    fun onReload(sender: CommandSender) {
        plugin.reloadConfigs()
        sender.sendMessage(parseMM("<green>Reloaded configs."))
    }

}

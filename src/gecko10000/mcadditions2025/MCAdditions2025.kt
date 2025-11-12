package gecko10000.mcadditions2025

import gecko10000.geckolib.config.YamlFileManager
import gecko10000.mcadditions2025.config.ConfigHolder
import gecko10000.mcadditions2025.di.MyKoinContext
import org.bukkit.plugin.java.JavaPlugin

class MCAdditions2025 : JavaPlugin() {

    private val configFile: YamlFileManager<ConfigHolder> = YamlFileManager(
        configDirectory = this.dataFolder,
        initialValue = ConfigHolder(),
        serializer = ConfigHolder.serializer(),
    )
    val config: ConfigHolder
        get() = configFile.value

    override fun onEnable() {
        MyKoinContext.init(this)
    }

}

package net.craftoriya

import org.bukkit.plugin.java.JavaPlugin
import net.craftoriya.packetmenuapi.PacketUxUiAPI

class TestMenu : JavaPlugin() {

    override fun onEnable() {
        PacketUxUiAPI.init()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}

package net.craftoriya

import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.craftoriya.commands.CommandListener
import net.craftoriya.packetuxui.PacketUxUiAPI
import org.bukkit.plugin.java.JavaPlugin

class TestMenu : JavaPlugin() {

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
    }

    override fun onEnable() {
        PacketUxUiAPI.init()
        val service = PacketUxUiAPI.getService()
        CommandListener(this, service)
    }

    override fun onDisable() {
        PacketUxUiAPI.terminate()
    }
}

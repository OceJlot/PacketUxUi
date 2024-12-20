package net.craftoriya

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.craftoriya.commands.CommandListener
import net.craftoriya.packetuxui.bukkit.PacketUxUiBukkit
import org.bukkit.plugin.java.JavaPlugin

val plugin: SuspendingJavaPlugin
    get() = JavaPlugin.getPlugin(TestMenu::class.java)

class TestMenu : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
    }

    override suspend fun onEnableAsync() {
        PacketUxUiBukkit.init()

        // Commands
        CommandListener
    }

    override suspend fun onDisableAsync() {
        PacketUxUiBukkit.terminate()
    }


}

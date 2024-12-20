package net.craftoriya.packetuxui.bukkit

import com.github.retrooper.packetevents.PacketEvents
import net.craftoriya.packetuxui.PacketUxUiApi
import net.craftoriya.packetuxui.bukkit.controller.BukkitListener
import net.craftoriya.packetuxui.bukkit.user.BukkitUser
import net.craftoriya.packetuxui.user.UserManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object PacketUxUiBukkit : PacketUxUiApi() {

    override fun initPlatform() {
        UserManager.userCreator = { uuid -> BukkitUser(uuid) }

        val packetEvents = PacketEvents.getAPI()
        val plugin = packetEvents.plugin as JavaPlugin

        Bukkit.getPluginManager().registerEvents(BukkitListener, plugin)
    }

    override fun terminatePlatform() {

    }

}
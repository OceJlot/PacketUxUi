package net.craftoriya.packetuxui.bukkit.controller

import net.craftoriya.packetuxui.bukkit.extensions.toUser
import net.craftoriya.packetuxui.service.menuService
import net.craftoriya.packetuxui.user.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object BukkitListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        menuService.onCloseMenu(event.player.toUser())
        UserManager.remove(event.player.uniqueId)
    }
}
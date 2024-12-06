package net.craftoriya.packetuxui.controller

import net.craftoriya.packetuxui.service.MenuService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class BukkitListener(private val service: MenuService) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent){
        service.onCloseMenu(event.player)
    }
}
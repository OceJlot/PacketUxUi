package net.craftoriya.packetmenuapi.controller

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import net.craftoriya.packetmenuapi.controller.menu.MenuListener
import net.craftoriya.packetmenuapi.service.MenuService


class PacketListener(menuService: MenuService) : PacketListener {
    private val menuListener = MenuListener(menuService)

    override fun onPacketReceive(event: PacketReceiveEvent) {
        println("WORKING!")
        menuListener.onClickWindow(event)
    }
}
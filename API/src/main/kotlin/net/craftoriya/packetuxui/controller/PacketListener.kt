package net.craftoriya.packetuxui.controller

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import net.craftoriya.packetuxui.controller.menu.MenuListener
import net.craftoriya.packetuxui.service.MenuService


class PacketListener(menuService: MenuService) : PacketListener {
    private val menuListener = MenuListener(menuService)

    override fun onPacketReceive(event: PacketReceiveEvent) {
        menuListener.onClickWindow(event)
    }
}
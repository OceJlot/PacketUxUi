package net.craftoriya.packetuxui.controller

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import net.craftoriya.packetuxui.controller.menu.MenuListener

object PacketListener : PacketListener {

    override fun onPacketReceive(event: PacketReceiveEvent) {
        MenuListener.onClickWindow(event)
    }
}
package net.craftoriya.packetuxui.controller

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import net.craftoriya.packetuxui.controller.menu.MenuListener

object PacketListener : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {

    override fun onPacketReceive(event: PacketReceiveEvent) {
        MenuListener.onClickWindow(event)
    }
}
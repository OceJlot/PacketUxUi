package net.craftoriya.packetuxui.user

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import net.kyori.adventure.text.Component
import java.util.*

interface User {

    val uuid: UUID

    fun updateInventory()

    val packetUser: User?
    val player: Any?

    fun sendPacket(wrapper: PacketWrapper<*>) = this.packetUser?.sendPacket(wrapper)

    fun receivePacket(wrapper: PacketWrapper<*>) =
        PacketEvents.getAPI().playerManager.receivePacketSilently(player, wrapper)

    fun sendMessage(message: Component)
}
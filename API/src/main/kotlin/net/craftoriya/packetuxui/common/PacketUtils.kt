package net.craftoriya.packetuxui.common

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PacketUtils {
    companion object {
        fun Player.sendPacket(wrapper: PacketWrapper<*>) = this.user.sendPacket(wrapper)

        fun Player.receivePacket(wrapper: PacketWrapper<*>) =
            PacketEvents.getAPI().playerManager.receivePacketSilently(this, wrapper)

        val User.player: Player? get() = this.uuid?.let { Bukkit.getPlayer(it) }
        val Player.user: User get() = PacketEvents.getAPI().playerManager.getUser(this)
        fun ItemStack.toPacketItemStack(): com.github.retrooper.packetevents.protocol.item.ItemStack {
            return SpigotConversionUtil.fromBukkitItemStack(this)
        }
        fun com.github.retrooper.packetevents.protocol.item.ItemStack.toBukkitItemStack(): ItemStack {
            return SpigotConversionUtil.toBukkitItemStack(this)
        }

    }
}
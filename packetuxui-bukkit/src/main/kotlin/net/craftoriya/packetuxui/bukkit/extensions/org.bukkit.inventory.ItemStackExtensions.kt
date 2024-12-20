package net.craftoriya.packetuxui.bukkit.extensions

import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.inventory.ItemStack

fun ItemStack.toPacketItemStack() = SpigotConversionUtil.fromBukkitItemStack(this)
fun com.github.retrooper.packetevents.protocol.item.ItemStack.toBukkitItemStack() =
    SpigotConversionUtil.toBukkitItemStack(this)
package net.craftoriya.packetuxui.bukkit.extensions

import net.craftoriya.packetuxui.user.User
import net.craftoriya.packetuxui.user.UserManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.toUser() = UserManager[this.uniqueId]
fun User.toPlayer() = Bukkit.getPlayer(this.uuid)
fun User.toOfflinePlayer() = Bukkit.getOfflinePlayer(this.uuid)
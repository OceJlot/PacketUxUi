package net.craftoriya.packetuxui.bukkit.extensions

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.service.MenuService
import org.bukkit.entity.Player

fun MenuService.getMenu(player: Player) = getMenu(player.toUser())

fun MenuService.updateItem(player: Player, item: ItemStack, slot: Int) =
    updateItem(player.toUser(), item, slot)

fun MenuService.openMenu(player: Player, menu: Menu) =
    openMenu(player.toUser(), menu)
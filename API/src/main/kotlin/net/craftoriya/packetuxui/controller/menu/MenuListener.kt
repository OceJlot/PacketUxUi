package net.craftoriya.packetuxui.controller.menu

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetuxui.service.MenuService
import net.craftoriya.packetuxui.types.ClickType
import org.bukkit.entity.Player

class MenuListener(
    private val menuService: MenuService
) {
    fun onClickWindow(event: PacketReceiveEvent) {
        val packetType = event.packetType
        val player = event.getPlayer<Player>()

        if (packetType == PacketType.Play.Client.CLOSE_WINDOW) {
            menuService.onCloseMenu(player)
            menuService.clearAccumulatedDrag(player)
        }
        if (packetType != PacketType.Play.Client.CLICK_WINDOW) return
        val packet = WrapperPlayClientClickWindow(event)
        if (menuService.shouldIgnore(packet.windowId, player)) return
        event.isCancelled = true

        val clickData = menuService.getClickType(packet)
        if (clickData.clickType == ClickType.DRAG_START || clickData.clickType == ClickType.DRAG_ADD) {
            menuService.accumulateDrag(player, packet, clickData.clickType)
            return
        }

        val menuClickData = menuService.isMenuClick(packet, clickData.clickType, player)
        if (menuClickData) {
            menuService.handleClickMenu(player, clickData, packet.slot)
            player.updateInventory()


        } else { // isInventoryClick


            menuService.handleClickInventory(player, packet)

        }
    }
}
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
        if (clickData.second == ClickType.DRAG_START || clickData.second == ClickType.DRAG_ADD) {
            menuService.accumulateDrag(player, packet, clickData.second)
            return
        }

        val menuClickData = menuService.isMenuClick(packet, clickData, player)
        if (menuClickData) {
            menuService.handleClickMenu(player, clickData, packet.slot)
            player.updateInventory()

        } else { // isInventoryClick

//            println("""
//
//                WindowID ${packet.windowId}
//                Button ${packet.button}
//                Slot ${packet.slot}
//                WindowClickType ${packet.windowClickType}
//                StateID ${packet.stateId}
//                ActionNumber ${packet.actionNumber}
//            """.trimIndent())
            menuService.handleClickInventory(player, packet)

        }
    }
}
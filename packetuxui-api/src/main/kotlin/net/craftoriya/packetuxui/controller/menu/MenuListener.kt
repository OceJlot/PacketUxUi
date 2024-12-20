package net.craftoriya.packetuxui.controller.menu

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetuxui.service.menuService
import net.craftoriya.packetuxui.types.ClickType
import net.craftoriya.packetuxui.user.UserManager

object MenuListener {

    fun onClickWindow(event: PacketReceiveEvent) {
        val packetType = event.packetType
        val packetUser = event.user
        val packetUserUuid = packetUser.uuid ?: return

        val user = UserManager[packetUserUuid]

        if (packetType == PacketType.Play.Client.CLOSE_WINDOW) {
            menuService.onCloseMenu(user)
            menuService.clearAccumulatedDrag(user)
        }
        if (packetType != PacketType.Play.Client.CLICK_WINDOW) return
        val packet = WrapperPlayClientClickWindow(event)
        if (menuService.shouldIgnore(packet.windowId, user)) return
        event.isCancelled = true

        val clickData = menuService.getClickType(packet)
        if (clickData.clickType == ClickType.DRAG_START || clickData.clickType == ClickType.DRAG_ADD) {
            menuService.accumulateDrag(user, packet, clickData.clickType)
            return
        }

        val menuClickData = menuService.isMenuClick(packet, clickData.clickType, user)
        if (menuClickData) {
            menuService.handleClickMenu(user, clickData, packet.slot)
            user.updateInventory()
        } else { // isInventoryClick
            menuService.handleClickInventory(user, packet)
        }
    }
}
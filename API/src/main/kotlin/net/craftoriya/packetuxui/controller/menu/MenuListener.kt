package net.craftoriya.packetuxui.controller.menu

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.common.PacketUtils.Companion.receivePacket
import net.craftoriya.packetuxui.dto.MenuClickData
import net.craftoriya.packetuxui.dto.WindowClick
import net.craftoriya.packetuxui.service.MenuService
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class MenuListener(
    private val menuService: MenuService
) {
    fun onClickWindow(event: PacketReceiveEvent) {
        val packetType = event.packetType
        val user = event.user
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
        if (clickData.clickType == net.craftoriya.packetuxui.types.ClickType.DRAG_START || clickData.clickType == net.craftoriya.packetuxui.types.ClickType.DRAG_ADD) {
            menuService.accumulateDrag(player, packet, clickData)
            return
        }

        val menuClickData = menuService.isMenuClick(MenuClickData(packet, clickData, player))
        if (menuClickData) {
            val response = menuService.onClickMenu(WindowClick(player, clickData, packet.slot))
            Bukkit.getScheduler().run { player.updateInventory() }
            if (response.menuContentPacket != null) {
                user.sendPacket(response.menuContentPacket)
                response.execute?.let { it(ExecuteComponent(player, clickData.buttonType, packet.slot, menuService.getCarriedItem(player))) }
            }
        } else { // isInventoryClick
            val response = menuService.onClickInventory(
                net.craftoriya.packetuxui.dto.InventoryClick(
                    player,
                    packet,
                    clickData
                )
            )
            player.receivePacket(response)
        }
    }
}
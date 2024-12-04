package net.craftoriya.packetmenuapi.controller.menu

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetmenuapi.types.ClickType
import net.craftoriya.packetmenuapi.types.ExecuteComponent
import net.craftoriya.packetmenuapi.common.PacketUtils.Companion.player
import net.craftoriya.packetmenuapi.common.PacketUtils.Companion.receivePacket
import net.craftoriya.packetmenuapi.dto.InventoryClickDTO
import net.craftoriya.packetmenuapi.dto.IsMenuClickDTO
import net.craftoriya.packetmenuapi.dto.WindowClickDTO
import net.craftoriya.packetmenuapi.service.MenuService
import org.bukkit.Bukkit

class MenuListener(
    private val menuService: MenuService
) {
    fun onClickWindow(event: PacketReceiveEvent) {
        println("Api working now")
        val type = event.packetType
        val user = event.user
        val player = event.user.player ?: return
        if(type == PacketType.Play.Client.CLOSE_WINDOW) {
            menuService.onCloseMenu(player)
            menuService.clearAccumulatedDrag(player)
        }
        if (type != PacketType.Play.Client.CLICK_WINDOW) return
        val packet = WrapperPlayClientClickWindow(event)

            if(menuService.shouldIgnore(packet.windowId, player)) { return }
            event.isCancelled = true
            val clickData = menuService.getClickType(packet)

            if (clickData.clickType == ClickType.DRAG_START || clickData.clickType == ClickType.DRAG_ADD) {
                menuService.accumulateDrag(player, packet, clickData)
                return
            }
            val isMenuClick = menuService.isMenuClick(IsMenuClickDTO(packet, clickData, player))

            if (isMenuClick) {
                val response = menuService.onClickMenu(WindowClickDTO(player, clickData, packet.slot))
                Bukkit.getScheduler().run {player.updateInventory()}
                if(response.menuContentPacket != null) {
                    user.sendPacket(response.menuContentPacket)
                    response.execute?.let { it(ExecuteComponent(player, clickData.buttonType, packet.slot, menuService.getCarriedItem(player))) }
                }

            }else{ /*isInventoryClick*/
                val response = menuService.onClickInventory(InventoryClickDTO(player, packet, clickData))
                player.receivePacket(response)
            }
    }
}
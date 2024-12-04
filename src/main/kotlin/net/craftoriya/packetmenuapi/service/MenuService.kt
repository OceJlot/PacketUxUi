package net.craftoriya.packetmenuapi.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetmenuapi.common.PacketUtils.Companion.receivePacket
import net.craftoriya.packetmenuapi.common.PacketUtils.Companion.sendPacket
import net.craftoriya.packetmenuapi.dto.*
import net.craftoriya.packetmenuapi.types.ButtonType
import net.craftoriya.packetmenuapi.types.ClickData
import net.craftoriya.packetmenuapi.types.ClickType
import org.bukkit.entity.Player
import java.util.*

class MenuService {
    private val viewers = WeakHashMap<Player, Menu>()
    private val carriedItem = WeakHashMap<Player, ItemStack>()
    private val accumulatedDrag = WeakHashMap<Player, MutableList<AccumulatedDrag>>()


    fun openMenu(player: Player, menu: Menu) {
        synchronized(viewers) {
            viewers[player] = menu.copy()
        }
        player.sendPacket(menu.menuPacket)
        player.sendPacket(menu.contentPacket)
    }

    fun onCloseMenu(player: Player) {
        synchronized(viewers) {
            viewers.remove(player)
        }
        synchronized(carriedItem) {
            carriedItem.remove(player)
        }
        clearAccumulatedDrag(player)
    }

    fun onClickInventory(click: InventoryClickDTO): WrapperPlayClientClickWindow {
        val menu = viewers[click.player] ?: error("Menu under player key not found.")
        val clickData = getClickType(click.packet)

        updateCarriedItem(click.player, click.packet.carriedItemStack, clickData.clickType)

        if (clickData.clickType == ClickType.DRAG_END) {
            handleDragEnd(click.player, menu)
        }

        return createAdjustedClickPacket(click, menu)
    }

    fun onClickMenu(click: WindowClickDTO): WindowClickResponseDTO {

        if (click.clickData.clickType == ClickType.DRAG_END) {
            clearAccumulatedDrag(click.player)
        }
        val carriedItem = carriedItem[click.player] ?: ItemStack.EMPTY
        val menu = viewers[click.player] ?: error("Menu under player key not found.")
        val windowClickResponseDTO = WindowClickResponseDTO(
                WrapperPlayServerWindowItems(126, 0, menu.contentPacket.items, carriedItem),
                null
        )
        val button = menu.buttons[click.slot] ?: return windowClickResponseDTO

        val now = System.currentTimeMillis()
        val cooldown = button.cooldown.combine(menu.cooldown)
        val execute = if(!cooldown.isFreezeExpired(now)) {
            null
        } else if (!cooldown.isTimeExpired(now)) {
            button.cooldown.resetFreeze()
            button.cooldown.execute
        }else {
            button.cooldown.resetTime()
            button.execute
        }
        return windowClickResponseDTO.copy(execute = execute)
    }

    fun updateItem(player: Player, item: ItemStack, slot: Int) {
        val menu = getMenu(player) ?: return
        if (slot > menu.type.lastIndex) throw IllegalArgumentException("Slot out of range.")

        synchronized(menu) {
            val items = menu.contentPacket.items.toMutableList()
            items[slot] = item
            menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)
        }
        player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, item))
    }

    fun updateItems(player: Player, newItems: Map<Int, ItemStack>) {
        val menu = getMenu(player) ?: return
        if (newItems.keys.any { it > menu.type.lastIndex }) throw IllegalArgumentException("Slot out of range.")

        synchronized(menu) {
            val items = menu.contentPacket.items.toMutableList()
            newItems.forEach { (slot, item) ->
                items[slot] = item
            }
            menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)
        }
        for((slot, item) in newItems) {
            player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, item))
        }
    }

    fun updateButton(player: Player, newButton: Button, slot: Int) {
        val menu = getMenu(player) ?: return
        if (slot > menu.type.lastIndex) throw IllegalArgumentException("Slot out of range.")

        synchronized(menu) {
            menu.buttons[slot] = newButton
            val items = menu.contentPacket.items.toMutableList()
            items[slot] = newButton.item
            menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)
        }
        player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, newButton.item))
    }

    fun updateButtons(player: Player, newButtons: Map<Int, Button>) {
        val menu = getMenu(player) ?: return
        if (newButtons.any { (slot, _) -> slot > menu.type.lastIndex }) {
            throw IllegalArgumentException("Slot out of range.")
        }

        synchronized(menu) {
            menu.buttons.clear()
            menu.buttons.putAll(newButtons)
            val items = MutableList(menu.type.lastIndex) { index ->
                newButtons[index]?.item ?: ItemStack.EMPTY
            }
            menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)
        }
        for((slot, button) in newButtons) {
            player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, button.item))
        }
    }

    fun getMenu(player: Player): Menu? = viewers[player]

    fun getCarriedItem(player: Player): ItemStack? = carriedItem[player]

    fun shouldIgnore(id: Int, player: Player): Boolean =  id != 126 || !viewers.containsKey(player)

    fun isMenuClick(click: IsMenuClickDTO): Boolean {
        val menu = viewers[click.player] ?: error("Menu under player key not found.")
        return when (click.clickType.clickType) {
            ClickType.SHIFT_CLICK -> true
            in listOf(ClickType.PICKUP, ClickType.PLACE) -> click.wrapper.slot in 0..menu.type.lastIndex
            ClickType.DRAG_END -> click.wrapper.slots.orElse(emptyMap()).keys.any { it in 0..menu.type.lastIndex }
            ClickType.PICKUP_ALL -> click.wrapper.slots.orElse(emptyMap()).keys.any { it in 0..menu.type.lastIndex }
            else -> false
        }
    }

    fun getClickType(packet: WrapperPlayClientClickWindow): ClickData {
        when (packet.windowClickType){
            WindowClickType.PICKUP ->{
                return if (packet.carriedItemStack != ItemStack.EMPTY) {
                    if (packet.button == 0) ClickData(ButtonType.LEFT, ClickType.PICKUP)
                    else ClickData(ButtonType.RIGHT, ClickType.PICKUP)
                } else {
                    ClickData(ButtonType.RIGHT, ClickType.PLACE)
                }
            }
            WindowClickType.QUICK_MOVE -> {
                return if(packet.button == 0) {
                    ClickData(ButtonType.SHIFT_LEFT, ClickType.SHIFT_CLICK)
                }else{
                    ClickData(ButtonType.SHIFT_RIGHT, ClickType.SHIFT_CLICK)
                }
            }
            WindowClickType.SWAP -> {
                return if(packet.button == 40) {
                    ClickData(ButtonType.F, ClickType.PICKUP)
                }else {
                    return ClickData(ButtonType.LEFT, ClickType.PLACE)
                }
            }
            WindowClickType.CLONE -> {
                return ClickData(ButtonType.MIDDLE, ClickType.PICKUP)
            }
            WindowClickType.THROW -> {
                return if(packet.button == 0) {
                    ClickData(ButtonType.DROP, ClickType.PICKUP)
                }else{
                    ClickData(ButtonType.CTRL_DROP, ClickType.PICKUP)
                }
            }
            WindowClickType.QUICK_CRAFT -> {
                return when (packet.button) {
                    0 -> ClickData(ButtonType.LEFT, ClickType.DRAG_START)
                    4 -> ClickData(ButtonType.RIGHT, ClickType.DRAG_START)
                    8 -> ClickData(ButtonType.MIDDLE, ClickType.DRAG_START)

                    1 -> ClickData(ButtonType.LEFT, ClickType.DRAG_ADD)
                    5 -> ClickData(ButtonType.RIGHT, ClickType.DRAG_ADD)
                    9 -> ClickData(ButtonType.MIDDLE, ClickType.DRAG_ADD)

                    2 -> ClickData(ButtonType.LEFT, ClickType.DRAG_END)
                    6 -> ClickData(ButtonType.RIGHT, ClickType.DRAG_END)
                    10 -> ClickData(ButtonType.MIDDLE, ClickType.DRAG_END)

                    else -> ClickData(ButtonType.LEFT, ClickType.UNDEFINED)
                }
            }
            WindowClickType.PICKUP_ALL -> {
                return ClickData(ButtonType.DOUBLE_CLICK, ClickType.PICKUP_ALL)
            }
            else -> { return ClickData(ButtonType.LEFT, ClickType.UNDEFINED) }
        }
    }

    fun accumulateDrag(player: Player, packet: WrapperPlayClientClickWindow, type: ClickData) {
        accumulatedDrag.getOrPut(player) { mutableListOf() }.add(AccumulatedDrag(packet, type.clickType))
    }

    private fun handleDragEnd(player: Player, menu: Menu) {
        accumulatedDrag[player]?.forEach { drag ->
            val packet = if (drag.type == ClickType.DRAG_START) {
                createDragPacket(drag.packet, 0)
            } else {
                createDragPacket(drag.packet, -menu.type.size + 9)
            }
            player.receivePacket(packet)
        }
        clearAccumulatedDrag(player)
    }

    private fun createDragPacket(originalPacket: WrapperPlayClientClickWindow, slotOffset: Int): WrapperPlayClientClickWindow {
        return WrapperPlayClientClickWindow(
            0, originalPacket.stateId, originalPacket.slot + slotOffset, originalPacket.button,
            originalPacket.actionNumber, originalPacket.windowClickType,
            Optional.of(mutableMapOf()), originalPacket.carriedItemStack
        )
    }

    fun clearAccumulatedDrag(player: Player) {
        synchronized(accumulatedDrag){
            accumulatedDrag[player]?.clear()
        }
    }

    private fun createAdjustedClickPacket(click: InventoryClickDTO, menu: Menu): WrapperPlayClientClickWindow {
        val slotOffset = if (click.packet.slot != -999) click.packet.slot - menu.type.size + 9 else -999
        val adjustedSlots = click.packet.slots.orElse(emptyMap()).mapKeys { (slot, _) ->
            slot - menu.type.size + 9
        }

        return WrapperPlayClientClickWindow(
            0, click.packet.stateId, slotOffset, click.packet.button,
            click.packet.actionNumber, click.packet.windowClickType,
            Optional.of(adjustedSlots), click.packet.carriedItemStack
        )
    }

    private fun updateCarriedItem(player: Player, carriedItemStack: ItemStack, clickType: ClickType) {
        synchronized(carriedItem){
            carriedItem[player] = when (clickType) {
                ClickType.PICKUP, ClickType.PICKUP_ALL, ClickType.DRAG_START, ClickType.DRAG_END -> {
                    carriedItemStack
                }
                else -> ItemStack.EMPTY
            }
        }
    }
}


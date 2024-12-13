package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetuxui.common.PacketUtils.Companion.receivePacket
import net.craftoriya.packetuxui.common.PacketUtils.Companion.sendPacket
import net.craftoriya.packetuxui.dto.AccumulatedDrag
import net.craftoriya.packetuxui.types.ButtonType
import net.craftoriya.packetuxui.types.ClickData
import net.craftoriya.packetuxui.types.ClickType
import net.craftoriya.packetuxui.types.ExecuteComponent
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MenuService {
    private val viewers = ConcurrentHashMap<Player, Menu>()
    private val carriedItem = ConcurrentHashMap<Player, ItemStack>()
    private val accumulatedDrag = ConcurrentHashMap<Player, MutableList<AccumulatedDrag>>()


    fun openMenu(player: Player, menu: Menu) {
        viewers[player] = menu.copy()
        player.sendPacket(menu.menuPacket)
        player.sendPacket(menu.contentPacket)
    }

    fun onCloseMenu(player: Player) {
        viewers.remove(player)
        carriedItem.remove(player)
        clearAccumulatedDrag(player)
    }

    fun handleClickInventory(player: Player, packet: WrapperPlayClientClickWindow) {
        val menu = viewers[player] ?: error("Menu under player key not found.")
        val clickData = getClickType(packet)

        updateCarriedItem(player, packet.carriedItemStack, clickData.clickType)

        if (clickData.clickType == ClickType.DRAG_END) {
            handleDragEnd(player, menu)
        }
        player.receivePacket(createAdjustedClickPacket(packet, menu))
    }

    fun handleClickMenu(player: Player, clickData: ClickData, slot: Int) {

        if (clickData.clickType == ClickType.DRAG_END) {
            clearAccumulatedDrag(player)
        }
        val carriedItem = carriedItem[player]
        val menu = viewers[player] ?: error("Menu under player key not found.")

        val menuContentPacket = WrapperPlayServerWindowItems(126, 0, menu.contentPacket.items, carriedItem)

        val button = menu.buttons[slot]
        if (button == null) {
            player.sendPacket(menuContentPacket)
            return
        }

        val now = System.currentTimeMillis()
        val cooldown = button.cooldown.combine(menu.cooldown)
        val execute = if (!cooldown.isFreezeExpired(now)) {
            null
        } else if (!cooldown.isTimeExpired(now)) {
            button.cooldown.resetFreeze()
            button.cooldown.execute
        } else {
            button.cooldown.resetTime()
            button.execute
        }
        player.sendPacket(menuContentPacket)
        execute?.let {
            it(
                ExecuteComponent(
                    player,
                    clickData.buttonType,
                    slot,
                    carriedItem
                )
            )
        }
    }

    fun updateItem(player: Player, item: ItemStack, slot: Int) {
        val menu = getMenu(player) ?: return
        if (slot > menu.type.lastIndex) throw IllegalArgumentException("Slot out of range.")

        val items = menu.contentPacket.items.toMutableList()
        items[slot] = item
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, item))
    }

    fun updateItems(player: Player, newItems: Map<Int, ItemStack>) {
        val menu = getMenu(player) ?: return
        if (newItems.keys.any { it > menu.type.lastIndex }) throw IllegalArgumentException("Slot out of range.")

        val items = menu.contentPacket.items.toMutableList()
        newItems.forEach { (slot, item) ->
            items[slot] = item
        }
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        for ((slot, item) in newItems) {
            player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, item))
        }
    }

    fun updateButton(player: Player, newButton: Button, slot: Int) {
        val menu = getMenu(player) ?: return
        if (slot > menu.type.lastIndex) throw IllegalArgumentException("Slot out of range.")

        menu.buttons[slot] = newButton
        val items = menu.contentPacket.items.toMutableList()
        items[slot] = newButton.item
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, newButton.item))
    }

    fun updateButtons(player: Player, newButtons: Map<Int, Button>) {
        val menu = getMenu(player) ?: return
        if (newButtons.any { (slot, _) -> slot > menu.type.lastIndex }) {
            throw IllegalArgumentException("Slot out of range.")
        }

        menu.buttons.clear()
        menu.buttons.putAll(newButtons)
        val items = MutableList(menu.type.lastIndex) { index ->
            newButtons[index]?.item ?: ItemStack.EMPTY
        }
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        for ((slot, button) in newButtons) {
            player.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, button.item))
        }
    }

    fun getMenu(player: Player): Menu? = viewers[player]


    fun shouldIgnore(id: Int, player: Player): Boolean = id != 126 || !viewers.containsKey(player)

    fun isMenuClick(
        wrapper: WrapperPlayClientClickWindow,
        clickType: ClickType,
        player: Player
    ): Boolean {
        val menu = viewers[player] ?: error("Menu under player key not found.")
        val slotRange = 0..menu.type.lastIndex

        return when (clickType) {
            ClickType.SHIFT_CLICK -> true
            in listOf(ClickType.PICKUP, ClickType.PLACE) -> wrapper.slot in slotRange
            ClickType.DRAG_END, ClickType.PICKUP_ALL ->
                wrapper.slot in slotRange || wrapper.slots.orElse(emptyMap()).keys.any { it in slotRange }

            else -> false
        }
    }

    fun getClickType(packet: WrapperPlayClientClickWindow): ClickData {
        return when (packet.windowClickType) {
            WindowClickType.PICKUP -> {
                val carriedItem = packet.carriedItemStack
                val isCarriedItemExist =
                    carriedItem != null && carriedItem != ItemStack.EMPTY && carriedItem.type != ItemTypes.AIR
                when (packet.button) {
                    0 -> ClickData(ButtonType.LEFT, if (isCarriedItemExist) ClickType.PICKUP else ClickType.PLACE)
                    else -> ClickData(
                        ButtonType.RIGHT,
                        if (isCarriedItemExist) ClickType.PLACE else ClickType.PICKUP
                    )
                }
            }

            WindowClickType.QUICK_MOVE -> {
                if (packet.button == 0) {
                    ClickData(ButtonType.SHIFT_LEFT, ClickType.SHIFT_CLICK)
                } else {
                    ClickData(ButtonType.SHIFT_RIGHT, ClickType.SHIFT_CLICK)
                }
            }

            WindowClickType.SWAP -> {
                when (packet.button) {
                    in 0..8 -> ClickData(ButtonType.entries[9 + packet.button], ClickType.PICKUP)
                    40 -> ClickData(ButtonType.F, ClickType.PICKUP)
                    else -> ClickData(ButtonType.LEFT, ClickType.PLACE)
                }
            }

            WindowClickType.CLONE -> {
                ClickData(ButtonType.MIDDLE, ClickType.PICKUP)
            }

            WindowClickType.THROW -> {
                if (packet.button == 0) {
                    ClickData(ButtonType.DROP, ClickType.PICKUP)
                } else {
                    ClickData(ButtonType.CTRL_DROP, ClickType.PICKUP)
                }
            }

            WindowClickType.QUICK_CRAFT -> {
                when (packet.button) {
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
                ClickData(ButtonType.DOUBLE_CLICK, ClickType.PICKUP_ALL)
            }

            else -> {
                ClickData(ButtonType.LEFT, ClickType.UNDEFINED)
            }
        }
    }


    fun accumulateDrag(player: Player, packet: WrapperPlayClientClickWindow, type: ClickType) {
        accumulatedDrag.getOrPut(player) { mutableListOf() }.add(AccumulatedDrag(packet, type))
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

    private fun createDragPacket(
        originalPacket: WrapperPlayClientClickWindow,
        slotOffset: Int
    ): WrapperPlayClientClickWindow {
        return WrapperPlayClientClickWindow(
            0, originalPacket.stateId, originalPacket.slot + slotOffset, originalPacket.button,
            originalPacket.actionNumber, originalPacket.windowClickType,
            Optional.of(mutableMapOf()), originalPacket.carriedItemStack
        )
    }

    fun clearAccumulatedDrag(player: Player) {
        accumulatedDrag[player]?.clear()
    }

    private fun createAdjustedClickPacket(
        packet: WrapperPlayClientClickWindow,
        menu: Menu
    ): WrapperPlayClientClickWindow {
        val slotOffset = if (packet.slot != -999) packet.slot - menu.type.size + 9 else -999
        val adjustedSlots = packet.slots.orElse(emptyMap()).mapKeys { (slot, _) ->
            slot - menu.type.size + 9
        }

        return WrapperPlayClientClickWindow(
            0, packet.stateId, slotOffset, packet.button,
            packet.actionNumber, packet.windowClickType,
            Optional.of(adjustedSlots), packet.carriedItemStack
        )
    }

    private fun updateCarriedItem(player: Player, carriedItemStack: ItemStack?, clickType: ClickType) {
        if (carriedItemStack == null || carriedItemStack.type == ItemTypes.AIR) {
            carriedItem.remove(player)
            return
        }
        when (clickType) {
            ClickType.PICKUP, ClickType.PICKUP_ALL, ClickType.DRAG_START, ClickType.DRAG_END -> {
                carriedItem[player] = carriedItemStack
            }

            else -> carriedItem.remove(player)
        }
    }
}


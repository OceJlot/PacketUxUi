package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectList
import net.craftoriya.packetuxui.common.mutableObject2ObjectMapOf
import net.craftoriya.packetuxui.common.mutableObjectListOf
import net.craftoriya.packetuxui.common.synchronize
import net.craftoriya.packetuxui.dto.AccumulatedDrag
import net.craftoriya.packetuxui.types.ButtonType
import net.craftoriya.packetuxui.types.ClickData
import net.craftoriya.packetuxui.types.ClickType
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.user.User
import java.util.*

val menuService = MenuService

object MenuService {

    private val viewers = mutableObject2ObjectMapOf<User, Menu>().synchronize()
    private val carriedItem = mutableObject2ObjectMapOf<User, ItemStack>().synchronize()
    private val accumulatedDrag =
        mutableObject2ObjectMapOf<User, ObjectList<AccumulatedDrag>>().synchronize()

    fun openMenu(user: User, menu: Menu) {
        viewers[user] = menu.copy()
        user.sendPacket(menu.menuPacket)
        user.sendPacket(menu.contentPacket)
    }

    fun onCloseMenu(user: User) {
        viewers.remove(user)
        carriedItem.remove(user)
        clearAccumulatedDrag(user)
    }

    fun handleClickInventory(user: User, packet: WrapperPlayClientClickWindow) {
        val menu = viewers[user] ?: error("Menu under user key not found.")
        val clickData = getClickType(packet)

        updateCarriedItem(user, packet.carriedItemStack, clickData.clickType)

        if (clickData.clickType == ClickType.DRAG_END) {
            handleDragEnd(user, menu)
        }

        user.receivePacket(createAdjustedClickPacket(packet, menu))
    }

    fun handleClickMenu(user: User, clickData: ClickData, slot: Int) {
        if (clickData.clickType == ClickType.DRAG_END) {
            clearAccumulatedDrag(user)
        }

        val carriedItem = carriedItem[user]
        val menu = viewers[user] ?: error("Menu under user key not found.")

        val menuContentPacket =
            WrapperPlayServerWindowItems(126, 0, menu.contentPacket.items, carriedItem)

        val button = menu.buttons[slot]
        if (button == null) {
            user.sendPacket(menuContentPacket)
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

        user.sendPacket(menuContentPacket)
        execute?.let {
            it(
                ExecuteComponent(
                    user,
                    clickData.buttonType,
                    slot,
                    carriedItem
                )
            )
        }
    }

    fun updateItem(user: User, item: ItemStack, slot: Int) {
        val menu = getMenu(user) ?: return
        require(slot in 0..menu.type.lastIndex) { "Slot out of range." }

        val items = menu.contentPacket.items.toMutableList()
        items[slot] = item
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        user.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, item))
    }

    fun updateItems(user: User, newItems: Int2ObjectMap<ItemStack>) {
        val menu = getMenu(user) ?: return
        require(newItems.keys.any { it in 0..menu.type.lastIndex }) { "Slot out of range." }

        val items = menu.contentPacket.items.toMutableList()
        newItems.forEach { (slot, item) ->
            items[slot] = item
        }
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        for ((slot, item) in newItems) {
            user.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, item))
        }
    }

    fun updateButton(user: User, newButton: Button, slot: Int) {
        val menu = getMenu(user) ?: return
        require(slot in 0..menu.type.lastIndex) { "Slot out of range." }

        menu.buttons[slot] = newButton
        val items = menu.contentPacket.items.toMutableList()
        items[slot] = newButton.item
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        user.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, newButton.item))
    }

    fun updateButtons(user: User, newButtons: Int2ObjectMap<Button>) {
        val menu = getMenu(user) ?: return
        require(newButtons.keys.any { it in 0..menu.type.lastIndex }) { "Slot out of range." }

        menu.buttons.clear()
        menu.buttons.putAll(newButtons)
        val items = MutableList(menu.type.lastIndex) { index ->
            newButtons[index]?.item ?: ItemStack.EMPTY
        }
        menu.contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)

        for ((slot, button) in newButtons) {
            user.sendPacket(WrapperPlayServerSetSlot(126, 0, slot, button.item))
        }
    }

    fun getMenu(user: User): Menu? = viewers[user]


    fun shouldIgnore(id: Int, user: User): Boolean = id != 126 || !viewers.containsKey(user)

    fun isMenuClick(
        wrapper: WrapperPlayClientClickWindow,
        clickType: ClickType,
        user: User
    ): Boolean {
        val menu = viewers[user] ?: error("Menu under user key not found.")
        val slotRange = 0..menu.type.lastIndex

        return when (clickType) {
            ClickType.SHIFT_CLICK -> true
            ClickType.PICKUP, ClickType.PLACE -> wrapper.slot in slotRange
            ClickType.DRAG_END, ClickType.PICKUP_ALL ->
                wrapper.slot in slotRange || wrapper.slots.map { it.keys.any { it in slotRange } }
                    .orElse(false)

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
                    0 -> ClickData(
                        ButtonType.LEFT,
                        if (isCarriedItemExist) ClickType.PICKUP else ClickType.PLACE
                    )

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


    fun accumulateDrag(user: User, packet: WrapperPlayClientClickWindow, type: ClickType) {
        accumulatedDrag.computeIfAbsent(user) { mutableObjectListOf() }
            .add(AccumulatedDrag(packet, type))
    }

    private fun handleDragEnd(user: User, menu: Menu) {
        accumulatedDrag[user]?.forEach { drag ->
            val packet = if (drag.type == ClickType.DRAG_START) {
                createDragPacket(drag.packet, 0)
            } else {
                createDragPacket(drag.packet, -menu.type.size + 9)
            }
            user.receivePacket(packet)
        }
        clearAccumulatedDrag(user)
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

    fun clearAccumulatedDrag(user: User) {
        accumulatedDrag[user]?.clear()
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

    private fun updateCarriedItem(
        user: User,
        carriedItemStack: ItemStack?,
        clickType: ClickType
    ) {
        if (carriedItemStack == null || carriedItemStack.type == ItemTypes.AIR) {
            carriedItem.remove(user)
            return
        }
        when (clickType) {
            ClickType.PICKUP, ClickType.PICKUP_ALL, ClickType.DRAG_START, ClickType.DRAG_END -> {
                carriedItem[user] = carriedItemStack
            }

            else -> carriedItem.remove(user)
        }
    }
}


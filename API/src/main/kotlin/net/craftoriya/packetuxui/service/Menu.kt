package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.kyori.adventure.text.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class Menu(
    val name: Component,
    val type: net.craftoriya.packetuxui.types.InventoryType,
    buttons: Map<Int, Button>,
    val cooldown: CooldownComponent = CooldownComponent()
) {
    val buttons: ConcurrentMap<Int, Button> = ConcurrentHashMap(buttons)

    @Volatile var contentPacket: WrapperPlayServerWindowItems
    @Volatile var menuPacket: WrapperPlayServerOpenWindow

    fun copy(): Menu {
        return Menu(name, type, buttons, cooldown)
    }

    init {
        val items = MutableList(type.size) { index ->
            this.buttons[index]?.item ?: ItemStack.EMPTY
        }

        if (buttons.size > type.size) {
            throw IllegalArgumentException("Too many items in menu")
        }
        menuPacket = WrapperPlayServerOpenWindow(126, type.id(), name)
        contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)
    }
}


package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.types.InventoryType

class Static3x9 {

    private val item1 = ItemBuilder()
        .itemType(ItemTypes.RED_WOOL)
        .name("<red>Red Wool".toComponent())
        .lore(
            "<#f7983e>Red Wool".toComponent(),
            "<#f7b33e>Red Wool".toComponent(),
            "<#f7c93e>Red Wool".toComponent(),
        )
        .amount(4)
        .enchantment(EnchantmentTypes.FIRE_ASPECT, 2, visible = true)
        .build()

    private val item2 = ItemBuilder()
        .itemType(ItemTypes.ACACIA_SIGN)
        .name("<rainbow>Cool sign".toComponent())
        .lore(
            "<#f7983e>smthn".toComponent(),
            "<#f7b33e>invisible enchantment".toComponent(),
        )
        .amount(64)
        .enchantment(EnchantmentTypes.UNBREAKING, 2, visible = false)
        .build()

    private val button1 = ButtonBuilder()
        .item(item1)
        .click {
            it.user.sendMessage("You clicked with item: ${it.itemStack?.type?.name}".toComponent())
        }
        .build()

    private val button2 = ButtonBuilder()
        .item(item2)
        .build()


    val menu = Menu(
        name = "<gradient:#ff6d2e:#ff2e62><bold>First packet menu".toComponent(),
        type = InventoryType.GENERIC9X3,
        buttons = mapOf(
            0 to button1,
            4 to button1,
            8 to button2,
            12 to button2,
            16 to button1,
            20 to button1,
            24 to button2,
        ),
        cooldown = CooldownComponent(
            delay = 5000,
            execute = { it.user.sendMessage("Menu cooldown bigger so it overrides item's one".toComponent()) },
            freeze = 1000
        )
    )
}
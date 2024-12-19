package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.craftoriya.packetuxui.common.asComponent
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.types.InventoryType

class Static3x9 {

    private val item1 = ItemBuilder().apply {
        itemType(ItemTypes.RED_WOOL)
        name("<red>Red Wool".asComponent)
        lore(
            "<#f7983e>Red Wool".asComponent,
            "<#f7b33e>Red Wool".asComponent,
            "<#f7c93e>Red Wool".asComponent,
        )
        amount(4)
        enchantment(EnchantmentTypes.FIRE_ASPECT, 2, visible = true)

    }.build() //example for dsl like syntax for ItemBuilder
        
        

    private val item2 = ItemBuilder()
        .itemType(ItemTypes.ACACIA_SIGN)
        .name("<rainbow>Cool sign".asComponent)
        .lore(
            "<#f7983e>smthn".asComponent,
            "<#f7b33e>invisible enchantment".asComponent,
        )
        .amount(64)
        .enchantment(EnchantmentTypes.UNBREAKING, 2, visible = false)
        .build()

    private val button1 = ButtonBuilder()
        .item(item1)
        .click {
            it.buttonType
            it.player.sendMessage("You clicked with item: ${it.itemStack?.type?.name}".asComponent)
        }
        .build()

    private val button2 = ButtonBuilder()
        .item(item2)
        .build()



    val menu = Menu(
        name = "<gradient:#ff6d2e:#ff2e62><bold>First packet menu".asComponent,
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
            execute = {it.player.sendMessage("Menu cooldown bigger so it overrides item's one".asComponent)},
            freeze = 1000
        )
    )
}
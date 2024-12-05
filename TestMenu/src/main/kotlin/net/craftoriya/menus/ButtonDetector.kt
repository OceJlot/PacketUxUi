package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.types.InventoryType

class ButtonDetector {
    private val glowingItem = ItemBuilder()
        .itemType(ItemTypes.GLOWSTONE)
        .name("<yellow><bold>Glowing Stone".toComponent())
        .amount(1)
        .build()

    private val heavyItem = ItemBuilder()
        .itemType(ItemTypes.ANVIL)
        .name("<dark_gray><bold>Heavy Anvil".toComponent())
        .lore(
            "<gray>This item is too heavy to carry.".toComponent(),
            "<red>Custom material property: <bold>Heavy".toComponent()
        )
        .amount(1)
        .build()

    private val hoverButton = ButtonBuilder()
        .item(glowingItem)
        .click {
            it.player.sendMessage("<green>You clicked on the glowing button!".toComponent())
            it.player.sendMessage("Ur button is ${it.buttonType}".toComponent())
        }
        .build()

    private val cooldownButton = ButtonBuilder()
        .item(heavyItem)
        .click {
            it.player.sendMessage("<gold>Clicked on Heavy Anvil!".toComponent())
            it.player.sendMessage("Ur button is ${it.buttonType}".toComponent())

        }
        .build()

    val menu = Menu(
        name = "<gradient:#ff7f50:#ff4500><bold>Main Menu".toComponent(),
        type = InventoryType.CRAFTING_TABLE,
        buttons = mapOf(
            0 to hoverButton,
            1 to cooldownButton,
            2 to hoverButton,
            3 to cooldownButton,
            4 to hoverButton,
            5 to cooldownButton,
            6 to hoverButton,
            7 to cooldownButton,
            8 to hoverButton,
            9 to cooldownButton
        ),
        cooldown = CooldownComponent(freeze = 300)

    )
}
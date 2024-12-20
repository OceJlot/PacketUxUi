package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.types.InventoryType

class CooldownTest {

    val item = ItemBuilder()
        .itemType(ItemTypes.DIAMOND_SWORD)
        .lore("<gray>cooldown 10s".toComponent(), "<gray>no freeze".toComponent())
        .amount(1)
        .build()

    val item1 = ItemBuilder()
        .itemType(ItemTypes.BOW)
        .name("<gradient:#96d9be:#00b4d8><bold>Test".toComponent())
        .lore("<gray>cooldown 2s".toComponent(), "<gray>freeze 1s".toComponent())
        .amount(1)
        .build()

    val button = ButtonBuilder()
        .item(item)
        .cooldown(CooldownComponent(
            10000, {
                it.user.sendMessage(
                    """
                
                <red>You can do everything the same as in execute, 
                when the player is under cooldown
                but not when freeze time""".trimIndent().toComponent()
                )
            }
        ))
        .build()

    val button1 = ButtonBuilder()
        .item(item1)
        .cooldown(
            CooldownComponent(
                2000, {
                    it.user.sendMessage(
                        """
                
                <red>You can do everything the same as in execute, 
                when the player is under cooldown
                but not when freeze time""".trimIndent().toComponent()
                    )
                },
                1000
            )
        )
        .build()

    val menu = Menu(
        name = "<gradient:#96d9be:#00b4d8><bold>CooldownTest".toComponent(),
        type = InventoryType.ANVIL,
        buttons = mapOf(
            0 to button,
            1 to button1,
            2 to button
        )
    )
}
package net.craftoriya.menus

import net.craftoriya.packetuxui.common.asComponent
import net.craftoriya.packetuxui.dsl.createButton
import net.craftoriya.packetuxui.dsl.createMenu
import net.craftoriya.packetuxui.dsl.slot
import net.craftoriya.packetuxui.types.InventoryType
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object DslMenus {
    val menuWithoutContent = createMenu(
        InventoryType.GENERIC9X3,
        "<green><b>DSL</b> menu".asComponent
    )

    val testButton = createButton {
        item {
            name("Hi item".asComponent)
            lore(listOf(
                "<red>R",
                "<green>G",
                "<blue>B",
            ).map(String::asComponent))
        }
        cooldown(5.seconds) {
            it.player.sendMessage("Opps, cooldown!")
        }
    }

    val menuWithButtons = createMenu(
        InventoryType.GENERIC9X3,
        "<green><b>DSL</b> menu".asComponent
    ) {
        button(1.slot) {
            item {
                name("Quick item".asComponent)
            }
            cooldown(5.seconds) {
                it.player.sendMessage("Opps, cooldown!")
            }
        }

        button((0..18).slot, testButton)

        cooldown(
            delay = 10.seconds,
            freeze = 50.milliseconds,
        ) {
            it.player.sendMessage("Opps, cooldown for menu!")
        }

    }
}


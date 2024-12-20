package net.craftoriya.commands

import net.craftoriya.menus.*
import net.craftoriya.packetuxui.bukkit.extensions.openMenu
import net.craftoriya.packetuxui.service.menuService
import net.craftoriya.plugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper
import org.incendo.cloud.paper.util.sender.PlayerSource
import org.incendo.cloud.paper.util.sender.Source

object CommandListener {

    init {
        val coordinator = ExecutionCoordinator.simpleCoordinator<Source>()
        val commandManager: PaperCommandManager<Source> = PaperCommandManager
            .builder(PaperSimpleSenderMapper.simpleSenderMapper())
            .executionCoordinator(coordinator)
            .buildOnEnable(plugin)

        val openMenuBuilder = commandManager.commandBuilder("open_menu")
            .senderType(PlayerSource::class.java)

        val drawLineBuilder = commandManager.commandBuilder("draw_line")
            .senderType(PlayerSource::class.java)

        val eggLineBuilder = commandManager.commandBuilder("egg_line")
            .senderType(PlayerSource::class.java)


        val subcommands = listOf(
            "static_3x9" to Pair(
                Static3x9().menu, """
                
                Simple static menu 3x9.
                Entire menu under cooldown of 5 seconds
                And freeze time of 1 second.
                
            """.trimIndent()
            ),

            "dynamic_4x9" to Pair(
                Dynamic4x9().menu, """
                
                Dynamic menu 4x9.
                buttons become a stone after a click
                Also some items are blinking
            """.trimIndent()
            ),

            "button_detector" to Pair(
                ButtonDetector().menu, """
                
                Static crafting table menu
                This menu detects button click types.
                freeze time 300ms
                """.trimIndent()
            ),
            "cooldown_test" to Pair(
                CooldownTest().menu, """
                
                
                 Static anvil menu.
                 Demonstrates how cooldown works.
                
                """.trimIndent()
            ),
            "all_in_one" to Pair(
                AllInOne().menu, """
                
                This is a comprehensive all-in-one menu.
                
                """.trimIndent()
            )
        )

        for ((subcommand, menuData) in subcommands) {
            val (menu, description) = menuData

            commandManager.command(
                openMenuBuilder.literal(subcommand)
                    .handler { context ->
                        menuService.openMenu(context.sender().source(), menu)
                    }
            )

            commandManager.command(
                openMenuBuilder.literal(subcommand)
                    .literal("desc")
                    .handler { context ->
                        val player = context.sender().source()
                        player.sendMessage(description)
                    }
            )
        }
    }
}


package net.craftoriya.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.stringArgument
import net.craftoriya.menus.*
import net.craftoriya.packetuxui.bukkit.extensions.openMenu
import net.craftoriya.packetuxui.service.menuService

object OpenMenuCommand {

    init {
        commandAPICommand("openmenu") {
            stringArgument("menu") {
                replaceSuggestions(
                    ArgumentSuggestions.strings(
                        "static_3x9",
                        "dynamic_4x9",
                        "button_detector",
                        "cooldown_test",
                        "all_in_one"
                    )
                )
            }

            executesPlayer(PlayerCommandExecutor { player, args ->
                val menuName = args["menu"] as String
                val menuClass = when (menuName) {
                    "static_3x9" -> Static3x9()
                    "dynamic_4x9" -> Dynamic4x9()
                    "button_detector" -> ButtonDetector()
                    "cooldown_test" -> CooldownTest()
                    "all_in_one" -> AllInOne()
                    else -> null
                }

                val menu = when (menuClass) {
                    is AllInOne -> menuClass.menu
                    is ButtonDetector -> menuClass.menu
                    is CooldownTest -> menuClass.menu
                    is Dynamic4x9 -> menuClass.menu
                    is Static3x9 -> menuClass.menu
                    else -> null
                }

                if (menu == null) {
                    player.sendMessage("Menu not found")
                    return@PlayerCommandExecutor
                }

                menuService.openMenu(player, menu)

                if (menuClass is AllInOne) {
                    menuClass.startUpdate()
                }
            })
        }
    }
}
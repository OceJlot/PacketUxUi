package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.shynixn.mccoroutine.folia.launch
import kotlinx.coroutines.delay
import net.craftoriya.packetuxui.bukkit.extensions.getMenu
import net.craftoriya.packetuxui.bukkit.extensions.updateItem
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.service.menuService
import net.craftoriya.packetuxui.types.InventoryType
import net.craftoriya.plugin
import org.bukkit.Bukkit
import kotlin.random.Random

class Dynamic4x9 {
    private val stone: ItemStack = ItemStack.builder().type(ItemTypes.STONE).build()
    private val air: ItemStack = ItemStack.builder().type(ItemTypes.AIR).build()

    init {
        plugin.launch {
            for (player in Bukkit.getOnlinePlayers()) {
                val playerMenu = menuService.getMenu(player)

                if (playerMenu != null && playerMenu.name == menu.name) {
                    for (i in 0 until 27) {
                        if (chance(10)) {
                            if (chance(50)) {
                                menu.buttons[i]?.let { it1 ->
                                    menuService.updateItem(
                                        player,
                                        it1.item,
                                        i
                                    )
                                }
                            } else {
                                menuService.updateItem(player, air, i)
                            }
                        }
                    }
                }
            }

            delay(200)
        }
    }


    val menu = Menu(
        name = "<gradient:#ff1493:#1e90ff><bold>Styled Background".toComponent(),
        type = InventoryType.GENERIC9X3,
        buttons = (0 until 27).associateWith { slot ->
            ButtonBuilder()
                .item(
                    ItemBuilder()
                        .itemType(if (slot % 2 == 0) ItemTypes.BLUE_STAINED_GLASS_PANE else ItemTypes.PINK_STAINED_GLASS_PANE)
                        .name("<dark_gray><italic>Background Tile".toComponent())
                        .build()
                )
                .click {
                    menuService.updateItem(it.user, stone, slot)
                }
                .build()
        }
    )

    private fun chance(percent: Int): Boolean {
        require(percent in 0..100) { "Percentage must be between 0 and 100" }
        return Random.nextFloat() * 100 < percent
    }
}


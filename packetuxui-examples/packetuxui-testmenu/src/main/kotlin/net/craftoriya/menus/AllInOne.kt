package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.shynixn.mccoroutine.folia.launch
import kotlinx.coroutines.delay
import net.craftoriya.packetuxui.bukkit.extensions.getMenu
import net.craftoriya.packetuxui.bukkit.extensions.updateItem
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.service.*
import net.craftoriya.packetuxui.types.InventoryType
import net.craftoriya.plugin
import org.bukkit.Bukkit
import kotlin.random.Random

class AllInOne {
    private val stone: ItemStack = ItemStack.builder().type(ItemTypes.STONE).build()
    private val air: ItemStack = ItemStack.builder().type(ItemTypes.AIR).build()
    private val updateButtons = listOf(2, 4, 6, 8, 10)

    fun startUpdate() {
        plugin.launch { // This job is not stopped when the menu closes
            while (true) {
                println("tick")
                for (player in Bukkit.getOnlinePlayers()) {
                    if (menuService.getMenu(player)?.name == menu.name) {
                        for (slot in updateButtons) {
                            if (chance(20)) {
                                val item = if (chance(50)) stone else air
                                menuService.updateItem(player, item, slot)
                            }
                        }
                    }
                }

                delay(200)
            }
        }
    }

    val menu = menu(InventoryType.GENERIC9X4) {
        name = "<gradient:#ff6d2e:#1e90ff><bold>Feature Showcase Menu".toComponent()

        buildAllButtons { slot ->
            when {
                slot in updateButtons -> {
                    item(stone)
                    click { menuService.updateItem(it.user, air, slot) }
                }

                slot % 9 == 0 -> {
                    buildItem {
                        itemType = ItemTypes.GLOWSTONE
                        name = "<yellow><bold>Glowing Stone".toComponent()
                        amount = 1
                    }
                    click {
                        it.user.sendMessage("<green>You clicked on the glowing button!".toComponent())
                        it.user.sendMessage("Button type: ${it.buttonType}".toComponent())
                    }
                }

                slot % 9 == 4 -> {
                    buildItem {
                        itemType = ItemTypes.RED_WOOL
                        name = "<red><bold>Red Wool".toComponent()
                        lore(
                            "<#f7983e>Shiny Red Wool".toComponent(),
                            "<#f7b33e>Perfect for decoration.".toComponent()
                        )
                        amount = 4
                        enchantment(EnchantmentTypes.FIRE_ASPECT, 2, visible = true)
                    }
                    click {
                        it.user.sendMessage("<gold>Clicked on Red Wool!".toComponent())
                        it.user.sendMessage("Item type: ${it.itemStack?.type?.name}".toComponent())
                    }
                    cooldown(4000, 1000) {
                        it.user.sendMessage("<red>Cooldown active. Wait before clicking again.".toComponent())
                    }
                }

                slot % 9 == 8 -> {
                    buildItem {
                        itemType = ItemTypes.ACACIA_SIGN
                        name = "<rainbow>Cool Sign".toComponent()
                        lore("<gray>Invisible enchantment here.".toComponent())
                        amount = 64
                        enchantment(EnchantmentTypes.UNBREAKING, 2, visible = false)
                    }
                    click {
                        it.user.sendMessage("<aqua>You clicked on the Cool Sign!".toComponent())
                    }
                }

                else -> {
                    item { type(ItemTypes.BLUE_STAINED_GLASS_PANE) }
                    click { it.user.sendMessage("<gray><italic>Decorative Tile".toComponent()) }
                }
            }
        }

        cooldown(delay = 6000, freeze = 1200) {
            it.user.sendMessage("<yellow>Menu is on cooldown!".toComponent())
        }
    }

    private fun chance(percent: Int): Boolean {
        require(percent in 0..100) { "Percentage must be between 0 and 100" }
        return Random.nextFloat() * 100 < percent
    }
}

package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.shynixn.mccoroutine.folia.launch
import kotlinx.coroutines.delay
import net.craftoriya.packetuxui.bukkit.extensions.getMenu
import net.craftoriya.packetuxui.bukkit.extensions.updateItem
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.service.menuService
import net.craftoriya.packetuxui.types.InventoryType
import net.craftoriya.plugin
import org.bukkit.Bukkit
import kotlin.random.Random

class AllInOne {
    private val stone: ItemStack = ItemStack.builder().type(ItemTypes.STONE).build()
    private val air: ItemStack = ItemStack.builder().type(ItemTypes.AIR).build()
    private val updateButtons = listOf(2, 4, 6, 8, 10)

    init {
        // Dynamic updates for specific slots

        plugin.launch {
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

    private val glowingItem = ItemBuilder()
        .itemType(ItemTypes.GLOWSTONE)
        .name("<yellow><bold>Glowing Stone".toComponent())
        .amount(1)
        .build()

    private val redWool = ItemBuilder()
        .itemType(ItemTypes.RED_WOOL)
        .name("<red><bold>Red Wool".toComponent())
        .lore(
            "<#f7983e>Shiny Red Wool".toComponent(),
            "<#f7b33e>Perfect for decoration.".toComponent()
        )
        .amount(4)
        .enchantment(EnchantmentTypes.FIRE_ASPECT, 2, visible = true)
        .build()

    private val coolSign = ItemBuilder()
        .itemType(ItemTypes.ACACIA_SIGN)
        .name("<rainbow>Cool Sign".toComponent())
        .lore(
            "<gray>Invisible enchantment here.".toComponent()
        )
        .amount(64)
        .enchantment(EnchantmentTypes.UNBREAKING, 2, visible = false)
        .build()

    private val hoverButton = ButtonBuilder()
        .item(glowingItem)
        .click {
            it.user.sendMessage("<green>You clicked on the glowing button!".toComponent())
            it.user.sendMessage("Button type: ${it.buttonType}".toComponent())
        }
        .build()

    private val cooldownButton = ButtonBuilder()
        .item(redWool)
        .click {
            it.user.sendMessage("<gold>Clicked on Red Wool!".toComponent())
            it.user.sendMessage("Item type: ${it.itemStack?.type?.name}".toComponent())
        }
        .cooldown(
            CooldownComponent(
                delay = 4000,
                execute = { it.user.sendMessage("<red>Cooldown active. Wait before clicking again.".toComponent()) },
                freeze = 1000
            )
        )
        .build()

    private val staticButton = ButtonBuilder()
        .item(coolSign)
        .click {
            it.user.sendMessage("<aqua>You clicked on the Cool Sign!".toComponent())
        }
        .build()

    val menu = Menu(
        name = "<gradient:#ff6d2e:#1e90ff><bold>Feature Showcase Menu".toComponent(),
        type = InventoryType.GENERIC9X4,
        buttons = (0 until 36).associateWith { slot ->
            when {
                slot in updateButtons -> ButtonBuilder()
                    .item(stone)
                    .click { menuService.updateItem(it.user, air, slot) }
                    .build()

                slot % 9 == 0 -> hoverButton
                slot % 9 == 4 -> cooldownButton
                slot % 9 == 8 -> staticButton
                else -> ButtonBuilder()
                    .item(
                        ItemBuilder()
                            .itemType(
                                if (slot % 2 == 0) ItemTypes.BLUE_STAINED_GLASS_PANE else ItemTypes.PINK_STAINED_GLASS_PANE
                            )
                            .name("<gray><italic>Decorative Tile".toComponent())
                            .build()
                    )
                    .build()
            }
        },
        cooldown = CooldownComponent(
            delay = 6000,
            execute = { it.user.sendMessage("<yellow>Menu is on cooldown!".toComponent()) },
            freeze = 1200
        )
    )

    private fun chance(percent: Int): Boolean {
        require(percent in 0..100) { "Percentage must be between 0 and 100" }
        return Random.nextFloat() * 100 < percent
    }
}

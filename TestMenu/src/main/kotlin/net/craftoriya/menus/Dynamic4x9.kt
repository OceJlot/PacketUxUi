package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.craftoriya.common.asyncRepeat
import net.craftoriya.packetuxui.common.toComponent
import net.craftoriya.packetuxui.service.ButtonBuilder
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.service.MenuService
import net.craftoriya.packetuxui.types.InventoryType
import org.bukkit.Bukkit
import kotlin.random.Random

class Dynamic4x9(
    private val service: MenuService
) {
    private val stone: ItemStack = ItemStack.builder().type(ItemTypes.STONE).build()
    private val air: ItemStack = ItemStack.builder().type(ItemTypes.AIR).build()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val updateButtons = listOf(1,3,7,13,26)

    init {
        scope.asyncRepeat(200) {
            for (player in Bukkit.getOnlinePlayers()) {
                if(service.getMenu(player) != null){
                    if(service.getMenu(player)!!.name == menu.name) {
                        for (i in 0 until 27) {
                            if(chance(10)){
                                if(chance(50)){
                                    menu.buttons[i]?.let { it1 -> service.updateItem(player, it1.item, i) }
                                }else{
                                    service.updateItem(player, air, i)
                                }
                            }
                        }
                    }
                }
            }
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
                    service.updateItem(it.player, stone, slot)
                }
                .build()
        }
    )

    private fun chance(percent: Int): Boolean {
        require(percent in 0..100) { "Percentage must be between 0 and 100" }
        return Random.nextFloat() * 100 < percent
    }
}


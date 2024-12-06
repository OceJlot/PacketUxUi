package net.craftoriya.packetuxui.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetuxui.types.ButtonType
import net.craftoriya.packetuxui.types.ClickType
import org.bukkit.entity.Player

data class MenuClickData(
    val wrapper: WrapperPlayClientClickWindow,
    val clickType: Pair<ButtonType, ClickType>,
    val player: Player
)
package net.craftoriya.packetuxui.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetuxui.types.ClickData
import org.bukkit.entity.Player

data class MenuClickData(
    val wrapper: WrapperPlayClientClickWindow,
    val clickType: ClickData,
    val player: Player
)
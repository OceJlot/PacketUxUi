package net.craftoriya.packetuxui.dto

import net.craftoriya.packetuxui.types.ClickData
import org.bukkit.entity.Player

data class WindowClick(
    val player: Player,
    val clickData: ClickData,
    val slot: Int,
)
package net.craftoriya.packetmenuapi.dto

import net.craftoriya.packetmenuapi.types.ClickData
import org.bukkit.entity.Player

data class WindowClickDTO(
    val player: Player,
    val clickData: ClickData,
    val slot: Int,
)
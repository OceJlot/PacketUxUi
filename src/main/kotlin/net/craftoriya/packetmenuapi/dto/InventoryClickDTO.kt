package net.craftoriya.packetmenuapi.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetmenuapi.types.ClickData
import org.bukkit.entity.Player

data class InventoryClickDTO(
    val player: Player,
    val packet: WrapperPlayClientClickWindow,
    val clickData: ClickData
)
package net.craftoriya.packetmenuapi.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetmenuapi.types.ClickType

data class AccumulatedDrag(
    val packet: WrapperPlayClientClickWindow,
    val type: ClickType
)
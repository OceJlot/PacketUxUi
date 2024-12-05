package net.craftoriya.packetuxui.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow

data class AccumulatedDrag(
    val packet: WrapperPlayClientClickWindow,
    val type: net.craftoriya.packetuxui.types.ClickType
)
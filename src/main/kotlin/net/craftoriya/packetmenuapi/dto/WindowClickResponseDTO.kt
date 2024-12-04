package net.craftoriya.packetmenuapi.dto

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetmenuapi.types.ExecuteComponent

data class WindowClickResponseDTO(
    val menuContentPacket: WrapperPlayServerWindowItems?,
    val execute: ((ExecuteComponent) -> Unit)?
)

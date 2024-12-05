package net.craftoriya.packetuxui.dto

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import net.craftoriya.packetuxui.types.ExecuteComponent

data class WindowClickResponse(
    val menuContentPacket: WrapperPlayServerWindowItems?,
    val execute: ((ExecuteComponent) -> Unit)?
)

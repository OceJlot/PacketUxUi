package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.dto.CooldownComponent

data class Button(
    val item: ItemStack,
    val execute: ((ExecuteComponent) -> Unit)? = null,
    val cooldown: CooldownComponent
)
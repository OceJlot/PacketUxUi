package net.craftoriya.packetmenuapi.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetmenuapi.types.ExecuteComponent
import net.craftoriya.packetmenuapi.dto.CooldownComponent

data class Button(
    val item: ItemStack,
    val execute: ((ExecuteComponent) -> Unit)? = null,
    val cooldown: CooldownComponent
)
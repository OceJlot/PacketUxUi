package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.ExecutableComponent

data class Button(
    val item: ItemStack,
    val execute: ExecutableComponent? = null,
    val cooldown: CooldownComponent
)
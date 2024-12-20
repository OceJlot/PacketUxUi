package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.ExecutableComponent
import net.craftoriya.packetuxui.types.ExecutableComponentMarker
import net.craftoriya.packetuxui.types.ExecuteComponent

interface IButtonBuilder {
    fun item(item: ItemStack): IButtonBuilder
    fun click(click: @ExecutableComponentMarker ExecutableComponent): IButtonBuilder
    fun executeCommand(command: Array<String>): IButtonBuilder
    fun makePlayerExecuteCommand(command: Array<String>): IButtonBuilder
    fun cooldown(cooldown: CooldownComponent): IButtonBuilder
    fun build(): Button
}

fun IButtonBuilder.cooldown(
    delay: Long = 0,
    freeze: Long = 0,
    execute: @ExecutableComponentMarker ExecutableComponent? = null
) = cooldown(CooldownComponent(delay, freeze, execute))

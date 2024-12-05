package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.dto.CooldownComponent

interface IButtonBuilder {
    fun item(item: ItemStack): IButtonBuilder
    fun click(click: (ExecuteComponent) -> Unit): IButtonBuilder
    fun executeCommand(command: Array<String>): IButtonBuilder
    fun makePlayerExecuteCommand(command: Array<String>): IButtonBuilder
    fun cooldown(cooldown: CooldownComponent): IButtonBuilder
    fun build(): Button
}
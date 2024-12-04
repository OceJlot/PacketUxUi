package net.craftoriya.packetmenuapi.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetmenuapi.types.ExecuteComponent
import net.craftoriya.packetmenuapi.dto.CooldownComponent

interface IButtonBuilder {
    fun item(item: ItemStack): IButtonBuilder
    fun click(click: (ExecuteComponent) -> Unit): IButtonBuilder
    fun executeCommand(command: Array<String>): IButtonBuilder
    fun makePlayerExecuteCommand(command: Array<String>): IButtonBuilder
    fun cooldown(cooldown: CooldownComponent): IButtonBuilder

    fun build(): Button
}
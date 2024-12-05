package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.dto.CooldownComponent

class ButtonBuilder : IButtonBuilder {

    private var item: ItemStack = ItemStack.EMPTY
    private var click: ((ExecuteComponent) -> Unit)? = null
    private var cooldown: CooldownComponent = CooldownComponent(0)
    private var commands: Array<String>? = null
    private var playerCommands: Array<String>? = null

    override fun item(item: ItemStack): IButtonBuilder {
        this.item = item
        return this
    }

    override fun click(click: (ExecuteComponent) -> Unit): IButtonBuilder {
        this.click = click
        return this
    }

    override fun executeCommand(command: Array<String>): IButtonBuilder {
        this.commands = command
        return this
    }

    override fun makePlayerExecuteCommand(command: Array<String>): IButtonBuilder {
        this.playerCommands = command
        return this
    }

    override fun cooldown(cooldown: CooldownComponent): IButtonBuilder {
        this.cooldown = cooldown
        return this
    }

    override fun build(): Button {
        return Button(item, click, cooldown)
    }
}

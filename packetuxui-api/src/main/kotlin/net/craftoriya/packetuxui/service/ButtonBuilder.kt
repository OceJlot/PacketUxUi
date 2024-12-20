package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.ExecutableComponent

class ButtonBuilder : IButtonBuilder {

    private var item: ItemStack = ItemStack.EMPTY
    private var click: ExecutableComponent? = null
    private var cooldown: CooldownComponent = CooldownComponent(0)
    private var commands: Array<String>? = null
    private var playerCommands: Array<String>? = null

    override fun item(item: ItemStack) = apply { this.item = item }
    override fun click(click: ExecutableComponent) = apply { this.click = click }
    override fun executeCommand(command: Array<String>) = apply { this.commands = command }

    override fun makePlayerExecuteCommand(command: Array<String>) =
        apply { this.playerCommands = command }

    override fun cooldown(cooldown: CooldownComponent) = apply { this.cooldown = cooldown }

    override fun build() = Button(item, click, cooldown)
}

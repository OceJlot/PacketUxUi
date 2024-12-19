package net.craftoriya.packetuxui.dsl

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.Button
import net.craftoriya.packetuxui.service.ItemBuilder
import net.craftoriya.packetuxui.types.ExecuteComponent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class DslButtonsBuilder {
    protected val buttons: MutableMap<Int, Button> = mutableMapOf()

    fun button(slots: Set<Int>, builder: DslButtonBuilder.() -> Unit) {
        button(slots, DslButtonBuilder().apply(builder).build())
    }
    fun button(slots: Set<Int>, button: Button) {
        slots.forEach {
            buttons[it] = button
        }
    }
}

class DslButtonBuilder: DslCooldownBuilder, DslBuilder<Button> {
    private var item: ItemStack = ItemStack.EMPTY
    private var onClick: (ExecuteComponent) -> Unit = {}
    private var cooldown: CooldownComponent = CooldownComponent()

    fun item(builder: ItemBuilder.() -> Unit) {
        item = ItemBuilder().apply(builder).build()
    }
    fun onClick(onClick: (ExecuteComponent) -> Unit) {
        this.onClick = onClick
    }
    override fun cooldown(
        delay: Duration,
        freeze: Duration,
        onCooldown: (ExecuteComponent) -> Unit,
    ) {
        cooldown = DslCooldownBuilderData(delay, freeze, onCooldown).build()
    }

    override fun build(): Button {
        return Button(item, onClick, cooldown)
    }
}

fun createButton(builder: DslButtonBuilder.() -> Unit): Button {
    return DslButtonBuilder().apply(builder).build()
}

context(DslButtonsBuilder)
val Int.slot: Set<Int> get() = setOf(this)

context(DslButtonsBuilder)
val IntRange.slot: Set<Int> get() = this.toSet()


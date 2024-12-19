package net.craftoriya.packetuxui.dsl

import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.service.Menu
import net.craftoriya.packetuxui.types.ExecuteComponent
import net.craftoriya.packetuxui.types.InventoryType
import net.kyori.adventure.text.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun createMenu(
    type: InventoryType,
    title: Component = Component.empty(),
    builder: DslMenuBuilder.() -> Unit = {},
): Menu {
    return DslMenuBuilder(type, title).apply(builder).build()
}

data class DslMenuBuilder(
    private val type: InventoryType,
    private val title: Component,
): DslButtonsBuilder(), DslCooldownBuilder, DslBuilder<Menu> {
    private var cooldown: CooldownComponent = CooldownComponent()
    override fun cooldown(delay: Duration, freeze: Duration, onCooldown: (ExecuteComponent) -> Unit) {
        cooldown = DslCooldownBuilderData(delay, freeze, onCooldown).build()
    }

    override fun build(): Menu {
        return Menu(
            name = title,
            type,
            buttons,
            cooldown
        )
    }

}



//PROOF OF CONCEPTS
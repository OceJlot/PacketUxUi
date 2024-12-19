package net.craftoriya.packetuxui.dsl

import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.ExecuteComponent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class DslCooldownBuilderData(
    val delay: Duration = 5.seconds,
    val freeze: Duration = 1.seconds,
    val execute: (ExecuteComponent) -> Unit,
): DslBuilder<CooldownComponent> {
    override fun build() = CooldownComponent(
        delay = delay.inWholeMilliseconds,
        freeze = freeze.inWholeMilliseconds,
        execute = execute
    )
}

interface DslCooldownBuilder {
    fun cooldown(
        delay: Duration = 5.seconds,
        freeze: Duration = 1.seconds,
        onCooldown: (ExecuteComponent) -> Unit
    )
}


package net.craftoriya.packetuxui.types

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.user.User

@DslMarker
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class ExecutableComponentMarker

typealias ExecutableComponent = (ExecuteComponent) -> Unit

data class ExecuteComponent(
    val user: User,
    val buttonType: ButtonType,
    val slot: Int,
    val itemStack: ItemStack?
)

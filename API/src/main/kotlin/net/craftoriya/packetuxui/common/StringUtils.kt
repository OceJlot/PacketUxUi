package net.craftoriya.packetuxui.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

@Deprecated("Try dsl like syntax", ReplaceWith(
    "this.asComponent",
    "net.craftoriya.packetuxui.common.StringUtilsKt.getAsComponent"
)) // try to use the dsl approach to such things
fun String.toComponent() = MiniMessage.miniMessage().deserialize(this)

@Deprecated("Try dsl like syntax", ReplaceWith(
    "this.asPlain",
    "net.craftoriya.packetuxui.common.StringUtilsKt.getAsPlain"
))
fun Component.toPlain() = PlainTextComponentSerializer.plainText().serialize(this)


val String.asComponent get() = MiniMessage.miniMessage().deserialize(this)
val Component.asPlain get() = PlainTextComponentSerializer.plainText().serialize(this)
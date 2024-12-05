package net.craftoriya.packetuxui.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun String.toComponent() = MiniMessage.miniMessage().deserialize(this)
fun Component.toPlain() = PlainTextComponentSerializer.plainText().serialize(this)
package net.craftoriya.packetuxui.dsl

interface DslBuilder<T: Any> {
    fun build(): T
}
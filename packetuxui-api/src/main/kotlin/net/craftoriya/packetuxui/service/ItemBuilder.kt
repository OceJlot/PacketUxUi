package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.craftoriya.packetuxui.common.mutableObject2IntMapOf
import net.craftoriya.packetuxui.common.mutableObjectListOf
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

open class ItemBuilder {
    var itemType: ItemType = ItemTypes.AIR
    var name: Component? = null
    var lore = mutableObjectListOf<Component>()
    var amount = 1
    var enchantments = mutableObject2IntMapOf<EnchantmentType>()
    var enchantVisibility = true
    var modelData: Int? = null

    fun itemType(itemType: ItemType) = apply { this.itemType = itemType }
    fun name(name: Component) = apply { this.name = name }

    fun lore(lore: MutableList<Component>) = apply {
        this.lore += lore.map {
            it.decorationIfAbsent(
                TextDecoration.ITALIC,
                TextDecoration.State.FALSE
            )
        }
    }

    fun lore(lore: Component) = apply {
        this.lore += lore.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
    }

    fun lore(vararg lore: Component) = apply {
        this.lore += lore.map {
            it.decorationIfAbsent(
                TextDecoration.ITALIC,
                TextDecoration.State.FALSE
            )
        }
    }

    fun amount(amount: Int) = apply { this.amount = amount }

    fun enchantments(
        enchantments: MutableMap<EnchantmentType, Int>,
        visible: Boolean = true
    ) = apply {
        this.enchantments += enchantments
        this.enchantVisibility = visible
    }

    fun enchantments(
        vararg enchantments: Pair<EnchantmentType, Int>,
        visible: Boolean = true
    ) = apply {
        this.enchantments += enchantments
        this.enchantVisibility = visible
    }

    fun enchantment(
        enchantment: EnchantmentType,
        level: Int,
        visible: Boolean = true
    ) = apply {
        this.enchantments[enchantment] = level
        this.enchantVisibility = visible
    }

    fun cmd(cmd: Int) = apply { this.modelData = cmd }

    open fun build(): ItemStack {
        val item = ItemStack.builder()
            .type(itemType)
            .component(ComponentTypes.LORE, ItemLore(lore))
            .amount(amount)
            .component(
                ComponentTypes.ENCHANTMENTS,
                ItemEnchantments(enchantments, enchantVisibility)
            )
        modelData?.let { item.component(ComponentTypes.CUSTOM_MODEL_DATA, it) }
        name?.let { item.component(ComponentTypes.ITEM_NAME, it) }
        return item.build()
    }
}
package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.kyori.adventure.text.Component
import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.kyori.adventure.text.format.TextDecoration

open class ItemBuilder {
    var itemType: ItemType = ItemTypes.AIR
    var name: Component? = null
    var lore: MutableList<Component> = mutableListOf()
    var amount: Int = 1
    var enchantments: MutableMap<EnchantmentType, Int> = mutableMapOf()
    var enchantVisibility: Boolean = true
    var cmd: Int? = null

    fun itemType(itemType: ItemType): ItemBuilder {
        this.itemType = itemType
        return this
    }

    fun name(name: Component): ItemBuilder {
        this.name = name
        return this
    }

    fun lore(lore: List<Component>): ItemBuilder {
        this.lore += lore.map { it.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }
        return this
    }

    fun lore(lore: Component): ItemBuilder {
        this.lore += lore.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        return this
    }

    fun lore(vararg lore: Component): ItemBuilder {
        this.lore += lore.map { it.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }
        return this
    }


    fun amount(amount: Int): ItemBuilder {
        this.amount = amount
        return this
    }

    fun enchantments(enchantments: MutableMap<EnchantmentType, Int>, visible: Boolean): ItemBuilder {
        this.enchantments += enchantments
        this.enchantVisibility = visible
        return this
    }

    fun enchantment(enchantment: EnchantmentType, level: Int, visible: Boolean = true): ItemBuilder {
        this.enchantments = this.enchantments.toMutableMap()
        this.enchantments[enchantment] = level
        this.enchantVisibility = visible
        return this
    }

    fun cmd(cmd: Int): ItemBuilder {
        this.cmd = cmd
        return this
    }

    open fun build(): ItemStack {
        val item = ItemStack.builder()
            .type(itemType)
            .component(ComponentTypes.LORE, ItemLore(lore))
            .amount(amount)
            .component(ComponentTypes.ENCHANTMENTS, ItemEnchantments(enchantments, enchantVisibility))
        cmd?.let { item.component(ComponentTypes.CUSTOM_MODEL_DATA, it) }
        name?.let { item.component(ComponentTypes.ITEM_NAME, it) }
        return item.build()
    }
}

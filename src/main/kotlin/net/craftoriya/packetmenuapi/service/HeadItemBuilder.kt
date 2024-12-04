package net.craftoriya.packetmenuapi.service

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes

class HeadItemBuilder(): ItemBuilder() {
    var base64: String = ""

    fun headTextureFromName(name: String): HeadItemBuilder {
        println("Fetching head texture for name: $name")
        return this
    }

    fun headTextureFromUrl(url: String): HeadItemBuilder {
        println("Fetching head texture from URL: $url")
        return this
    }

    fun headTextureFromUuid(uuid: String): HeadItemBuilder {
        println("Fetching head texture for UUID: $uuid")
        return this
    }
    override fun build(): ItemStack {
        val item = ItemStack.builder()
            .type(itemType)
            .component(ComponentTypes.LORE, ItemLore(lore))
            .amount(amount)
            .component(ComponentTypes.ENCHANTMENTS, ItemEnchantments(enchantments, enchantVisibility))
        cmd?.let { item.component(ComponentTypes.CUSTOM_MODEL_DATA, it) }
        name?.let { item.component(ComponentTypes.ITEM_NAME, it) }

        if (itemType == ItemTypes.PLAYER_HEAD) {
            item.component(
                ComponentTypes.PROFILE, ItemProfile(null, null, listOf(
                ItemProfile.Property("textures", base64, null)))
            )
        }
        return item.build()
    }
}
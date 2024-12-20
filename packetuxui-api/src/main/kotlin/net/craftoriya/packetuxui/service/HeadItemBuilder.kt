package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import kotlinx.coroutines.launch
import net.craftoriya.packetuxui.util.PlayerSkinFetcher
import net.craftoriya.packetuxui.util.SkinFetcherScope
import java.util.*

class HeadItemBuilder : ItemBuilder() {
    var base64: String = ""

    fun headTextureFromName(name: String) = apply {
        println("Fetching head texture for name: $name")
    }

    fun headTextureFromUrl(url: String) = apply {
        println("Fetching head texture from URL: $url")
    }

    fun headTextureFromUuid(uuid: UUID) = apply {
        println("Fetching head texture for UUID: $uuid")
        SkinFetcherScope.launch {
            val properties = PlayerSkinFetcher.fetchSkin(uuid)
            val texture = properties.firstOrNull { it.name == "textures" }
            if (texture != null) {
                base64 = texture.value
            } else {
                println("Failed to fetch head texture for UUID: $uuid")
            }
        }
    }

    override fun build(): ItemStack {
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

        if (itemType == ItemTypes.PLAYER_HEAD) {
            item.component(
                ComponentTypes.PROFILE,
                ItemProfile(null, null, listOf(ItemProfile.Property("textures", base64, null)))
            )
        }
        return item.build()
    }
}
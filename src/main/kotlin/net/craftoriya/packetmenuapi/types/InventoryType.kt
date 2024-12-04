package net.craftoriya.packetmenuapi.types

enum class InventoryType(private val slots: Int) {
    GENERIC9X1(9),
    GENERIC9X2(18),
    GENERIC9X3(27),
    GENERIC9X4(36),
    GENERIC9X5(45),
    GENERIC9X6(54),
    GENERIC3X3(9),
    CRAFTER3X3(10),
    ANVIL(3),
    BEACON(1),
    BLAST_FURNACE(3),
    BREWING_STAND(4),
    CRAFTING_TABLE(10),
    ENCHANTMENT_TABLE(2),
    FURNACE(3),
    GRINDSTONE(3),
    HOPPER(5),
    LECTERN(0),
    LOOM(4),
    VILLAGER(3),
    SHULKER_BOX(27),
    SMITHING_TABLE(4),
    SMOKER(3),
    CARTOGRAPHY_TABLE(3),
    STONECUTTER(2),
    CUSTOM9x7(63),
    CUSTOM9x8(72),
    CUSTOM9x9(81),
    CUSTOM9x10(90);


    val size = slots
    val lastIndex = slots - 1

    fun id() = if (ordinal < 24) ordinal else 5
}
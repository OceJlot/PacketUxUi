package net.craftoriya.packetuxui.user

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*

object UserManager {

    private val _users = Object2ObjectOpenHashMap<UUID, User>()
    val users = Object2ObjectMaps.unmodifiable(_users)

    var userCreator: ((UUID) -> User)? = null

    /**
     * Gets a user from the cache or creates a new one if it doesn't exist.
     */
    operator fun get(uuid: UUID): User {
        return _users.computeIfAbsent(uuid) { newUuid: UUID ->
            userCreator?.invoke(newUuid) ?: throw IllegalStateException("User creator is not set")
        }
    }

    /**
     * Removes a user from the cache.
     */
    fun remove(uuid: UUID) {
        _users.remove(uuid)
    }

}
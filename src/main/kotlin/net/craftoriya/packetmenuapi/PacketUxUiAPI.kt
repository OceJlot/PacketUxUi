package net.craftoriya.packetmenuapi

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import net.craftoriya.packetmenuapi.controller.PacketListener
import net.craftoriya.packetmenuapi.service.MenuService


object PacketUxUiAPI {
    private var isInitialized = false
    private lateinit var service: MenuService

    fun isInitialized() = isInitialized

    fun getService(): MenuService {
        if (!isInitialized) {
            throw IllegalStateException("PacketUxUiAPI is not initialized. Call init() first.")
        }
        return service
    }

    fun init() {
        if (isInitialized) return
        val packetEvents = PacketEvents.getAPI()

        if (!packetEvents.isLoaded) {
            packetEvents.load()
        }
        if (!packetEvents.isInitialized) {
            packetEvents.init()
        }

        service = MenuService()
        packetEvents.eventManager.registerListener(
            PacketListener(service).asAbstract(PacketListenerPriority.HIGHEST)
        )
        isInitialized = true
    }

    fun terminate() {
        if (!isInitialized) return
        val packetEvents = PacketEvents.getAPI()
        if (!packetEvents.isTerminated) {
            packetEvents.terminate()
        }
        isInitialized = false
    }
}



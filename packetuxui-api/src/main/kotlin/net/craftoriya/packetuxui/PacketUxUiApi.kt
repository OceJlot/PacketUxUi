package net.craftoriya.packetuxui

import com.github.retrooper.packetevents.PacketEvents
import net.craftoriya.packetuxui.controller.PacketListener

abstract class PacketUxUiApi {
    private var initialized = false

    abstract fun initPlatform()
    abstract fun terminatePlatform()

    fun init() {
        if (initialized) return
        val packetEvents = PacketEvents.getAPI()

        if (!packetEvents.isLoaded) {
            packetEvents.load()
        }

        if (!packetEvents.isInitialized) {
            packetEvents.init()
        }

        packetEvents.eventManager.registerListener(PacketListener)

        initPlatform()

        initialized = true
    }

    fun terminate() {
        if (!initialized) return

        val packetEvents = PacketEvents.getAPI()

        if (!packetEvents.isTerminated) {
            packetEvents.terminate()
        }

        terminatePlatform()

        initialized = false
    }
}



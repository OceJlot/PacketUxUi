package net.craftoriya.packetuxui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import net.craftoriya.packetuxui.controller.BukkitListener
import net.craftoriya.packetuxui.controller.PacketListener
import net.craftoriya.packetuxui.service.MenuService
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


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

    fun init(plugin: JavaPlugin) {
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
        Bukkit.getPluginManager().registerEvents(BukkitListener(service), plugin)
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



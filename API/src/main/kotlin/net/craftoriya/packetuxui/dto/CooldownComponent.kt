package net.craftoriya.packetuxui.dto

import net.craftoriya.packetuxui.types.ExecuteComponent
import kotlin.math.max

data class CooldownComponent(
    val delay: Long = 0,
    val execute: ((ExecuteComponent) -> Unit)? = null,
    val freeze: Long = 0,

    ){
    private var expireTime: Long = 0
    private var expireFreeze: Long = 0

    fun combine(cooldown: CooldownComponent): CooldownComponent {
        val execute =
            if (this.execute != null && cooldown.execute != null) {
                if (this.delay >= cooldown.delay) this.execute else cooldown.execute
            } else this.execute ?: cooldown.execute

        val combined = CooldownComponent(
            delay = max(this.delay, cooldown.delay),
            execute = execute,
            freeze = max(this.freeze, cooldown.freeze)
        )
        combined.expireTime = max(this.expireTime, cooldown.expireTime)
        combined.expireFreeze = max(this.expireFreeze, cooldown.expireFreeze)
        return combined
    }

    fun resetFreeze(){
        expireFreeze = System.currentTimeMillis() + freeze
    }
    fun resetTime() {
        expireTime = System.currentTimeMillis() + delay
    }
    fun isFreezeExpired(now: Long): Boolean = now >= expireFreeze
    fun isTimeExpired(now: Long): Boolean = now >= expireTime
}
package com.dedztbh.demagica.global

import com.dedztbh.demagica.util.DeOS
import com.dedztbh.demagica.util.TickTaskManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Created by DEDZTBH on 2019-07-12.
 * Project DEMagica
 */

open class TickOS : DeOS<TickTaskManager>({ TickTaskManager() }) {
    fun tick() = processMap.forEach { (key, tickTaskManager) ->
        if (key != null) {
            tickTaskManager.tick()
        }
    }
}

@JvmField
val ServerTickOS = object : TickOS() {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ServerTickEvent) = super.tick()
}

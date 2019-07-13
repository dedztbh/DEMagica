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

@JvmField
val ServerTickOS = object : DeOS<TickTaskManager>({ TickTaskManager() }) {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ServerTickEvent) = processMap.forEach { (key, tickTaskManager) ->
        if (key != null) {
            tickTaskManager.tick()
        }
    }
}
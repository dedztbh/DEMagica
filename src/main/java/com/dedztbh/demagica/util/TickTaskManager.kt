package com.dedztbh.demagica.util

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


/**
 * Created by DEDZTBH on 19-2-13.
 * Project DEMagica
 */
class TickTaskManager {

    @SubscribeEvent
    fun clientTicked(event: TickEvent.ClientTickEvent) {

    }

    val tasks: List<() -> Unit> = mutableListOf()



}
package com.dedztbh.demagica.global

import com.dedztbh.demagica.util.TickOS
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Created by DEDZTBH on 2019-07-10.
 * Project DEMagica
 */


val ServerTickOS = TickOS()

@Mod.EventBusSubscriber
object Ticker {
    @SubscribeEvent
    @JvmStatic
    fun onServerTick(event: TickEvent.ServerTickEvent) {
        ServerTickOS.tick()
    }
}
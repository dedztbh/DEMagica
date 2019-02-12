package com.dedztbh.demagica

import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.proxy.CommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = DEMagica.MODID, name = DEMagica.NAME, version = DEMagica.VERSION)
class DEMagica {
    companion object {
        const val MODID = "demagica"
        const val NAME = "DEMagica"
        const val VERSION = "1.0"

        @JvmStatic
        lateinit var logger: Logger

        @JvmStatic
        @SidedProxy(clientSide = "com.dedztbh.demagica.proxy.ClientProxy", serverSide = "com.dedztbh.demagica.proxy.ServerProxy")
        lateinit var proxy: CommonProxy

        @JvmStatic
        @Mod.Instance
        lateinit var instance: DEMagica
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        proxy.postInit(e)
        logger.debug("Dalao:${Config.dalaofy}")
    }

}

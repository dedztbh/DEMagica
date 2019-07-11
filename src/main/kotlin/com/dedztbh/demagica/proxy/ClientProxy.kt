package com.dedztbh.demagica.proxy

import com.dedztbh.demagica.global.ModBlocks
import com.dedztbh.demagica.global.ModItems
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(Side.CLIENT)
class ClientProxy : CommonProxy() {
    override fun preInit(e: FMLPreInitializationEvent) {
        super.preInit(e)
    }

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun registerModels(event: ModelRegistryEvent) {
            ModBlocks.initModels()
            ModItems.initModels()
        }
    }
}
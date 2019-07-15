package com.dedztbh.demagica.proxy

import com.dedztbh.demagica.global.ModBlocks
import com.dedztbh.demagica.global.ModItems
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.input.Keyboard

@Mod.EventBusSubscriber(Side.CLIENT)
class ClientProxy : CommonProxy() {
    override fun init(e: FMLInitializationEvent) {
        super.init(e)
        keyBindings.forEach(ClientRegistry::registerKeyBinding)
    }

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

        @JvmStatic
        var keyBindings = arrayOf(
                KeyBinding("key.zoom.desc", Keyboard.KEY_Z, "key.magicbeans.category")
        )
    }
}
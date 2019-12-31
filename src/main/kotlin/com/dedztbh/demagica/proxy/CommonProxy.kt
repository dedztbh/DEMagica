package com.dedztbh.demagica.proxy

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.global.IDEMagicaBlock
import com.dedztbh.demagica.global.ModBlocks
import com.dedztbh.demagica.global.ModItems
import com.dedztbh.demagica.projectile.*
import com.dedztbh.demagica.util.Open
import com.dedztbh.demagica.util.then
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import java.io.File


@Mod.EventBusSubscriber
@Open
class CommonProxy {
    companion object {
        // Config instance
        @JvmStatic
        lateinit var config: Configuration

        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            ModBlocks.stuffOf(Block::class, IDEMagicaBlock::class, newInstance = true).forEach {
                event.registry.register(it)
                it.hasTileEntity(it.defaultState) then {
                    it as IDEMagicaBlock
                    GameRegistry.registerTileEntity(it.getTEClass(), it.registryName)
                }
            }
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            ModItems.stuffOf(Item::class, newInstance = true).forEach {
                event.registry.register(it)
            }

            ModBlocks.stuffOf(Block::class).forEach {
                event.registry.register(ItemBlock(it).setRegistryName(it.registryName))
            }
        }
    }

    fun preInit(e: FMLPreInitializationEvent) {
        val directory: File = e.modConfigurationDirectory
        config = Configuration(File(directory.path, "demagica.cfg"))
        Config.readConfig()

        //Register projectiles
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicball"), MagicBall::class.java, "MagicBall", 10, DEMagica.instance, 512, 1, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBall::class.java, MagicBallRenderFactory())
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicbomb"), MagicBomb::class.java, "MagicBomb", 11, DEMagica.instance, 512, 1, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBomb::class.java, MagicBallRenderFactory())
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicballheavy"), MagicBallHeavy::class.java, "MagicBallHeavy", 12, DEMagica.instance, 512, 1, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBallHeavy::class.java, MagicBallRenderFactory())
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicballkatyusha"), MagicBallKatyusha::class.java, "MagicBallKatyusha", 13, DEMagica.instance, 512, 1, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBallKatyusha::class.java, MagicBallRenderFactory())
    }

    fun init(e: FMLInitializationEvent) {}

    fun postInit(e: FMLPostInitializationEvent) {
        if (config.hasChanged()) {
            config.save()
        }
    }
}
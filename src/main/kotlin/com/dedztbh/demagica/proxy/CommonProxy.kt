package com.dedztbh.demagica.proxy

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.blocks.BlockMagic
import com.dedztbh.demagica.blocks.tileEntities.BlockMagicTileEntity
import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.global.ModBlocks
import com.dedztbh.demagica.items.ItemMagicGun
import com.dedztbh.demagica.items.ItemMagicStick
import com.dedztbh.demagica.projectile.*
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
open class CommonProxy {
    companion object {
        // Config instance
        @JvmStatic
        lateinit var config: Configuration

        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            event.registry.register(BlockMagic())
            GameRegistry.registerTileEntity(BlockMagicTileEntity::class.java, "${DEMagica.MODID}_magic")
        }

        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            event.registry.apply {
                register(ItemMagicStick())
                register(ItemBlock(ModBlocks.blockMagic).setRegistryName(ModBlocks.blockMagic.registryName))
                register(ItemMagicGun())
            }
        }
    }

    open fun preInit(e: FMLPreInitializationEvent) {
        val directory = e.modConfigurationDirectory
        config = Configuration(File(directory.path, "demagica.cfg"))
        Config.readConfig()

        //Register projectiles
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicball"), MagicBall::class.java, "MagicBall", 10, DEMagica.instance, 512, 2, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBall::class.java, MagicBallRenderFactory())
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicbomb"), MagicBomb::class.java, "MagicBomb", 11, DEMagica.instance, 512, 2, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBomb::class.java, MagicBallRenderFactory())
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicballheavy"), MagicBallHeavy::class.java, "MagicBallHeavy", 12, DEMagica.instance, 512, 2, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBallHeavy::class.java, MagicBallRenderFactory())
        EntityRegistry.registerModEntity(ResourceLocation("demagica:magicballkatyusha"), MagicBallKatyusha::class.java, "MagicBallKatyusha", 13, DEMagica.instance, 512, 2, true)
        RenderingRegistry.registerEntityRenderingHandler(MagicBallKatyusha::class.java, MagicBallRenderFactory())
    }

    open fun init(e: FMLInitializationEvent) {}

    open fun postInit(e: FMLPostInitializationEvent) {
        if (config.hasChanged()) {
            config.save()
        }
    }
}
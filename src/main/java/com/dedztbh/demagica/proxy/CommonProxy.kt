package com.dedztbh.demagica.proxy

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.blocks.BlockMagic
import com.dedztbh.demagica.blocks.tileEntities.BlockMagicTileEntity
import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.global.ModBlocks
import com.dedztbh.demagica.items.ItemMagicStick
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
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
                event.registry.let {
                    it.register(ItemMagicStick())
                    it.register(ItemBlock(ModBlocks.blockMagic).setRegistryName(ModBlocks.blockMagic.registryName))
                }
        }
    }

    open fun preInit(e: FMLPreInitializationEvent) {
        val directory = e.modConfigurationDirectory
        config = Configuration(File(directory.path, "demagica.cfg"))
        Config.readConfig()
    }

    open fun init(e: FMLInitializationEvent) {}

    open fun postInit(e: FMLPostInitializationEvent) {
        if (config.hasChanged()) {
            config.save()
        }
    }
}
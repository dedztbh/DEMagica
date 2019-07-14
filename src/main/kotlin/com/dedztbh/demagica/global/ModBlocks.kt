package com.dedztbh.demagica.global

import com.dedztbh.demagica.blocks.BlockMagic
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ModBlocks {
    companion object {
        @JvmStatic
        @GameRegistry.ObjectHolder("demagica:magicblock")
        lateinit var blockMagic: BlockMagic

        @SideOnly(Side.CLIENT)
        fun initModels() {
            blockMagic.initModel()
        }
    }
}
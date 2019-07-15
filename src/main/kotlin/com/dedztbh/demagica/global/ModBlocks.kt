package com.dedztbh.demagica.global

import com.dedztbh.demagica.blocks.BlockMagic
import net.minecraftforge.fml.common.registry.GameRegistry

object ModBlocks : DEMagicaModStuff() {
    @JvmStatic
    @GameRegistry.ObjectHolder("demagica:magicblock")
    lateinit var blockMagic: BlockMagic
}
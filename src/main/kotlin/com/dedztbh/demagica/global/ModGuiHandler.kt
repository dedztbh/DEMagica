package com.dedztbh.demagica.global

import com.dedztbh.demagica.blocks.containers.BlockMagicContainer
import com.dedztbh.demagica.blocks.containers.BlockMagicGui
import com.dedztbh.demagica.blocks.tileEntities.BlockMagicTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler


/**
 * Created by DEDZTBH on 19-7-14.
 * Project DEMagica
 */

class ModGuiHandler : IGuiHandler {
    companion object {
        const val MAGIC = 0
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) =
            when (ID) {
                MAGIC -> BlockMagicGui(getServerGuiElement(ID, player, world, x, y, z)!!, player.inventory)
                else -> null
            }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) =
            when (ID) {
                MAGIC -> BlockMagicContainer(player.inventory, world.getTileEntity(BlockPos(x, y, z)) as BlockMagicTileEntity)
                else -> null
            }
}
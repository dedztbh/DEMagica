package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ItemMagicStick : Item() {
    init {
        setRegistryName("magicstick")
        unlocalizedName = DEMagica.MODID + ".magicstick"
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val entity = playerIn.rayTrace(8.0, 1f)?.blockPos?.let {
            worldIn.getTileEntity(it)
        }
        if (entity != null) {
            when (entity) {
                is IFluidHandler -> {
                    entity.fill(FluidRegistry.getFluidStack("steam", Int.MAX_VALUE), true)
                }
                is IFluidTank -> {
                    entity.fill(FluidRegistry.getFluidStack("steam", Int.MAX_VALUE), true)
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

}
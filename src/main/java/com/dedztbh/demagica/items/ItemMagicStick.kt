package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
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
            //TODO: Fix fluid container recognition
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.let { fluidCapabililty ->
                entity.hasCapability(fluidCapabililty, null).let {
                    if (it) {
                        entity.getCapability(fluidCapabililty, null)
                                ?.fill(FluidRegistry.getFluidStack("steam", Int.MAX_VALUE), true)
                                .let {
                                    if (!worldIn.isRemote) {
                                        TextComponentString("Able to fill ${it}mb").let { component ->
                                            component.style.color = TextFormatting.GREEN
                                            playerIn.sendStatusMessage(component, false)
                                        }
                                    }
                                }
                    }
                }
            }
//
//            when (entity) {
//                is IFluidHandler -> {
//                    entity.fill(FluidRegistry.getFluidStack("steam", Int.MAX_VALUE), true)
//                }
//                is IFluidTank -> {
//                    entity.fill(FluidRegistry.getFluidStack("steam", Int.MAX_VALUE), true)
//                }
//            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
        player.lookVec.apply {
            val pow = 20
            entity.addVelocity(x * pow, y * pow, z * pow)
        }

        GlobalScope.launch {
            delay(1000)
            entity?.apply {
                entityWorld.createExplosion(this, posX, posY, posZ, 20f, true)
            }
        }

        return false
    }
}
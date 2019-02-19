package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.util.TickTaskManager
import com.dedztbh.demagica.util.isLocal
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ItemMagicStick : Item() {

    private val taskManager: TickTaskManager

    init {
        setRegistryName("magicstick")
        unlocalizedName = DEMagica.MODID + ".magicstick"

        creativeTab = CreativeTabs.TOOLS

        taskManager = TickTaskManager.create(this)
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (worldIn.isLocal()) {
            val entity = worldIn.getTileEntity(pos)
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.let { fluidCapabililty ->
                entity?.hasCapability(fluidCapabililty, null)?.let {
                    if (it) {
                        entity.getCapability(fluidCapabililty, null)
                                ?.fill(FluidRegistry.getFluidStack("steam", Int.MAX_VALUE), true)
                                .let {
                                    if (worldIn.isLocal()) {
                                        TextComponentString("Able to fill ${it}mb").let { component ->
                                            component.style.color = TextFormatting.GREEN
                                            player.sendStatusMessage(component, false)
                                        }
                                    }
                                }
                    }
                }
            }
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }

    override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
        if (entity.world.isLocal()) {
            player.lookVec.apply {
                val pow = 20
                entity.addVelocity(x * pow, y * pow, z * pow)
            }

            taskManager.runTask(1.0, false) {
                entity.apply {
                    world.createExplosion(player, posX, posY, posZ, 5f, true)
                    world.playSound(player, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 10f, 0.5f)
                }
            }
        }

        return false
    }

}
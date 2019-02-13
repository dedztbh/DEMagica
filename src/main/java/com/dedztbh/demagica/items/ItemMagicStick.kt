package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.projectile.MagicBall
import com.dedztbh.demagica.util.TickTaskManager
import com.dedztbh.demagica.util.isLocal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
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

        taskManager = TickTaskManager.create(this)
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

//    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
//
//        return super.onItemRightClick(worldIn, playerIn, handIn)
//    }

    var delayedTaskFiring: TickTaskManager.DelayedTask? = null

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (worldIn.isLocal()) {
            if (entityIn is EntityPlayer) {
                //TODO Fix key
                if (entityIn.isHandActive) {
                    if (delayedTaskFiring!!.removedFlag) {
                        delayedTaskFiring = null
                    }
                    if (delayedTaskFiring == null) {
                        delayedTaskFiring = taskManager.runDelayedTask(0.25, false) {
                            worldIn.spawnEntity(MagicBall(worldIn = worldIn, throwerIn = entityIn))
                        }
                    }
                }
            }
        }
    }

    override fun onLeftClickEntity(stack: ItemStack, player: EntityPlayer, entity: Entity): Boolean {
        if (!entity.world.isRemote) {
            player.lookVec.apply {
                val pow = 20
                entity.setVelocity(x * pow, y * pow, z * pow)
            }

            GlobalScope.launch {
                delay(1000)
                taskManager.runSync {
                    entity.apply {
                        if (!entityWorld.isRemote) {
                            entityWorld.createExplosion(player, posX, posY, posZ, 5f, true)
                        }
                    }
                }
            }
        }

        return false
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

    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean {
        entityLiving.apply {
            if (entityWorld.isRemote) {
                entityWorld.spawnEntity(MagicBall(worldIn = entityWorld, throwerIn = this).apply {
                    setVelocityToMultipleOfVecLook(10.0)
                    explosivePower = 5f
                })
            }
        }

        return false
    }
}
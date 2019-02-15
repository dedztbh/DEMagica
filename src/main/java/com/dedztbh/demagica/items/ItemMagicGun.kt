package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.projectile.MagicBall
import com.dedztbh.demagica.projectile.MagicBomb
import com.dedztbh.demagica.util.TickTaskManager
import com.dedztbh.demagica.util.isLocal
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemMagicGun : ItemBow() {

    private val taskManager: TickTaskManager

    init {
        setRegistryName("magicgun")
        unlocalizedName = DEMagica.MODID + ".magicgun"

        creativeTab = CreativeTabs.COMBAT
        maxStackSize = 1

        taskManager = TickTaskManager.create(this)
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    var delayedTaskFiring: TickTaskManager.DelayedTask? = null

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        playerIn.world.apply {
            if (isLocal()) {
                if (delayedTaskFiring == null || delayedTaskFiring!!.removedFlag) {
                    delayedTaskFiring = taskManager.runDelayedTask(0.25, true, true) {
                        spawnEntity(MagicBall(this, playerIn).apply {
                            playerIn.apply {
                                //                                println()
//                                println("""
//
//                                    $rotationPitch
//                                    $rotationYaw
//                                """.trimIndent())
                                // TODO： Fix display offset
                                shoot(this, rotationPitch, rotationYaw, 0f, 20f, 1f)
                            }
                        })
                    }

                } else if (!delayedTaskFiring!!.isAboutToTerminate()) {
                    taskManager.terminateDelayedTask(delayedTaskFiring!!)
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        if (worldIn.isLocal()) {
            if (delayedTaskFiring != null) {
                taskManager.terminateDelayedTask(delayedTaskFiring!!)
                delayedTaskFiring = null
            }
        }
    }

    override fun getMaxItemUseDuration(stack: ItemStack): Int = Int.MAX_VALUE


    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean {
        entityLiving.world.apply {
            if (isLocal()) {
                spawnEntity(MagicBomb(this, entityLiving as EntityPlayer).apply {
                    entityLiving.apply {
                        shoot(this, rotationPitch, rotationYaw, 0f, 2.0f, 1f)
                    }
                })
            }
        }
        return false
    }
}
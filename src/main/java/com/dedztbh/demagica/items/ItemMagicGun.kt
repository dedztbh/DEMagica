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
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

const val FIRE_INTERVAL = 0.25

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

    private fun clearFiringTask() {
        firingTask?.apply {
            if (!isTerminated && !isAboutToTerminate()) {
                terminate()
                firingTask = null
            }
        }
    }

    var firingTask: TickTaskManager.Task? = null

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        worldIn.apply {
            if (isLocal()) {
//                println("onItemRightClick")
                if (firingTask != null) {
                    clearFiringTask()
                }
                firingTask = taskManager.runTask(if (playerIn.isSneaking) 0.05 else FIRE_INTERVAL, repeat = true, startImmediately = true) {
                    worldIn.spawnEntity(
                            MagicBall(this, playerIn)
                                    .apply {
                                        shoot(playerIn, 5f, 1f)
                                    })
                }
            }
        }
        playerIn.activeHand = handIn
        return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn))
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        if (worldIn.isLocal()) {
//            println("onPlayerStoppedUsing")
            clearFiringTask()
        }
    }

    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean {
        entityLiving.world.apply {
            if (isLocal()) {
                spawnEntity(
                        MagicBomb(this, entityLiving as EntityPlayer)
                                .apply {
                                    shoot(entityLiving, 3f, 1f)
                                })
            }
        }
        return false
    }
}
package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.projectile.MagicBall
import com.dedztbh.demagica.projectile.MagicBallHeavy
import com.dedztbh.demagica.projectile.MagicBomb
import com.dedztbh.demagica.util.TickTaskManager
import com.dedztbh.demagica.util.isLocal
import kotlinx.coroutines.*
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

class ItemMagicGun : ItemBow() {

    private val fireDelayMs = 50L

    private val heavyExtraDelayMs = 50L

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

    var runningCoroutineTerminationFlag = false
    var firingTask: TickTaskManager.Task? = null
    var runningCoroutine: Job? = null
    private fun asyncShootMagicBall(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand, doShoot: Boolean = true): Job {
        return GlobalScope.launch {
            if (runningCoroutineTerminationFlag) {
                runningCoroutine = null
                return@launch
            }
            if (doShoot) {
                if (!playerIn.isSneaking) {
                    firingTask = taskManager.runTask {
                        worldIn.spawnEntity(
                                MagicBall(worldIn, playerIn)
                                        .apply {
                                            shoot(playerIn, 5f, 1f)
                                        })
                    }
                } else {
                    firingTask = taskManager.runTask {
                        worldIn.spawnEntity(
                                MagicBallHeavy(worldIn, playerIn)
                                        .apply {
                                            shoot(playerIn, 4f, 1f)
                                        })
                    }
                    delay(heavyExtraDelayMs)
                }
            }
            delay(fireDelayMs)
            if (firingTask?.isTerminated == true) {
                firingTask = null
            }
            runningCoroutine = asyncShootMagicBall(worldIn, playerIn, handIn, firingTask == null)
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        worldIn.apply {
            if (isLocal()) {
                if (runningCoroutine?.isActive == true) {
                    runBlocking {
                        runningCoroutine?.cancelAndJoin()
                    }
                }
                runningCoroutineTerminationFlag = false
                runningCoroutine = asyncShootMagicBall(worldIn, playerIn, handIn)
            }
        }
        playerIn.activeHand = handIn
        return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn))
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        if (worldIn.isLocal()) {
            runningCoroutineTerminationFlag = true
        }
    }

    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean {
        entityLiving.world.apply {
            if (isLocal()) {
                spawnEntity(
                        MagicBomb(this, entityLiving as EntityPlayer)
                                .apply {
                                    shoot(entityLiving, 5f, 1f)
                                })
            }
        }
        return false
    }
}
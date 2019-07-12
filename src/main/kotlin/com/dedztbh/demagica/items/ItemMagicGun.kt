package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.global.ServerTickOS
import com.dedztbh.demagica.projectile.MagicBall
import com.dedztbh.demagica.projectile.MagicBallHeavy
import com.dedztbh.demagica.projectile.MagicBallKatyusha
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
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemMagicGun : ItemBow() {

    enum class MagicGunMode(val delayMs: Long) {
        LIGHT(50L),
        HEAVY(100L),
        KATYUSHA(125L),
        MORTAR(250L)
    }

    private val taskManager: TickTaskManager
    private var magicGunMode: MagicGunMode = MagicGunMode.LIGHT

    init {
        setRegistryName("magicgun")
        unlocalizedName = DEMagica.MODID + ".magicgun"

        creativeTab = CreativeTabs.COMBAT
        maxStackSize = 1

        taskManager = ServerTickOS.create(this)
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    private var runningCoroutineTerminationFlag = false
    private var firingTask: TickTaskManager.Task? = null
    private var runningCoroutine: Job? = null
    private fun asyncShootMagicBall(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand, doShoot: Boolean = true): Job {
        return GlobalScope.launch {
            if (runningCoroutineTerminationFlag) {
                runningCoroutine = null
                return@launch
            }
            if (doShoot) {
                firingTask = taskManager.runTask {
                    worldIn.apply {
                        when (magicGunMode) {
                            MagicGunMode.LIGHT -> {
                                spawnEntity(
                                        MagicBall(this, playerIn).apply {
                                            shoot(playerIn, 5f, 1f)
                                        })
                            }
                            MagicGunMode.HEAVY -> {
                                spawnEntity(
                                        MagicBallHeavy(this, playerIn).apply {
                                            shoot(playerIn, 5f, 1f)
                                        })
                            }
                            MagicGunMode.KATYUSHA -> {
                                spawnEntity(
                                        MagicBallKatyusha(this, playerIn).apply {
                                            shoot(playerIn, 4f, 10f)
                                        })
                            }
                            MagicGunMode.MORTAR -> {
                                spawnEntity(
                                        MagicBomb(this, playerIn).apply {
                                            shoot(playerIn, 3f, 1f)
                                        })
                            }
                        }
                    }
                }
                delay(magicGunMode.delayMs)
            }
            if (firingTask?.isTerminated == true) {
                firingTask = null
            }
            runningCoroutine = asyncShootMagicBall(worldIn, playerIn, handIn, firingTask == null)
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        worldIn.apply {
            if (isLocal()) {
                runningCoroutineTerminationFlag = false
                if (runningCoroutine?.isActive == true) {
//                    runBlocking {
//                        runningCoroutine?.cancelAndJoin()
//                    }
                } else {
                    runningCoroutine = asyncShootMagicBall(worldIn, playerIn, handIn)
                }
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
        if (entityLiving.world.isLocal()) {
            magicGunMode = when (magicGunMode) {
                MagicGunMode.LIGHT -> MagicGunMode.HEAVY
                MagicGunMode.HEAVY -> MagicGunMode.KATYUSHA
                MagicGunMode.KATYUSHA -> MagicGunMode.MORTAR
                MagicGunMode.MORTAR -> MagicGunMode.LIGHT
            }
            (entityLiving as? EntityPlayer)?.sendStatusMessage(
                    TextComponentString("Magic Gun Mode: $magicGunMode").apply {
                        style.color = TextFormatting.GREEN
                    }, false)
        }
        return false
    }
}
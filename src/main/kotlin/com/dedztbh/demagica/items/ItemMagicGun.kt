package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.global.ClientTickOS
import com.dedztbh.demagica.global.IDEMagicaItem
import com.dedztbh.demagica.global.ModItems.tabDEMagica
import com.dedztbh.demagica.global.ServerTickOS
import com.dedztbh.demagica.projectile.MagicBall
import com.dedztbh.demagica.projectile.MagicBallHeavy
import com.dedztbh.demagica.projectile.MagicBallKatyusha
import com.dedztbh.demagica.projectile.MagicBomb
import com.dedztbh.demagica.util.DeOS
import com.dedztbh.demagica.util.TickTaskManager
import com.dedztbh.demagica.util.isLocal
import com.dedztbh.demagica.util.then
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

const val MAGIC_GUN_MODE = "MagicGunMode"

class ItemMagicGun : ItemBow(), IDEMagicaItem {

    enum class MagicGunMode(val delayMs: Long, val ammo: Int) {
        LIGHT(0L, 1),
        HEAVY(100L, 2),
        KATYUSHA(200L, 4),
        MORTAR(500L, 8)
    }

    private val taskManager: TickTaskManager = ServerTickOS.create(this)
    private val taskManagerClient: TickTaskManager = ClientTickOS.create(this)
    private val stackShooterOS = DeOS(::StackShooter)
    private fun DeOS<StackShooter>.terminate(itemStack: ItemStack) {
        get(itemStack)?.runningCoroutineTerminationFlag = true
    }

    init {
        setRegistryName("magicgun")
        unlocalizedName = "${DEMagica.MODID}.magicgun"

        maxDamage = 0
        creativeTab = tabDEMagica
        maxStackSize = 1
    }

    inner class StackShooter {
        var runningCoroutineTerminationFlag = false
        var firingTask: TickTaskManager.Task? = null
        var runningCoroutine: Job? = null

        fun asyncShootMagicBall(stack: ItemStack,
                                stackShooter: StackShooter,
                                worldIn: World,
                                playerIn: EntityPlayer,
                                handIn: EnumHand,
                                doShoot: Boolean = true): Job =
                GlobalScope.launch {
                    // Check if item is switched
                    (playerIn.getHeldItem(handIn) !== stack) then {
                        runningCoroutineTerminationFlag = true
                    }

                    stackShooter.run {
                        if (runningCoroutineTerminationFlag) {
                            runningCoroutine = null
                            return@launch
                        }
                        if (doShoot) {
                            val magicGunMode =
                                    MagicGunMode.valueOf(stack.tagCompound!!.getString(MAGIC_GUN_MODE))
                            taskManagerClient.runTask {
                                playerIn.activeHand = handIn
                            }
                            firingTask = taskManager.runTask {
                                //                                playerIn.activeHand = handIn
                                val ammo = findAmmo(playerIn, magicGunMode.ammo)
                                (ammo.count >= magicGunMode.ammo || playerIn.isCreative) then {
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
                                                            shoot(playerIn, 3.5f, 2f)
                                                        })
                                            }
                                        }
                                    }
                                    !playerIn.isCreative then { ammo.shrink(magicGunMode.ammo) }
                                }
                            }
                            delay(magicGunMode.delayMs)
                        }
                        if (firingTask?.isTerminated == true) {
                            firingTask = null
                        }
                        runningCoroutine = asyncShootMagicBall(stack, this, worldIn, playerIn, handIn, firingTask == null)
                    }
                }
    }

    @SideOnly(Side.CLIENT)
    override fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    override fun onUpdate(itemstack: ItemStack, world: World, entity: Entity, metadata: Int, bool: Boolean) {
        if (itemstack.tagCompound == null) {
            itemstack.tagCompound = NBTTagCompound().apply {
                setString(MAGIC_GUN_MODE, MagicGunMode.LIGHT.name)
            }
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        worldIn.isLocal then {
            val stack = playerIn.getHeldItem(handIn)
            stackShooterOS.getOrCreate(stack).run {
                runningCoroutineTerminationFlag = false
                if (runningCoroutine?.isActive != true) {
                    runningCoroutine = asyncShootMagicBall(stack, this@run, worldIn, playerIn, handIn)
                }
            }
        }
        playerIn.activeHand = handIn
        return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn))
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        worldIn.isLocal then {
            stackShooterOS.terminate(stack)
            entityLiving.heldItemOffhand.apply {
                (item is ItemMagicGun) then {
                    stackShooterOS.terminate(stack)
                }
            }
        }
    }

    override fun onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean {
        entityLiving.world.isLocal then {
            val newMagicGunMode = when (MagicGunMode.valueOf(stack.tagCompound!!.getString(MAGIC_GUN_MODE))) {
                MagicGunMode.LIGHT -> MagicGunMode.HEAVY
                MagicGunMode.HEAVY -> MagicGunMode.KATYUSHA
                MagicGunMode.KATYUSHA -> MagicGunMode.MORTAR
                MagicGunMode.MORTAR -> MagicGunMode.LIGHT
            }
            stack.tagCompound?.setString(MAGIC_GUN_MODE, newMagicGunMode.name)
            (entityLiving as? EntityPlayer)?.sendStatusMessage(
                    TextComponentString("Magic Gun Mode: $newMagicGunMode").apply {
                        style.color = TextFormatting.GREEN
                    }, false)
        }
        return false
    }

    override fun onDroppedByPlayer(item: ItemStack, player: EntityPlayer): Boolean {
        stackShooterOS.terminate(item)
        return true
    }

    override fun isArrow(stack: ItemStack): Boolean {
        return stack.item is ItemMagicAmmo
    }

    private fun findAmmo(player: EntityPlayer, amount: Int): ItemStack {
        if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
            player.getHeldItem(EnumHand.OFF_HAND).let {
                if (it.count >= amount) return it
            }
        }
        if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
            player.getHeldItem(EnumHand.MAIN_HAND).let {
                if (it.count >= amount) return it
            }
        }
        for (i in 0 until player.inventory.sizeInventory) {
            val itemstack = player.inventory.getStackInSlot(i)
            if (isArrow(itemstack) && itemstack.count >= amount) {
                return itemstack
            }
        }
        return ItemStack.EMPTY
    }
}
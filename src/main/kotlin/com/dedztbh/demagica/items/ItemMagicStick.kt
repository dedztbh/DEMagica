package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.blocks.BlockMagic
import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.global.IDEMagicaItem
import com.dedztbh.demagica.global.ModItems
import com.dedztbh.demagica.global.ServerTickOS
import com.dedztbh.demagica.util.TickGroup
import com.dedztbh.demagica.util.isLocal
import com.dedztbh.demagica.util.nextPitch
import com.dedztbh.demagica.util.onlyIfNot
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
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
import kotlin.random.Random


class ItemMagicStick : ItemSword(ToolMaterial.GOLD), IDEMagicaItem {
    private val group: TickGroup = ServerTickOS.create(this)

    init {
        setRegistryName("magicstick")
        unlocalizedName = "${DEMagica.MODID}.magicstick"

        creativeTab = ModItems.tabDEMagica

        maxDamage = 64
    }

    @SideOnly(Side.CLIENT)
    override fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (worldIn.isLocal && player.isCreative) {
            val entity = worldIn.getTileEntity(pos)
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.let { fluidCapability ->
                if (entity?.hasCapability(fluidCapability, null) == true) {
                    FluidRegistry.getFluidStack("steam", Int.MAX_VALUE)?.let { steam ->
                        entity.getCapability(fluidCapability, null)?.fill(steam, true)
                                .let {
                                    player.sendStatusMessage(
                                            TextComponentString("Able to fill ${it}mb").apply {
                                                style.color = TextFormatting.GREEN
                                            }, false)
                                }
                    }
                }
            }
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }

    //TODO: Cooldown maybe?
    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        attacker.apply {
            if (world.isLocal) {
                lookVec.apply {
                    val pow = 20
                    target.addVelocity(x * pow, y * pow, z * pow)
                }

                group.runProcess(20, false) {
                    target.apply {
                        world.createExplosion(attacker onlyIfNot Config.explosionDoAffectSelf, posX, posY, posZ, 5f, true)
                        world.playSound(attacker as? EntityPlayer, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 10f, Random.nextPitch())
                    }
                }
            }
        }
        return super.hitEntity(stack, target, attacker)
    }

    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        return repair.run {
            count > 0 && (item as? ItemBlock)?.block is BlockMagic
        }
    }
}
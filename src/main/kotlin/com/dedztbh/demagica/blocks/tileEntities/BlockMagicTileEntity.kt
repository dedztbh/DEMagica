package com.dedztbh.demagica.blocks.tileEntities

import cofh.redstoneflux.api.IEnergyProvider
import cofh.redstoneflux.api.IEnergyReceiver
import cofh.redstoneflux.impl.EnergyStorage
import com.dedztbh.demagica.util.P
import com.dedztbh.demagica.util.isLocal
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

const val BATTERY_RF_CAPACITY = 16000
const val TANK_MB_CAPACITY = 16000
const val MB_CONSUMED = 50
const val RF_GENERATED = 50
const val CONVERT_TICKS = 1

class BlockMagicTileEntity :
        TileEntity(),
        IFluidHandler,
        IEnergyProvider,
        cofh.redstoneflux.api.IEnergyStorage,
        net.minecraftforge.energy.IEnergyStorage,
        ITickable {
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
            when (capability) {
                CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY -> {
                    true
                }
                CapabilityEnergy.ENERGY -> {
                    true
                }
                else -> {
                    super.hasCapability(capability, facing)
                }
            }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
            when (capability) {
                CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, CapabilityEnergy.ENERGY -> {
                    this as T
                }
                else -> {
                    super.getCapability(capability, facing)
                }
            }

    private val steamTank = object : FluidTank(TANK_MB_CAPACITY) {
        override fun canFillFluidType(fluid: FluidStack): Boolean = fluid.fluid.name == "steam"
    }
    private val battery = object : EnergyStorage(BATTERY_RF_CAPACITY) {

    }

    //IEnergyProvider

    override fun getMaxEnergyStored(from: EnumFacing?): Int = battery.maxEnergyStored

    override fun getEnergyStored(from: EnumFacing?): Int = battery.energyStored

    override fun canConnectEnergy(from: EnumFacing?): Boolean = true

    override fun extractEnergy(from: EnumFacing?, maxExtract: Int, simulate: Boolean): Int = battery.extractEnergy(maxExtract, simulate)

    //IEnergyStorage

    override fun getMaxEnergyStored(): Int = battery.maxEnergyStored

    override fun getEnergyStored(): Int = battery.energyStored

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = battery.extractEnergy(maxExtract, simulate)

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = 0

    override fun canExtract(): Boolean = true

    override fun canReceive(): Boolean = false

    // IFluidHandler
    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? = null

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? = null

    override fun fill(resource: FluidStack, doFill: Boolean): Int = steamTank.fill(resource, doFill)

    override fun getTankProperties(): Array<IFluidTankProperties> =
            steamTank.run {
                arrayOf(FluidTankProperties(
                        fluid,
                        capacity,
                        canFill(),
                        canDrain()
                ))
            }

    fun getInfo(): String = "Fluid Amount: ${steamTank.fluidAmount}mB, Energy Amount: ${battery.energyStored}RF"

    var tickCounter = 0
    override fun update() {
        if (world.isLocal()) {

            var dirtyFlag = false

            if (++tickCounter >= CONVERT_TICKS) {
                tickCounter = 0

                if (steamTank.drain(MB_CONSUMED, false)?.amount == MB_CONSUMED
                        && battery.receiveEnergy(RF_GENERATED, true) == RF_GENERATED) {
                    //have enough steam and tank has enough space, can convert
                    steamTank.drain(MB_CONSUMED, true)
                    battery.receiveEnergy(RF_GENERATED, false)

                    dirtyFlag = true
                }
            }

            pos.apply {
                listOf(P(up(), EnumFacing.DOWN),
                        P(down(), EnumFacing.UP),
                        P(east(), EnumFacing.WEST),
                        P(west(), EnumFacing.EAST),
                        P(south(), EnumFacing.NORTH),
                        P(north(), EnumFacing.SOUTH)).let {
                    for ((targetBlockPos, facing) in it) {
                        world.getTileEntity(targetBlockPos).let { targetTE ->
                            if (targetTE is IEnergyReceiver && targetTE.canConnectEnergy(facing)) {
                                targetTE.receiveEnergy(
                                        facing,
                                        battery.extractEnergy(battery.maxExtract, true),
                                        true)
                                        .let { maxRFCanSent ->
                                            if (maxRFCanSent > 0) {
                                                battery.extractEnergy(maxRFCanSent, false)
                                                targetTE.receiveEnergy(facing, maxRFCanSent, false)
                                                dirtyFlag = true
                                            }
                                        }
                            }
                        }
                    }
                }
            }

            if (dirtyFlag) {
                markDirty()
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        steamTank.readFromNBT(nbt)
        battery.readFromNBT(nbt)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        steamTank.writeToNBT(nbt)
        battery.writeToNBT(nbt)
        return nbt
    }
}
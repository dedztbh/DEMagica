package com.dedztbh.demagica.blocks.tileEntities

import cofh.redstoneflux.api.IEnergyProvider
import cofh.redstoneflux.api.IEnergyReceiver
import cofh.redstoneflux.impl.EnergyStorage
import com.dedztbh.demagica.blocks.BlockMagic
import com.dedztbh.demagica.util.TickTaskManager
import com.dedztbh.demagica.util.isLocal
import com.dedztbh.demagica.util.oppositeBlockPosAndEnumFacings
import com.dedztbh.demagica.util.then
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler

const val BATTERY_RF_CAPACITY = 16000
const val TANK_MB_CAPACITY = 16000
const val MB_CONSUMED = 50
const val RF_GENERATED = 50
const val CONVERT_TICKS = 1
const val STORAGE_SIZE = 1

class BlockMagicTileEntity :
        TileEntity(),
        IFluidHandler,
        IEnergyProvider,
        IItemHandlerModifiable,
        cofh.redstoneflux.api.IEnergyStorage,
        net.minecraftforge.energy.IEnergyStorage,
        ITickable {

    companion object {
        val CAPABILITIES = setOf<Capability<*>>(
                CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                CapabilityEnergy.ENERGY,
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
        )
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
            when (capability) {
                in CAPABILITIES -> {
                    true
                }
                else -> {
                    super.hasCapability(capability, facing)
                }
            }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
            when (capability) {
                in CAPABILITIES -> {
                    this as T
                }
                else -> {
                    super.getCapability(capability, facing)
                }
            }

    private val steamTank = object : FluidTank(TANK_MB_CAPACITY) {
        override fun canFillFluidType(fluid: FluidStack): Boolean = fluid.fluid.name == "steam"
    }
    private val battery = EnergyStorage(BATTERY_RF_CAPACITY)
    private val storage = ItemStackHandler(STORAGE_SIZE)

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

    //IFluidHandler

    fun fluidCapacity() = steamTank.capacity
    fun fluidAmount() = steamTank.fluidAmount
    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? = null
    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? = null
    override fun fill(resource: FluidStack, doFill: Boolean): Int = steamTank.fill(resource, doFill).also {
        doFill then {
            lastInputRate = it
            inputRateUpdated = true
        }
    }

    override fun getTankProperties(): Array<IFluidTankProperties> = steamTank.run {
        arrayOf(FluidTankProperties(
                fluid,
                capacity,
                canFill(),
                canDrain()
        ))
    }


    //IItemHandlerModifiable

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = storage.insertItem(slot, stack, simulate)
    override fun getStackInSlot(slot: Int): ItemStack = storage.getStackInSlot(slot)
    override fun getSlotLimit(slot: Int): Int = storage.getSlotLimit(slot)
    override fun getSlots(): Int = storage.slots
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = storage.extractItem(slot, amount, simulate)
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = storage.isItemValid(slot, stack)
    override fun setStackInSlot(slot: Int, stack: ItemStack) = storage.setStackInSlot(slot, stack)

    fun getInfo(): String = "Steam: ${steamTank.fluidAmount}mB, Energy: ${battery.energyStored}RF"

    private var dirtyFlag = false
    private var inputRateUpdated = false

    var lastInputRate = 0
    var lastConvertRate = 0.0
    var lastOutputRate = 0

    private val taskManager = TickTaskManager().apply {
        runTask(CONVERT_TICKS.toLong(), repeat = true, startNow = true, isEvery = true) {
            lastConvertRate = if (steamTank.drain(MB_CONSUMED, false)?.amount == MB_CONSUMED
                    && battery.receiveEnergy(RF_GENERATED, true) == RF_GENERATED) {
                //have enough steam and tank has enough space, can convert
                steamTank.drain(MB_CONSUMED, true)
                battery.receiveEnergy(RF_GENERATED, false)
                dirtyFlag = true
                RF_GENERATED.toDouble() / CONVERT_TICKS

            } else {
                0.0
            }.also {
                if (lastConvertRate == 0.0 && it > 0 || lastConvertRate > 0 && it == 0.0) {
                    BlockMagic.setState(it > 0.0, world, pos)
                    dirtyFlag = true
                }
            }
        }

        runTask(repeat = true, startNow = true) {
            for ((targetBlockPos, facing) in oppositeBlockPosAndEnumFacings()) {
                val targetTE = world.getTileEntity(targetBlockPos)
                if (targetTE is IEnergyReceiver && targetTE.canConnectEnergy(facing)) {
                    targetTE.receiveEnergy(facing, battery.extractEnergy(battery.maxExtract, true), true)
                            .let { maxRFCanSent ->
                                lastOutputRate = maxRFCanSent
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

    override fun update() {
        if (world.isLocal()) {
            taskManager.tick()
            if (!inputRateUpdated) {
                lastInputRate = 0
            }
            inputRateUpdated = false
            if (dirtyFlag) {
                markDirty()
                dirtyFlag = false
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        nbt.apply {
            super.readFromNBT(nbt)
            steamTank.readFromNBT(getCompoundTag("SteamTank"))
            battery.readFromNBT(getCompoundTag("Battery"))
            storage.deserializeNBT(getCompoundTag("Storage"))
            lastConvertRate = getDouble("LastConvertRate")
            lastOutputRate = getInteger("LastOutputRate")
            lastInputRate = getInteger("LastInputRate")
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound =
            nbt.apply {
                super.writeToNBT(nbt)
                setTag("SteamTank", steamTank.writeToNBT(NBTTagCompound()))
                setTag("Battery", battery.writeToNBT(NBTTagCompound()))
                setTag("Storage", storage.serializeNBT())
                setDouble("LastConvertRate", lastConvertRate)
                setInteger("LastOutputRate", lastOutputRate)
                setInteger("LastInputRate", lastInputRate)
            }

    override fun getUpdatePacket(): SPacketUpdateTileEntity =
            SPacketUpdateTileEntity(getPos(), 1, writeToNBT(NBTTagCompound()))

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        super.onDataPacket(net, pkt)
        readFromNBT(pkt.nbtCompound)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block !is BlockMagic || newSate.block !is BlockMagic
    }
}
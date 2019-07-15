package com.dedztbh.demagica.blocks

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.blocks.tileEntities.BlockMagicTileEntity
import com.dedztbh.demagica.global.DEMagicaBlock
import com.dedztbh.demagica.global.ModGuiHandler
import com.dedztbh.demagica.global.ModItems.tabDEMagica
import com.dedztbh.demagica.util.isLocal
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class BlockMagic : Block(Material.ROCK), ITileEntityProvider, DEMagicaBlock {
    init {
        unlocalizedName = "${DEMagica.MODID}.magicblock"
        setRegistryName("magicblock")
        setCreativeTab(tabDEMagica)
        defaultState = blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return BlockMagicTileEntity()
    }

    companion object {
        @JvmField
        val FACING = PropertyDirection.create("facing")

        @JvmField
        val CONVERTING = PropertyBool.create("converting")

        @JvmStatic
        fun getFacingFromEntity(clickedBlock: BlockPos, entity: EntityLivingBase): EnumFacing {
            return EnumFacing.getFacingFromVector(
                    (entity.posX - clickedBlock.x).toFloat(),
                    (entity.posY - clickedBlock.y).toFloat(),
                    (entity.posZ - clickedBlock.z).toFloat())
        }

        @JvmStatic
        fun setState(converting: Boolean, worldIn: World, pos: BlockPos) {
            worldIn.getBlockState(pos).apply {
                worldIn.setBlockState(pos, withProperty(CONVERTING, converting), 3)
            }
        }
    }

//    @SideOnly(Side.CLIENT)
//    override fun getBlockLayer(): BlockRenderLayer {
//        return BlockRenderLayer.SOLID
//    }

    override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        world.setBlockState(pos, state
                .withProperty(FACING, getFacingFromEntity(pos, placer)), 2)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState
                .withProperty(FACING, EnumFacing.getFront(meta and 7)) // 3 bit
                .withProperty(CONVERTING, (meta and 8) != 0)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).index + // 3 bit
                (if (state.getValue(CONVERTING)) 8 else 0) // 1 bit
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, CONVERTING)
    }

    fun World.TEAt(pos: BlockPos): BlockMagicTileEntity {
        return getTileEntity(pos) as BlockMagicTileEntity
    }

    override fun onBlockActivated(worldIn: World,
                                  pos: BlockPos,
                                  state: IBlockState,
                                  playerIn: EntityPlayer,
                                  hand: EnumHand,
                                  side: EnumFacing,
                                  hitX: Float,
                                  hitY: Float,
                                  hitZ: Float): Boolean {
        if (worldIn.isLocal) {
            playerIn.apply {
                if (isSneaking) {
                    if (side == state.getValue(FACING)) {
                        TextComponentString(worldIn.TEAt(pos).getInfo()).apply {
                            style.color = TextFormatting.GREEN
                            sendStatusMessage(this, false)
                        }
                    }
                } else {
                    pos.run {
                        openGui(DEMagica.instance, ModGuiHandler.MAGIC, world, x, y, z)
                    }
                }
            }
        }
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
    }

    // TODO: Change according to TE lastConvertRate.
    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        return if (state.getValue(CONVERTING)) 15 else 0
    }

    override fun getTEClass(): Class<out TileEntity> {
        return BlockMagicTileEntity::class.java
    }
}
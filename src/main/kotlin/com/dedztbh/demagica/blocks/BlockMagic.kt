package com.dedztbh.demagica.blocks

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.blocks.tileEntities.BlockMagicTileEntity
import com.dedztbh.demagica.util.isLocal
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class BlockMagic : Block(Material.ROCK), ITileEntityProvider {
    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return BlockMagicTileEntity()
    }

    companion object {
        @JvmStatic
        val FACING = PropertyDirection.create("facing")

        @JvmStatic
        fun getFacingFromEntity(clickedBlock: BlockPos, entity: EntityLivingBase): EnumFacing {
            return EnumFacing.getFacingFromVector(
                    (entity.posX - clickedBlock.x).toFloat(),
                    (entity.posY - clickedBlock.y).toFloat(),
                    (entity.posZ - clickedBlock.z).toFloat())
        }
    }

    init {
        unlocalizedName = "${DEMagica.MODID}.magic"
        setRegistryName("magic")
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
        defaultState = blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.SOLID
    }

    override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(pos, placer)), 2)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(FACING, EnumFacing.getFront(meta and 7))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).index
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    private fun getTE(world: World, pos: BlockPos): BlockMagicTileEntity {
        return world.getTileEntity(pos) as BlockMagicTileEntity
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
        if (worldIn.isLocal()) {
            if (side == state.getValue(FACING)) {
                val component = TextComponentString(getTE(worldIn, pos).getInfo()).apply {
                    style.color = TextFormatting.GREEN
                }
                playerIn.sendStatusMessage(component, false)
            }

            object : GuiScreen() {
                override fun doesGuiPauseGame(): Boolean {
                    return false
                }

                override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
                    fontRenderer.drawString(getTE(worldIn, pos).getInfo(), 100, 100, 0xFFFFFF)
                    super.drawScreen(mouseX, mouseY, partialTicks)
                }
            }.run {
                Minecraft.getMinecraft().apply {
                    addScheduledTask {
                        displayGuiScreen(this@run)
                    }
                }
            }
        }
        return true
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
    }
}
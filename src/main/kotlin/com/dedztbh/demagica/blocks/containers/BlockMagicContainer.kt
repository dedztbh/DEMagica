package com.dedztbh.demagica.blocks.containers

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.blocks.tileEntities.BlockMagicTileEntity
import com.dedztbh.demagica.global.ModBlocks
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler


/**
 * Created by DEDZTBH on 19-7-14.
 * Project DEMagica
 */
class BlockMagicContainer(playerInv: InventoryPlayer, val magicTE: BlockMagicTileEntity) : Container() {
    init {
        val inventory = magicTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)
        addSlotToContainer(object : SlotItemHandler(inventory, 0, 80, 35) {
            override fun onSlotChanged() {
                magicTE.markDirty()
            }
        })
        for (i in 0..2) {
            for (j in 0..8) {
                addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }
        for (k in 0..8) {
            addSlotToContainer(Slot(playerInv, k, 8 + k * 18, 142))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

    override fun transferStackInSlot(player: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            val containerSlots = inventorySlots.size - player.inventory.mainInventory.size

            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.count == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(player, itemstack1)
        }

        return itemstack
    }
}

class BlockMagicGui(private val container: Container, private val playerInv: InventoryPlayer) : GuiContainer(container) {
    companion object {
        private val BG_TEXTURE = ResourceLocation(DEMagica.MODID, "textures/gui/pedestal.png")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
//        val name = I18n.format(ModBlocks.blockMagic.unlocalizedName + ".name")
        val name = (container as BlockMagicContainer).magicTE.getInfo()
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040)
        fontRenderer.drawString(playerInv.displayName.unformattedText, 8, ySize - 94, 0x404040)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(BG_TEXTURE)
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize)
    }

}
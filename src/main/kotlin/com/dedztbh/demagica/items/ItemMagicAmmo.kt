package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.global.IDEMagicaItem
import com.dedztbh.demagica.global.ModItems
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ItemMagicAmmo : Item(), IDEMagicaItem {
    init {
        setRegistryName("magicammo")
        unlocalizedName = "${DEMagica.MODID}.magicammo"

        creativeTab = ModItems.tabDEMagica
    }

    @SideOnly(Side.CLIENT)
    override fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }
}
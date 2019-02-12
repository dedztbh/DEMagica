package com.dedztbh.demagica.items

import com.dedztbh.demagica.DEMagica
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ItemMagicStick : Item() {
    init {
        setRegistryName("magicstick")
        unlocalizedName = DEMagica.MODID + ".magicstick"
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }
}
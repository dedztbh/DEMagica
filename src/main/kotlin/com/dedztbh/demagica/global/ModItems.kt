package com.dedztbh.demagica.global

import com.dedztbh.demagica.items.ItemMagicGun
import com.dedztbh.demagica.items.ItemMagicStick
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly



class ModItems {
    companion object {
        @JvmStatic
        @GameRegistry.ObjectHolder("demagica:magicstick")
        lateinit var itemMagicStick: ItemMagicStick

        @JvmStatic
        @GameRegistry.ObjectHolder("demagica:magicgun")
        lateinit var itemMagicGun: ItemMagicGun

        @SideOnly(Side.CLIENT)
        fun initModels() {
            itemMagicStick.initModel()
            itemMagicGun.initModel()
        }

        val tabTutorialMod: CreativeTabs = object : CreativeTabs("tabDEMagica") {
            override fun getTabIconItem(): ItemStack {
                return ItemStack(itemMagicGun)
            }
        }
    }
}
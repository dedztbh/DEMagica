package com.dedztbh.demagica.global

import com.dedztbh.demagica.items.ItemMagicAmmo
import com.dedztbh.demagica.items.ItemMagicGun
import com.dedztbh.demagica.items.ItemMagicStick
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry


object ModItems : DEMagicaModStuff() {
    @JvmStatic
    @GameRegistry.ObjectHolder("demagica:magicstick")
    lateinit var itemMagicStick: ItemMagicStick

    @JvmStatic
    @GameRegistry.ObjectHolder("demagica:magicgun")
    lateinit var itemMagicGun: ItemMagicGun

    @JvmStatic
    @GameRegistry.ObjectHolder("demagica:magicammo")
    lateinit var itemMagicAmmo: ItemMagicAmmo

    @JvmField
    val tabDEMagica: CreativeTabs = object : CreativeTabs("tabDEMagica") {
        override fun getTabIconItem(): ItemStack {
            return ItemStack(itemMagicGun)
        }
    }

}
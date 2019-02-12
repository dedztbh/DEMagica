package com.dedztbh.demagica.global

import com.dedztbh.demagica.items.ItemMagicStick
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.thread.SidedThreadGroups.CLIENT
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly



class ModItems {
    companion object {
        @JvmStatic
        @GameRegistry.ObjectHolder("demagica:magicstick")
        lateinit var itemMagicStick: ItemMagicStick

        @SideOnly(Side.CLIENT)
        fun initModels() {
            itemMagicStick.initModel()
        }
    }
}
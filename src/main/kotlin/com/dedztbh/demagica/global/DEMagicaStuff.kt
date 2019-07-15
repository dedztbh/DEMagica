package com.dedztbh.demagica.global

import com.dedztbh.demagica.util.Open
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by DEDZTBH on 2019-07-15.
 * Project DEMagica
 */

interface DEMagicaStuff {
    @SideOnly(Side.CLIENT)
    fun initModel()
}

interface DEMagicaBlock : DEMagicaStuff {
    fun getTEClass(): Class<out TileEntity>
}

@Open
class DEMagicaModStuff {

    @Suppress("UNCHECKED_CAST")
    fun <T> stuffOf(clazz: Class<T>, vararg otherClazz: Class<out Any>, newInstance: Boolean = false): List<T> =
            this::class.java.declaredMethods.filter { getter ->
                setOf(clazz, *otherClazz).all {
                    it.isAssignableFrom(getter.returnType)
                }
            }.map {
                (if (newInstance)
                    it.returnType.newInstance()
                else
                    it.invoke(this)) as T
            }

    @SideOnly(Side.CLIENT)
    fun initModels() {
        this.stuffOf(DEMagicaStuff::class.java).forEach {
            it.initModel()
        }
    }
}
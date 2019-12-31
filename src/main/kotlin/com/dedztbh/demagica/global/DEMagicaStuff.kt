package com.dedztbh.demagica.global

import com.dedztbh.demagica.util.Open
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.reflect.KClass

/**
 * Created by DEDZTBH on 2019-07-15.
 * Project DEMagica
 */

interface IDEMagicaStuff {
    @SideOnly(Side.CLIENT)
    fun initModel()
}

interface IDEMagicaBlock : IDEMagicaStuff {
    fun getTEClass(): Class<out TileEntity>
}

typealias IDEMagicaItem = IDEMagicaStuff

@Open
class DEMagicaModStuff {
    @Suppress("UNCHECKED_CAST")
    fun <T> stuffOf(clazz: Class<T>, vararg otherClazz: Class<out Any>, newInstance: Boolean = false): List<T> =
            setOf(clazz, *otherClazz).let { allClazz ->
                this::class.java.declaredFields.filter { field ->
                    allClazz.all { it.isAssignableFrom(field.type) }
                }.map {
                    (if (newInstance)
                        it.type.newInstance()
                    else
                        it.get(this)) as T
                }
            }

    fun <T : Any> stuffOf(clazz: KClass<out T>, vararg otherClazz: KClass<out Any>, newInstance: Boolean = false): List<T> =
            stuffOf(clazz.java, *otherClazz.map { it.java }.toTypedArray(), newInstance = newInstance)


    @SideOnly(Side.CLIENT)
    fun initModels() {
        this.stuffOf(IDEMagicaStuff::class).forEach {
            it.initModel()
        }
    }
}
package com.dedztbh.demagica.util

import java.util.*

/**
 * Created by DEDZTBH on 19-7-13.
 * Project DEMagica
 */
@Open
class DeOS<T>(private val constructObject: () -> T) {
    val groupMap = WeakHashMap<Any, T>()

    fun create(objRef: Any): T = constructObject().also {
        groupMap[objRef] = it
    }

    fun get(objRef: Any, createIfNotExist: Boolean = false): T? =
            groupMap[objRef]
                    ?: if (createIfNotExist)
                        create(objRef)
                    else
                        null

    fun getOrCreate(objRef: Any): T = get(objRef, true)!!

    fun destroy(objRef: Any): T? =
            groupMap.remove(objRef)
}
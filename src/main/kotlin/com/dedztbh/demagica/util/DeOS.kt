package com.dedztbh.demagica.util

import java.util.*

/**
 * Created by DEDZTBH on 19-7-13.
 * Project DEMagica
 */
open class DeOS<T>(private val constructObject: () -> T) {
    constructor(clazz: Class<T>) : this({ clazz.newInstance() })

    val processMap = WeakHashMap<Any, T>()

    fun create(objRef: Any): T = constructObject().also {
        processMap[objRef] = it
    }

    fun get(objRef: Any, createIfNotExist: Boolean = false): T? =
            processMap[objRef]
                    ?: if (createIfNotExist)
                        create(objRef)
                    else
                        null

    fun getOrCreate(objRef: Any): T = get(objRef, true)!!

    fun destroy(objRef: Any): T? =
            processMap.remove(objRef)
}
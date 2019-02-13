package com.dedztbh.demagica.util

import net.minecraft.world.World

/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

typealias P<T, R> = Pair<T, R>

fun World.isLocal() = !isRemote
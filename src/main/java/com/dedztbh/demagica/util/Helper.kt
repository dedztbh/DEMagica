package com.dedztbh.demagica.util

import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.random.Random

/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

typealias P<T, R> = Pair<T, R>

fun World.isLocal() = !isRemote

fun Random.nextVec3d(maxSpread: Double): Vec3d =
        if (maxSpread > 0.0) {
            (maxSpread / 2).let { halfSpread ->
                Vec3d(
                        (nextDouble(maxSpread) - halfSpread),
                        (nextDouble(maxSpread) - halfSpread),
                        (nextDouble(maxSpread) - halfSpread)
                )
            }
        } else {
            Vec3d(0.0, 0.0, 0.0)
        }

fun Random.nextPitch(): Float =
        0.4f / (nextFloat() * 0.4f + 0.8f)
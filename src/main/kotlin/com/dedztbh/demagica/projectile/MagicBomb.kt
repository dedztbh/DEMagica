package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.util.isLocal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

class MagicBomb : MagicBall {
//    override val VELOCITY = 2.0

    constructor(worldIn: World) : super(worldIn)

    constructor(worldIn: World, player: EntityPlayer) : super(worldIn, player)

    override var gravity: Double = 0.1

    override fun onHit(raytraceResultIn: RayTraceResult) {
        if (world.isLocal()) {
            world.newExplosion(this, posX, posY, posZ, 5f, true, true)
            setDead()
        }
    }
}
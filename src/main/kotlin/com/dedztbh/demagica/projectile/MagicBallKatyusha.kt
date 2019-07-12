package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.util.isLocal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World


/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

open class MagicBallKatyusha : MagicBall {

    constructor(worldIn: World) : super(worldIn)

    constructor(worldIn: World, player: EntityPlayer) : super(worldIn, player)

    override var gravity: Double = 0.06

    override fun onHit(raytraceResultIn: RayTraceResult) {
        if (world.isLocal()) {
            world.newExplosion(this, posX, posY, posZ, 3f, true, true)
            setDead()
        }
    }

    override fun isSilent(): Boolean {
        return true
    }
}
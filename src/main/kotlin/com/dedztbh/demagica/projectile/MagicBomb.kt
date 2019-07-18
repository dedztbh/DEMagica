package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.util.isLocal
import com.dedztbh.demagica.util.onlyIfNot
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

    override var gravity: Double = 0.08

    override fun onHit(raytraceResultIn: RayTraceResult) {
        super.onHit(raytraceResultIn)
        if (world.isLocal) {
            world.newExplosion(thrower onlyIfNot Config.explosionDoAffectSelf, posX, posY, posZ, 6f, true, true)
            setDead()
        }
    }
}
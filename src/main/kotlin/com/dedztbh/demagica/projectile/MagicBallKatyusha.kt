package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.global.Config
import com.dedztbh.demagica.util.isLocal
import com.dedztbh.demagica.util.onlyIfNot
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.SoundEvents
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World


/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

open class MagicBallKatyusha : MagicBall {

    constructor(worldIn: World) : super(worldIn)

    constructor(worldIn: World, shooter: EntityLivingBase) : super(worldIn, shooter)

    override val gravity: Double = 0.04
    override val sound: SoundEvent = SoundEvents.ENTITY_FIREWORK_LAUNCH

    override fun onHit(raytraceResultIn: RayTraceResult) {
        if (world.isLocal) {
            world.newExplosion(thrower onlyIfNot Config.explosionDoAffectSelf, posX, posY, posZ, 3f, true, true)
            setDead()
        }
    }
}
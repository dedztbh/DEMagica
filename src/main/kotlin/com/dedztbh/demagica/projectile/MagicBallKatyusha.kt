package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.util.isLocal
import net.minecraft.entity.player.EntityPlayer
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

    constructor(worldIn: World, player: EntityPlayer) : super(worldIn, player)

    override val gravity: Double = 0.04
    override val sound: SoundEvent = SoundEvents.ENTITY_FIREWORK_LAUNCH

    override fun onHit(raytraceResultIn: RayTraceResult) {
        super.onHit(raytraceResultIn)
        if (world.isLocal()) {
            world.newExplosion(this, posX, posY, posZ, 3f, true, true)
            setDead()
        }
    }

    override fun isSilent(): Boolean {
        return true
    }
}
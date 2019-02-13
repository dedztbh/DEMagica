package com.dedztbh.demagica.projectile

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */
class MagicBall : EntityThrowable {

    constructor(worldIn: World) : super(worldIn)

    constructor(worldIn: World, throwerIn: EntityLivingBase) : super(worldIn, throwerIn)

    constructor(worldIn: World, x: Double, y: Double, z: Double) : super(worldIn, x, y, z)

    var explosivePower = 2f
    var vMultiplier = 20.0

    init {
        setVelocityToMultipleOfVecLook(vMultiplier)
    }

    fun setVelocityToMultipleOfVecLook(vMultiplier: Double) {
        thrower.lookVec.apply {
            setVelocity(x * vMultiplier, y * vMultiplier, z * vMultiplier)
        }
    }

    override fun onImpact(result: RayTraceResult) {
        entityWorld.createExplosion(this, posX, posY, posZ, explosivePower, true)
        setDead()
    }

    override fun getGravityVelocity(): Float {
        return 0.005F
    }

}
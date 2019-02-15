package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.util.isLocal
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.SoundEvents
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.IThrowableEntity
import kotlin.random.Random


/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

open class MagicBall : EntityThrowable, IThrowableEntity {

    override fun shoot(entityThrower: Entity, rotationPitchIn: Float, rotationYawIn: Float, pitchOffset: Float, velocity: Float, inaccuracy: Float) {
        super.shoot(entityThrower, rotationPitchIn, rotationYawIn, pitchOffset, velocity, inaccuracy)
//        entityThrower.lookVec.also { v ->
//            posX += v.x * 2
//            posY += v.y * 2
//            posZ += v.z * 2
//        }
    }

    //    open val VELOCITY = 10.0
    open val GRAVITY = 0.05f
//    open val Position_offsetMultiplier = 2.0
//    open val Position_maxSpread = 0.0
//    open val Velocity_maxSpread = 0.0

    constructor(worldIn: World) : super(worldIn)

    constructor(worldIn: World, player: EntityPlayer) : super(worldIn, player)

    constructor(worldIn: World, x: Double, y: Double, z: Double) : super(worldIn, x, y, z)

    //    var velocity = VELOCITY
    var gravity = GRAVITY

    fun playSound(thrower: EntityPlayer) {
        world.playSound(thrower, posX, posY, posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 0.5f, 0.4f / (Random.nextFloat() * 0.4f + 0.8f))
    }

    open fun onImpactTask(result: RayTraceResult) {
        if (world.isLocal()) {
            result.entityHit?.apply {
                attackEntityFrom(DamageSource.GENERIC, 10f)
            }
        }
    }

    override fun onImpact(result: RayTraceResult) {
        onImpactTask(result)
        setDead()
    }

    override fun getGravityVelocity(): Float = gravity

    override fun setThrower(entity: Entity) {
        thrower = entity as EntityLivingBase
        playSound(thrower as EntityPlayer)
    }
}
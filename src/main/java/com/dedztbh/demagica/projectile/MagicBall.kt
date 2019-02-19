package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.util.isLocal
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
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

open class MagicBall : EntityArrow, IThrowableEntity {

    constructor (worldIn: World) : super(worldIn)

    constructor(worldIn: World, player: EntityPlayer) : super(worldIn, player)

    init {
        damage = 10.0
    }

    open var gravity = 0.02

    override fun onUpdate() {
        super.onUpdate()
        motionY -= (gravity - 0.05000000074505806)
    }

    fun playShootingSound() {
        world.playSound(thrower as EntityPlayer?, posX, posY, posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 16f, Random.nextFloat())
    }

    fun shoot(shooter: Entity, velocity: Float = 5f, inaccuracy: Float = 1f) =
            shooter.lookVec.run {
                shoot(x, y, z, velocity, inaccuracy)
            }


    override fun onHit(raytraceResultIn: RayTraceResult) {
//        super.onHit(raytraceResultIn)
        if (world.isLocal()) {
            raytraceResultIn.entityHit?.apply {
                attackEntityFrom(DamageSource.causeArrowDamage(this@MagicBall, thrower), damage.toFloat())
            }
            setDead()
        }
    }

    override fun getArrowStack(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun setThrower(entity: Entity?) {
        shootingEntity = entity
        if (shootingEntity != null && !isDead && getDistance(shootingEntity) <= 5) {
            playShootingSound()
        }
    }

    override fun getThrower(): Entity? {
        return shootingEntity
    }
}
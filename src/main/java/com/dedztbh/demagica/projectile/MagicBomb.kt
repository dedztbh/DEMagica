package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.util.isLocal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */

class MagicBomb : MagicBall {

//    override val VELOCITY = 2.0

    constructor(worldIn: World) : super(worldIn) {
//        println(worldIn.isRemote)
    }

    constructor(worldIn: World, player: EntityPlayer) : super(worldIn, player) {
//        println(worldIn.isRemote)
    }

    override fun onImpactTask(result: RayTraceResult) {
        if (world.isLocal()) {
            world.newExplosion(this, posX, posY, posZ, 5f, true, true)
            world.playSound(thrower as EntityPlayer?, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 3f, 0.5f)
        }
    }
}
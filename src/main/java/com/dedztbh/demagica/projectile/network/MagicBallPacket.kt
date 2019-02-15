package com.dedztbh.demagica.projectile.network

import com.dedztbh.demagica.projectile.MagicBall
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

/**
 * Created by DEDZTBH on 19-2-14.
 * Project DEMagica
 */

//TODO: I don't know
class MagicBallPacket() : IMessage {

    lateinit var magicBall: MagicBall

    constructor(magicBall: MagicBall) : this() {
        this.magicBall = magicBall
    }

    override fun fromBytes(buf: ByteBuf) {
//        buf.writeBytes(magicBall.inpu)
    }

    override fun toBytes(buf: ByteBuf) {

    }
}
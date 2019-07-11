package com.dedztbh.demagica.util

import com.dedztbh.demagica.DEMagica
import net.minecraftforge.fml.common.network.NetworkRegistry


/**
 * Created by DEDZTBH on 19-2-14.
 * Project DEMagica
 */
class DEMagicaPacketHandler {
    companion object {
        @JvmField
        val INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DEMagica.MODID)
    }
}
package com.dedztbh.demagica.keybinding

import com.dedztbh.demagica.proxy.ClientProxy
import com.dedztbh.demagica.util.then
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * Created by DEDZTBH on 19-7-15.
 * Project DEMagica
 */

@Mod.EventBusSubscriber
object KeyListener {

    @JvmStatic
    var zoom = 1

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    @JvmStatic
    fun onKeyInput(event: KeyInputEvent) = ClientProxy.keyBindings.let {
        // DEBUG
//        println("Key Input Event")

        // check each enumerated key binding type for pressed and take appropriate action

        it[0].isPressed then {
            // DEBUG
//                println("Key binding = " + it[0].keyDescription)

            // do stuff for this key binding here
            // remember you may need to send packet to server
            zoom = when (zoom) {
                1 -> 4
                4 -> 10
                else -> 1
            }
        }

        it[1].isPressed then {
            Minecraft.getMinecraft().player.apply {
                isCreative then {
                    setPosition(posX, 400.0, posZ)
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun onFOVUpdate(event: FOVUpdateEvent) {
        event.newfov = 1f / zoom
    }
}
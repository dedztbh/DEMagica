package com.dedztbh.demagica.keybinding

import com.dedztbh.demagica.proxy.ClientProxy
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.FOVUpdateEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * Created by DEDZTBH on 19-7-15.
 * Project DEMagica
 */

@SideOnly(Side.CLIENT)
@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
fun onEvent(event: KeyInputEvent) {
    // DEBUG
//    println("Key Input Event")

    // make local copy of key binding array
    val keyBindings = ClientProxy.keyBindings

    // check each enumerated key binding type for pressed and take appropriate action
    if (keyBindings[0].isPressed) {
        // DEBUG
//        System.out.println("Key binding =" + keyBindings[0].keyDescription)

        // do stuff for this key binding here
        // remember you may need to send packet to server

        MinecraftForge.EVENT_BUS.post(FOVUpdateEvent(Minecraft.getMinecraft().player, 10f))
    }
}
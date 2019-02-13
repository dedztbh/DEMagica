package com.dedztbh.demagica.util

import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.roundToLong


/**
 * Created by DEDZTBH on 19-2-13.
 * Project DEMagica
 */
class TickTaskManager private constructor() {

    companion object {
        init {
            EVENT_BUS.register(TickTaskManager::class.java)
        }

        @SubscribeEvent
        @JvmStatic
        fun tick(event: TickEvent.ServerTickEvent) {
            tickTaskManagersMap.forEach { _, tickTaskManager ->
                mutableListOf<DelayedTask>().let { markForDeletion ->
                    tickTaskManager.tasks.apply {
                        //Tick DelayedTasks
                        for (delayedTask in this) {
                            delayedTask.apply {
                                if (terminationFlag) {
                                    markForDeletion.add(this)
                                } else {
                                    when {
                                        ticksRemaining > 0L -> {
                                            ticksRemaining--
                                        }
                                        ticksRemaining == 0L -> {
                                            task()
                                            if (repeat) {
                                                ticksRemaining = timerTicks
                                            } else {
                                                markForDeletion.add(this)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //Remove finished DelayedTasks
                        markForDeletion.forEach {
                            it.removedFlag = true
                        }
                        removeAll(markForDeletion)
                    }
                }
            }
        }

        val tickTaskManagersMap: MutableMap<Any, TickTaskManager> = mutableMapOf()

        @JvmStatic
        fun create(objRef: Any) = TickTaskManager().also {
            tickTaskManagersMap[objRef] = it
        }

        @JvmStatic
        fun get(objRef: Any, createIfNotExist: Boolean = false): TickTaskManager? =
                tickTaskManagersMap[objRef]
                        ?: if (createIfNotExist)
                            create(objRef)
                        else
                            null

        @JvmStatic
        fun destroy(objRef: Any): TickTaskManager? =
                tickTaskManagersMap.remove(objRef)
    }

    private val tasks: MutableList<DelayedTask> = mutableListOf()

    fun runDelayedTask(secondsDelay: Double, repeat: Boolean = false, task: () -> Unit) =
            DelayedTask((secondsDelay * 20).roundToLong(), task, repeat).also {
                tasks.add(it)
            }

    fun terminateDelayedTask(delayedTask: DelayedTask) {
        delayedTask.terminationFlag = true
    }

    fun runSync(task: () -> Unit) =
            DelayedTask(0, task).also { tasks.add(it) }

    class DelayedTask(
            var ticksRemaining: Long,
            val task: () -> Unit,
            val repeat: Boolean = false
    ) {
        val timerTicks = ticksRemaining

        var terminationFlag = false

        var removedFlag = false

        init {
            if (ticksRemaining < 0) {
                throw IllegalArgumentException("ticksRemaining cannot be negative")
            }
        }
    }
}
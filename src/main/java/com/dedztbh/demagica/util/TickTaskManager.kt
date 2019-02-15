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
                mutableListOf<Task>().let { markForDeletion ->
                    //Tick DelayedTasks
                    for (delayedTask in tickTaskManager.tasks) {
                        delayedTask.apply {
                            when {
                                ticksLeft < 0L -> {
                                    //Terminate now!
                                    markForDeletion.add(this)
                                }
                                ticksLeft > 0L -> {
                                    //Waiting
                                    ticksLeft--
                                }
                                ticksLeft == 0L -> {
                                    //Execute and terminate/repeat
                                    task()
                                    if (repeat) {
                                        ticksLeft = timerTicks
                                    } else {
                                        markForDeletion.add(this)
                                    }
                                }
                            }
                        }
                    }

                    //Remove finished DelayedTasks
                    markForDeletion.forEach {
                        it.terminationCallback()
                        it.isTerminated = true
                    }
                    tickTaskManager.tasks.removeAll(markForDeletion)

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

    private val tasks: MutableList<Task> = mutableListOf()

    fun runTask(secondsDelay: Double,
                repeat: Boolean = false,
                startImmediately: Boolean = false,
                task: () -> Unit) =
            Task(
                    ticksLeft = (secondsDelay * 20).roundToLong(),
                    task = task,
                    repeat = repeat,
                    startImmediately = startImmediately
            ).also {
                tasks.add(it)
                it.taskManager = this
            }

    fun terminateTask(task: Task, terminationCallback: (() -> Unit)? = null) =
            task.apply {
                if (terminationCallback != null) {
                    this.terminationCallback = terminationCallback
                }
                ticksLeft = -1
            }

    fun runSync(task: () -> Unit) =
            runTask(0.0, task = task)

    inner class Task(
            var ticksLeft: Long,
            val task: () -> Unit,
            val repeat: Boolean = false,
            startImmediately: Boolean = false
    ) {

        var terminationCallback: () -> Unit = {}

        val timerTicks = ticksLeft

        var isTerminated = false

        lateinit var taskManager: TickTaskManager

        init {
            if (startImmediately) {
                ticksLeft = 0
            }
        }

        fun isAboutToTerminate() = ticksLeft < 0

        fun terminate() = taskManager.terminateTask(this)
    }
}
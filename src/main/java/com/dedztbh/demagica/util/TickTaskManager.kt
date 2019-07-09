package com.dedztbh.demagica.util

import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.lang.ref.WeakReference
import java.util.*
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
            tickTaskManagersMap.forEach { (_, tickTaskManager) ->
                val terminatedTasks = mutableListOf<Task>()
                tickTaskManager.tasks.addAll(tickTaskManager.tasksToBeAdd)
                // ignoring newly created task into the old list
                tickTaskManager.tasksToBeAdd = mutableListOf()

                //Tick DelayedTasks
                for (delayedTask in tickTaskManager.tasks) {
                    delayedTask.apply {
                        when (runningState()) {
                            Task.State.WILL_TERMINATE -> {
                                //Terminate now!
                                terminatedTasks.add(this)
                            }
                            Task.State.WILL_EXECUTE_LATER -> {
                                //Waiting
                                ticksLeft--
                            }
                            Task.State.WILL_EXECUTE -> {
                                //Execute and terminate/repeat
                                task()
                                if (repeat) {
                                    ticksLeft = timerTicks
                                } else {
                                    terminatedTasks.add(this)
                                }
                            }
                            else -> {
                                // Should never happen
                            }
                        }
                    }
                }

                //Remove finished Tasks
                terminatedTasks.forEach {
                    it.onTerminate()
                    it.isTerminated = true
                    tickTaskManager.tasks.remove(it)
                }
            }
        }

        val tickTaskManagersMap = WeakHashMap<Any, TickTaskManager>()

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

    private val tasks = mutableListOf<Task>()

    private var tasksToBeAdd = mutableListOf<Task>()

    fun runTask(secondsDelay: Double,
                repeat: Boolean = false,
                startImmediately: Boolean = false,
                task: () -> Unit) =
            Task(
                    ticksLeft = (secondsDelay * 20).roundToLong(),
                    task = task,
                    repeat = repeat,
                    startImmediately = startImmediately,
                    taskManager = this
            ).also {
                tasksToBeAdd.add(it)
            }

    fun terminateTask(task: Task, onTerminate: (() -> Unit)? = null) =
            task.apply {
                if (onTerminate != null) {
                    this.onTerminate = onTerminate
                }
                ticksLeft = -1
            }

    fun runSync(task: () -> Unit) =
            runTask(0.0, task = task)

    class Task(
            var ticksLeft: Long,
            taskManager: TickTaskManager,
            val repeat: Boolean = false,
            startImmediately: Boolean = false,
            val task: () -> Unit
    ) {
        private val taskManager: WeakReference<out TickTaskManager>

        init {
            if (startImmediately) {
                ticksLeft = 0
            }
            this.taskManager = WeakReference(taskManager)
        }

        val timerTicks = ticksLeft

        var isTerminated = false

        var onTerminate: () -> Unit = {}

        fun terminate() = taskManager.get()?.terminateTask(this)

        fun runningState() =
                if (isTerminated) {
                    State.TERMINATED
                } else {
                    when {
                        ticksLeft < 0L -> State.WILL_TERMINATE
                        ticksLeft == 0L -> State.WILL_EXECUTE
                        else -> State.WILL_EXECUTE_LATER
                    }
                }

        enum class State {
            WILL_TERMINATE,
            WILL_EXECUTE,
            WILL_EXECUTE_LATER,
            TERMINATED
        }
    }
}
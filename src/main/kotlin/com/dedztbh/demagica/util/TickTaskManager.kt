package com.dedztbh.demagica.util

import java.util.*


/**
 * Created by DEDZTBH on 19-2-13.
 * Project DEMagica
 */


class TickTaskManager private constructor() {

    class OS {
        private val tickTaskManagersMap = WeakHashMap<Any, TickTaskManager>()

        fun tick() = tickTaskManagersMap.forEach { (key, tickTaskManager) ->
            if (key != null) {
                val terminatedTasks = mutableListOf<Task>()
                tickTaskManager.tasks.addAll(tickTaskManager.tasksToBeAdd)
                // ignoring newly created task into the old list
                tickTaskManager.tasksToBeAdd = mutableListOf()

                //Tick DelayedTasks
                for (tasks in tickTaskManager.tasks) {
                    tasks.apply {
                        when (runningState()) {
                            TaskState.WILL_TERMINATE -> {
                                //Terminate now!
                                terminatedTasks.add(this)
                            }
                            TaskState.WILL_EXECUTE_LATER -> {
                                //Waiting
                                ticksLeft--
                            }
                            TaskState.WILL_EXECUTE -> {
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

        fun create(objRef: Any) = TickTaskManager().also {
            tickTaskManagersMap[objRef] = it
        }

        fun get(objRef: Any, createIfNotExist: Boolean = false): TickTaskManager? =
                tickTaskManagersMap[objRef]
                        ?: if (createIfNotExist)
                            create(objRef)
                        else
                            null

        fun destroy(objRef: Any): TickTaskManager? =
                tickTaskManagersMap.remove(objRef)
    }

    companion object {
        private val companionOS = OS()

        @JvmStatic
        fun tick() = companionOS.tick()

        @JvmStatic
        fun create(objRef: Any) = companionOS.create(objRef)

        @JvmStatic
        fun get(objRef: Any, createIfNotExist: Boolean = false) = companionOS.get(objRef, createIfNotExist)

        @JvmStatic
        fun destroy(objRef: Any) = companionOS.destroy(objRef)
    }

    val tasks = mutableListOf<Task>()

    private var tasksToBeAdd = mutableListOf<Task>()

    fun runTask(ticks: Long = 0,
                repeat: Boolean = false,
                startNow: Boolean = false,
                isEvery: Boolean = false,
                task: () -> Unit) = Task(
            ticksLeft = ticks - if (isEvery) 1 else 0,
            task = task,
            repeat = repeat,
            startNow = startNow
    ).run()

    fun terminateTask(task: Task, onTerminate: (() -> Unit)? = null) = task.terminate(onTerminate)

    inner class Task(
            var ticksLeft: Long = 0L,
            val repeat: Boolean = false,
            startNow: Boolean = false,
            isEvery: Boolean = false,
            val task: () -> Unit
    ) {
        val timerTicks: Long

        init {
            if (isEvery) {
                ticksLeft--
            }
            timerTicks = ticksLeft
            if (startNow) {
                ticksLeft = 0
            }
        }

        var isTerminated = false

        var onTerminate = {}

        fun runningState() =
                if (isTerminated) {
                    TaskState.TERMINATED
                } else {
                    when {
                        ticksLeft < 0L -> TaskState.WILL_TERMINATE
                        ticksLeft == 0L -> TaskState.WILL_EXECUTE
                        else -> TaskState.WILL_EXECUTE_LATER
                    }
                }

        fun run(): Task = apply {
            this@TickTaskManager.tasksToBeAdd.add(this)
        }

        fun terminate(onTerminate: (() -> Unit)? = null) {
            if (onTerminate != null) {
                this.onTerminate = onTerminate
            }
            ticksLeft = -1
        }
    }

    enum class TaskState {
        WILL_TERMINATE,
        WILL_EXECUTE,
        WILL_EXECUTE_LATER,
        TERMINATED
    }
}
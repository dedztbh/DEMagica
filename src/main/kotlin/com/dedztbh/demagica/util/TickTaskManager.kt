package com.dedztbh.demagica.util

import com.dedztbh.demagica.DEMagica

/**
 * Created by DEDZTBH on 19-2-13.
 * Project DEMagica
 */

class TickTaskManager {
    private val tasks = HashSet<Task>()

    private var tasksToBeAdd = mutableListOf<Task>()

    fun tick() {
        tasksToBeAdd.isNotEmpty() then {
            tasks.addAll(tasksToBeAdd)
            // ignoring newly created task into the old list
            tasksToBeAdd = mutableListOf()
        }

        if (tasks.isEmpty()) return
        val finishedTasks = mutableListOf<Task>()

        //Tick Tasks
        for (task in tasks) {
            try {
                task.apply {
                    when (runningState()) {
                        TaskState.WILL_TERMINATE -> {
                            //Terminate now!
                            finishedTasks.add(this)
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
                                finishedTasks.add(this)
                            }
                        }
                        else -> {
                            // Should never happen
                            DEMagica.logger.warn("Terminated task in tasks")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //Remove finished Tasks
        finishedTasks.forEach {
            it.onTerminate()
            it.isTerminated = true
            tasks.remove(it)
        }
    }

    fun runTask(ticks: Long = 0,
                repeat: Boolean = false,
                startNow: Boolean = false,
                isEvery: Boolean = false,
                task: () -> Unit) =
            Task(
                    ticksLeft = ticks,
                    task = task,
                    repeat = repeat,
                    startNow = startNow,
                    isEvery = isEvery
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
            tasksToBeAdd.add(this)
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
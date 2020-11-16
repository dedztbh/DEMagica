package com.dedztbh.demagica.util

import java.util.*

/**
 * Created by DEDZTBH on 19-2-13.
 * Project DEMagica
 */

class TickGroup {
    private val processes = Collections.newSetFromMap(WeakHashMap<Process, Boolean>())

    private var processQueue = mutableListOf<Process>()

    fun tick() {
        processQueue.isNotEmpty() then {
            processes.addAll(processQueue)
            // ignoring newly created processes into the old list
            processQueue = mutableListOf()
        }

        if (processes.isEmpty()) return
        val finished = mutableListOf<Process>()

        //Tick Tasks
        for (task in processes) {
            if (task == null) continue
            try {
                task.apply {
                    when (runningState()) {
                        State.TERMINATE -> {
                            // Terminate now!
                            finished.add(this)
                        }
                        State.EXECUTE_LATER -> {
                            // Waiting
                            ticksLeft--
                        }
                        State.EXECUTE -> {
                            // Execute and terminate/repeat
                            task()
                            if (repeat) ticksLeft = timerTicks
                            else finished.add(this)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //Remove finished Tasks
        finished.forEach {
            it.onTerminate()
            it.markTerminate()
            processes.remove(it)
        }
    }

    fun runProcess(ticks: Long = 0,
                   repeat: Boolean = false,
                   startNow: Boolean = false,
                   isEvery: Boolean = false,
                   task: () -> Unit) =
            Process(
                    ticksLeft = ticks,
                    task = task,
                    repeat = repeat,
                    startNow = startNow,
                    isEvery = isEvery
            ).exec()

    fun runProcess(ticks: Long = 0,
                   repeat: Boolean = false,
                   startNow: Boolean = false,
                   isEvery: Boolean = false,
                   task: () -> Unit,
                   onTerminate: () -> Unit) =
            Process(
                    ticksLeft = ticks,
                    task = task,
                    repeat = repeat,
                    startNow = startNow,
                    isEvery = isEvery,
                    onTerminate = onTerminate
            ).exec()

    inner class Process(
            var ticksLeft: Long = 0L,
            val repeat: Boolean = false,
            startNow: Boolean = false,
            isEvery: Boolean = false,
            val task: () -> Unit,
            var onTerminate: () -> Unit = {}
    ) {
        val timerTicks: Long

        init {
            if (isEvery) ticksLeft--
            timerTicks = ticksLeft
            if (startNow) ticksLeft = 0
        }

        fun runningState() =
                when {
                    ticksLeft < 0L -> State.TERMINATE
                    ticksLeft == 0L -> State.EXECUTE
                    else -> State.EXECUTE_LATER
                }

        fun exec(): Process = apply {
            processQueue.add(this)
        }

        fun markTerminate() {
            ticksLeft = -1
        }
    }

    enum class State {
        TERMINATE,
        EXECUTE,
        EXECUTE_LATER
    }
}
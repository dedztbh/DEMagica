package com.dedztbh.demagica.global

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.proxy.CommonProxy
import net.minecraftforge.common.config.Configuration
import org.apache.logging.log4j.Level

object Config {
    // This values below you can access elsewhere in your mod:
    var explosionDoAffectSelf = false
    var BATTERY_RF_CAPACITY = 64000
    var TANK_MB_CAPACITY = 16000
    var MB_CONSUMED = 50
    var RF_GENERATED = 50

    private val CATEGORY_GENERAL = "general"
    private val CATEGORY_MACHINES = "machines"
    private val CATEGORY_DIMENSIONS = "dimensions"

    // Call this from CommonProxy.preInit(). It will create our global if it doesn't
    // exist yet and read the values if it does exist.
    fun readConfig() {
        CommonProxy.config.run {
            try {
                load()
                initGeneralConfig()
                initMachineConfig()
                initDimensionConfig()
            } catch (e1: Exception) {
                DEMagica.logger.log(Level.ERROR, "Problem loading global file!", e1)
            } finally {
                if (hasChanged()) {
                    save()
                }
            }
        }
    }

    private fun Configuration.initGeneralConfig() {
        addCustomCategoryComment(CATEGORY_GENERAL, "General configuration")
        explosionDoAffectSelf = getBoolean("explosionDoAffectSelf", CATEGORY_GENERAL, explosionDoAffectSelf, "Explosion Do Affect Creator")
    }

    private fun Configuration.initMachineConfig() {
        addCustomCategoryComment(CATEGORY_MACHINES, "Machine configuration")
        BATTERY_RF_CAPACITY = getInt("BATTERY_RF_CAPACITY", CATEGORY_MACHINES, BATTERY_RF_CAPACITY, "How much RF magic block holds")
        TANK_MB_CAPACITY = getInt("TANK_MB_CAPACITY", CATEGORY_MACHINES, TANK_MB_CAPACITY, "How much mB of steam magic block holds")
        MB_CONSUMED = getInt("MB_CONSUMED", CATEGORY_MACHINES, MB_CONSUMED, "How much mB of steam consumed per tick")
        RF_GENERATED = getInt("RF_GENERATED", CATEGORY_MACHINES, RF_GENERATED, "How much RF generated per tick")
    }

    private fun Configuration.initDimensionConfig() {
        addCustomCategoryComment(CATEGORY_DIMENSIONS, "Dimension configuration")
    }

    private fun Configuration.getInt(name: String, category: String, defaultValue: Int, comment: String): Int {
        return getInt(name, category, defaultValue, 0, Int.MAX_VALUE, comment)
    }
}
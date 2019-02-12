package com.dedztbh.demagica.global

import com.dedztbh.demagica.DEMagica
import com.dedztbh.demagica.proxy.CommonProxy
import net.minecraftforge.common.config.Configuration
import org.apache.logging.log4j.Level

object Config {
    // This values below you can access elsewhere in your mod:
    var dalaofy = true

    private val CATEGORY_GENERAL = "general"
    private val CATEGORY_DIMENSIONS = "dimensions"

    // Call this from CommonProxy.preInit(). It will create our global if it doesn't
    // exist yet and read the values if it does exist.
    fun readConfig() {
        val cfg = CommonProxy.config
        try {
            cfg.load()
            initGeneralConfig(cfg)
            initDimensionConfig(cfg)
        } catch (e1: Exception) {
            DEMagica.logger.log(Level.ERROR, "Problem loading global file!", e1)
        } finally {
            if (cfg.hasChanged()) {
                cfg.save()
            }
        }
    }

    private fun initGeneralConfig(cfg: Configuration) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration")
        dalaofy = cfg.getBoolean("dalaofy", CATEGORY_GENERAL, dalaofy, "To Dalaofy")
    }

    private fun initDimensionConfig(cfg: Configuration) {
        cfg.addCustomCategoryComment(CATEGORY_DIMENSIONS, "Dimension configuration")

    }
}
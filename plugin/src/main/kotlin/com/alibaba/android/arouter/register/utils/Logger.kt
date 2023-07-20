package com.alibaba.android.arouter.register.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * Format log
 */
object Logger {
    fun make(project: Project) {
        logger = project.logger
    }

    fun i(info: String?) {
        if (null != info && null != logger) {
            logger!!.info("ARouter::Register >>> $info")
        }
    }

    fun e(error: String?) {
        if (null != error && null != logger) {
            logger!!.error("ARouter::Register >>> $error")
        }
    }

    fun w(warning: String?) {
        if (null != warning && null != logger) {
            logger!!.warn("ARouter::Register >>> $warning")
        }
    }

    var logger: Logger? = null
}
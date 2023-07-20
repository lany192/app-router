package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.core.transform.RegisterTransform
import com.alibaba.android.arouter.register.utils.Logger
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Simple version of AutoRegister plugin for ARouter
 */
class PluginLaunch : Plugin<Project> {
    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        //only application module needs this plugin to generate register code
        if (isApp) {
            Logger.make(project)
            Logger.i("Project enable arouter-register plugin")
            RegisterTransform.transform(project)
        }
    }
}
package com.alibaba.android.arouter.register.core.transform.visitor.collect

import com.alibaba.android.arouter.register.utils.ScanSetting
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input

interface CollectParams : InstrumentationParameters {

    @get:Input
    val registerMap: MapProperty<String, ScanSetting>

}
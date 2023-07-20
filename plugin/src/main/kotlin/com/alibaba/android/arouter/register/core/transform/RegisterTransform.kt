package com.alibaba.android.arouter.register.core.transform

import com.alibaba.android.arouter.register.core.transform.visitor.collect.CollectClassVisitorFactory
import com.alibaba.android.arouter.register.core.transform.visitor.inject.InjectClassVisitorFactory
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

object RegisterTransform {

    fun transform(project: Project) {
        val interfaceOne = "com.alibaba.android.arouter.facade.template.IRouteRoot"
        val interfaceTwo = "com.alibaba.android.arouter.facade.template.IRouteGroup"
        val interfaceThree = "com.alibaba.android.arouter.facade.template.IProviderGroup"

        val map = hashMapOf<String, ScanSetting>()
        map[interfaceOne] = ScanSetting(interfaceOne)
        map[interfaceTwo] = ScanSetting(interfaceTwo)
        map[interfaceThree] = ScanSetting(interfaceThree)
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                CollectClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {
                it.registerMap.set(map)
            }
            variant.instrumentation.transformClassesWith(
                InjectClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {
                it.registerMap.set(map)
            }
        }
    }

}
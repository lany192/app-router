package com.alibaba.android.arouter.register.core.transform

import com.alibaba.android.arouter.register.core.transform.visitor.collect.CollectClassVisitorFactory
import com.alibaba.android.arouter.register.core.transform.visitor.inject.InjectClassVisitorFactory
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

/**
 * @author jadepeakpoet(Yufeng Wu) email: wuyufengaw@gmail.com
 * @since 2023/04/21
 */
object RegisterTransform {

    fun transform(project: Project) {
        val interface1 = "com.alibaba.android.arouter.facade.template.IRouteRoot"
        val interface2 = "com.alibaba.android.arouter.facade.template.IRouteGroup"
        val interface3 = "com.alibaba.android.arouter.facade.template.IProviderGroup"
        val interface4 = "com.alibaba.android.arouter.facade.template.IProvider"
        val interface5 = "com.alibaba.android.arouter.facade.template.IInterceptorGroup"

        val map = hashMapOf<String, ScanSetting>()
        map[interface1] = ScanSetting(interface1)
        map[interface2] = ScanSetting(interface2)
        map[interface3] = ScanSetting(interface3)
        map[interface4] = ScanSetting(interface4)
        map[interface5] = ScanSetting(interface5)
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
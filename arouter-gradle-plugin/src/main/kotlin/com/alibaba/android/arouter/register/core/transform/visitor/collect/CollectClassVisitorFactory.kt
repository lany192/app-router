package com.alibaba.android.arouter.register.core.transform.visitor.collect

import com.alibaba.android.arouter.register.utils.ScanSetting
import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.util.TraceClassVisitor
import java.io.PrintWriter

abstract class CollectClassVisitorFactory : AsmClassVisitorFactory<CollectParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return TraceClassVisitor(nextClassVisitor, PrintWriter(System.out))
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.className.startsWith(ScanSetting.rOUTER_CLASS_PACKAGE_NAME)) {
            classData.interfaces.forEach {
                val scanSettings = parameters.get().registerMap.get()[it]
                if (scanSettings != null) {
                    // set could avoid repeated inject init code when Multi-channel packaging
                    return scanSettings.classSet.add(classData.className)
                }
            }
        }
        return false
    }
}
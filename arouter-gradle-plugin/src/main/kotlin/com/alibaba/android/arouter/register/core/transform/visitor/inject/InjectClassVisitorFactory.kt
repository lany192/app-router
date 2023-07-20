package com.alibaba.android.arouter.register.core.transform.visitor.inject

import com.alibaba.android.arouter.register.core.transform.visitor.collect.CollectParams
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

abstract class InjectClassVisitorFactory : AsmClassVisitorFactory<CollectParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return RouteClassVisitor(Opcodes.ASM9, nextClassVisitor, parameters.get())
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className == ScanSetting.gENERATE_TO_CLASS_FILE_NAME
    }
}
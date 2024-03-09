package com.alibaba.android.arouter.register.core.transform.visitor.inject

import com.alibaba.android.arouter.register.core.transform.visitor.collect.CollectParams
import com.alibaba.android.arouter.register.utils.ScanSetting
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RouteClassVisitor(api: Int, cv: ClassVisitor?, private var collectParams: CollectParams) :
    ClassVisitor(api, cv) {
    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String,
        superName: String,
        interfaces: Array<String>
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        desc: String,
        signature: String,
        exceptions: Array<String>
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, desc, signature, exceptions)
        //generate code into this method
        if (name == ScanSetting.gENERATE_TO_METHOD_NAME) {
            mv = RouteMethodVisitor(Opcodes.ASM9, mv, collectParams)
        }
        return mv
    }

}
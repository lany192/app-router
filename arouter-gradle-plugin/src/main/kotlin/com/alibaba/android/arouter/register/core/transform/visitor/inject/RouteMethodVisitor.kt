package com.alibaba.android.arouter.register.core.transform.visitor.inject

import com.alibaba.android.arouter.register.core.transform.visitor.collect.CollectParams
import com.alibaba.android.arouter.register.utils.ScanSetting
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class RouteMethodVisitor(api: Int, mv: MethodVisitor?, var collectParams: CollectParams) :
    MethodVisitor(api, mv) {
    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            collectParams.registerMap.get().forEach { entry ->
                entry.value.classSet.forEach { className ->
                    mv.visitLdcInsn(className) //类名
                    // generate invoke register method into LogisticsCenter.loadRouterMap()
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        ScanSetting.gENERATE_TO_CLASS_NAME,
                        ScanSetting.rEGISTER_METHOD_NAME,
                        "(Ljava/lang/String;)V",
                        false
                    )
                }
            }
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 4, maxLocals)
    }

}
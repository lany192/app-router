package com.github.lany192.arouter.utils;


import com.alibaba.android.arouter.facade.enums.TypeKind;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.github.lany192.arouter.utils.Consts.*;

public class TypeUtils {

    private final Types types;
    private final TypeMirror parcelableType;
    private final TypeMirror serializableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;

        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    /**
     * Diagnostics out the true java type
     *
     * @param element Raw type
     * @return Type class of java
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}

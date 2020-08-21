package com.tb.library.tbExtend

import com.tb.library.util.TbLogUtils
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Created by Tb on 2020/8/18.
 * describe:反射获取泛型
 */
/**
 *
 * @receiver Type
 * @param index Int 泛型位置标记
 * @return Type
 */
fun Type.getType(index: Int = 0): Type {
    when (this) {
        is ParameterizedType -> {
            return this.getGenericType(this, index)
        }
        is TypeVariable<*> -> {
            return this.bounds[index].getType()
        }
    }
    return this
}

private fun ParameterizedType.getGenericType(type: ParameterizedType, index: Int): Type {
    if (type.actualTypeArguments.isEmpty()) return type
    val actualType = type.actualTypeArguments[index]
    when (actualType) {
        is ParameterizedType -> {
            return actualType.rawType
        }
        is GenericArrayType -> {
            return actualType.genericComponentType
        }
        is TypeVariable<*> -> {
            return actualType.bounds[index]
        }
    }
    return actualType
}
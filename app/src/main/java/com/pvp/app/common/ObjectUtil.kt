package com.pvp.app.common

object ObjectUtil {

    fun Any?.isNotNull(): Boolean {
        return this != null
    }

    fun Any?.isNull(): Boolean {
        return this == null
    }
}
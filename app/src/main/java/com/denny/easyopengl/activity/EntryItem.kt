package com.denny.easyopengl.activity

import java.io.Serializable

data class EntryItem(
    var showName: String? = null,
    var clazz: Class<*>? = null
):Serializable

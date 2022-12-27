package com.ltl.recipes.utils

import org.json.JSONObject

interface Writable {
    fun toJson(): JSONObject
}
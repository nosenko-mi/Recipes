package com.ltl.recipes.ingredient

import com.google.gson.Gson
import com.ltl.recipes.R
import com.ltl.recipes.utils.Writable
import org.json.JSONObject

enum class QuantityType(val type: String): Writable, java.io.Serializable {
    NONE("none") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }
    },
    GRAM("Gram") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }
    },
    MILL("Mill") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }
    },
    OZ("Oz") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }
    },
    SPOON("Spoon") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }
    };

    override fun toString(): String {
        return type
    }
}
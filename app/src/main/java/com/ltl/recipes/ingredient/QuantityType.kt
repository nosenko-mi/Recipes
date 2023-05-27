package com.ltl.recipes.ingredient

import com.google.gson.Gson
import com.ltl.recipes.utils.SpinnerPosition
import com.ltl.recipes.utils.Writable
import org.json.JSONObject

enum class QuantityType(val type: String): Writable, SpinnerPosition, java.io.Serializable {
    NONE("none") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }

        override fun toSpinnerPosition(): Int{
            return 0
        }
    },
    GRAM("Gram") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }

        override fun toSpinnerPosition(): Int{
            return 1
        }
    },
    MILL("Mill") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }

        override fun toSpinnerPosition(): Int{
            return 2
        }
    },
    OZ("Oz") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }

        override fun toSpinnerPosition(): Int{
            return 3
        }
    },
    SPOON("Spoon") {
        override fun toJson(): JSONObject {
            val gson = Gson()
            val jsonType: String = gson.toJson(this)
            return JSONObject(jsonType)
        }

        override fun toSpinnerPosition(): Int{
            return 4
        }
    };

    override fun toString(): String {
        return type
    }
}
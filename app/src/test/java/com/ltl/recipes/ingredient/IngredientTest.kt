package com.ltl.recipes.ingredient

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class IngredientTest{

    @Test
    fun emptyTitleReturnsFalse() {
        val ingredient = Ingredient("", 1f, QuantityType.GRAM)
        val result = ingredient.isValid()

        assertThat(result).isFalse()
    }

    @Test
    fun negativeQtyReturnsFalse() {
        val ingredient = Ingredient("s", -1f, QuantityType.GRAM)
        val result = ingredient.isValid()

        assertThat(result).isFalse()
    }

    @Test
    fun nanQtyReturnsFalse() {
        val ingredient = Ingredient("s", Float.NaN, QuantityType.GRAM)
        val result = ingredient.isValid()

        assertThat(result).isFalse()
    }
}
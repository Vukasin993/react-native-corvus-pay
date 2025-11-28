package com.corvuspay.sdk.models

import com.corvuspay.sdk.enums.CardType

data class InstallmentsMap(
    private val discountsByCardType: Map<CardType, List<Discount>> = emptyMap()
) {
    data class Discount(
        val numberOfInstallments: Int,
        val amount: Double,
        val discountedAmount: Double
    )

    fun getDiscounts(cardType: CardType): List<Discount>? {
        return discountsByCardType[cardType]
    }

    class Builder {
        private val discountsByCardType = mutableMapOf<CardType, List<Discount>>()

        fun addDiscountsFor(cardType: CardType, discounts: List<Discount>) = apply {
            discountsByCardType[cardType] = discounts
        }

        fun build(): InstallmentsMap {
            return InstallmentsMap(discountsByCardType)
        }
    }
}

package com.corvuspay.sdk.models

import com.corvuspay.sdk.enums.Language
import com.corvuspay.sdk.enums.Currency

data class Checkout(
    val storeId: Int,
    val orderId: String,
    val language: Language,
    val cart: String,
    val currency: Currency,
    val amount: Double,
    val requireComplete: Boolean = false,
    val signature: String,
    val bestBefore: Long? = null,
    val discountAmount: Double? = null,
    val installmentParams: InstallmentParams? = null,
    val cardholder: Cardholder? = null,
    val installmentsMap: InstallmentsMap? = null,
    val sdkVersion: Double,
    val isSdk: Boolean,
    val version: String,
    val useCardProfiles: Boolean? = null,
    val userCardProfilesId: String? = null,
    val voucherAmount: Double? = null,
    val hideTab: String? = null,
    val hideTabs: String? = null,
    val creditorReference: String? = null,
    val debtorIban: String? = null,
    val shopAccountId: String? = null
) {
    fun generateStringForSignature(): String {
        val amountFormatted = String.format("%.2f", amount)
        return "$storeId|$orderId|$amountFormatted|${currency.name}"
    }
}

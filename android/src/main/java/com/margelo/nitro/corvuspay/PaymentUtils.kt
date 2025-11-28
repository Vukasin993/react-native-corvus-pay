package com.margelo.nitro.corvuspay

import com.corvuspay.sdk.enums.CardType
import com.corvuspay.sdk.enums.Currency
import com.corvuspay.sdk.enums.Language
import com.corvuspay.sdk.models.InstallmentsMap
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

object PaymentUtils {

    fun optString(m: ReadableMap, key: String): String? =
        if (m.hasKey(key) && !m.isNull(key)) m.getString(key) else null

    fun optDouble(m: ReadableMap, key: String): Double? {
        if (!m.hasKey(key) || m.isNull(key)) return null
        return try {
            m.getDouble(key)
        } catch (_: Throwable) {
            null
        }
    }

    fun optLongFromNumber(m: ReadableMap, key: String): Long? {
        if (!m.hasKey(key) || m.isNull(key)) return null
        return try {
            m.getDouble(key).toLong()
        } catch (_: Throwable) {
            null
        }
    }

    fun optBoolean(m: ReadableMap, key: String, defVal: Boolean): Boolean =
        if (m.hasKey(key) && !m.isNull(key)) {
            try {
                m.getBoolean(key)
            } catch (_: Throwable) {
                defVal
            }
        } else {
            defVal
        }

    fun optInt(m: ReadableMap, key: String): Int? {
        if (!m.hasKey(key) || m.isNull(key)) return null
        return try {
            m.getInt(key)
        } catch (_: Throwable) {
            try {
                m.getDouble(key).toInt()
            } catch (_: Throwable) {
                null
            }
        }
    }

    fun optBooleanObj(m: ReadableMap, key: String): Boolean? {
        if (!m.hasKey(key) || m.isNull(key)) return null
        return try {
            m.getBoolean(key)
        } catch (_: Throwable) {
            null
        }
    }

    fun optMap(m: ReadableMap, key: String): ReadableMap? {
        if (!m.hasKey(key) || m.isNull(key)) return null
        return try {
            m.getMap(key)
        } catch (_: Throwable) {
            null
        }
    }

    fun tripleFromRange(m: ReadableMap?): Triple<Boolean, Int, Int> {
        val enabled = if (m != null) optBoolean(m, "enabled", false) else false
        val min = if (m != null) (optInt(m, "min") ?: 0) else 0
        val max = if (m != null) (optInt(m, "max") ?: 0) else 0
        return Triple(enabled, min, max)
    }

    fun parseCardType(name: String?): CardType? =
        name?.trim()?.uppercase()?.let {
            try {
                CardType.valueOf(it)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

    fun optArray(m: ReadableMap, key: String): ReadableArray? {
        if (!m.hasKey(key) || m.isNull(key)) return null
        return try {
            m.getArray(key)
        } catch (ignore: Throwable) {
            null
        }
    }

    fun toDiscountList(arr: ReadableArray?): List<InstallmentsMap.Discount> {
        val discounts = mutableListOf<InstallmentsMap.Discount>()
        if (arr == null) return discounts

        for (i in 0 until arr.size()) {
            if (arr.isNull(i)) continue
            val row = arr.getMap(i) ?: continue

            val n = optInt(row, "numberOfInstallments")
            val a = optDouble(row, "amount")
            val da = optDouble(row, "discountedAmount")
            if (n == null || a == null || da == null) continue

            val nn = n.coerceAtLeast(1)
            val amt = a.coerceAtLeast(0.0)
            val dam = da.coerceAtLeast(0.0)

            discounts.add(InstallmentsMap.Discount(nn, amt, dam))
        }
        return discounts
    }

    fun parseLanguage(langStr: String?): Language {
        if (langStr == null) {
            return Language.EN // sensible default
        }
        return try {
            Language.valueOf(langStr.uppercase()) // enforce upper-case
        } catch (e: IllegalArgumentException) {
            Language.EN // fallback if unknown
        }
    }

    fun parseCurrency(currencyStr: String?): Currency {
        if (currencyStr == null) {
            return Currency.EUR // sensible default
        }
        return try {
            Currency.valueOf(currencyStr.uppercase()) // enforce upper-case
        } catch (e: IllegalArgumentException) {
            Currency.EUR // fallback if unknown
        }
    }

    fun requireString(m: ReadableMap, key: String): String {
        val value = optString(m, key)
        if (value == null || value.trim().isEmpty()) {
            throw IllegalArgumentException("Missing required field: $key")
        }
        return value
    }
}

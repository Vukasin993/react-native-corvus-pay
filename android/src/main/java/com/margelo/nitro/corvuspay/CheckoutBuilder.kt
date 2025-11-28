package com.margelo.nitro.corvuspay

import com.margelo.nitro.corvuspay.PaymentModule.Companion.CHECKOUT_VERSION_STR
import com.margelo.nitro.corvuspay.PaymentModule.Companion.IS_SDK_FLAG
import com.margelo.nitro.corvuspay.PaymentModule.Companion.SDK_VERSION_NUM
import com.margelo.nitro.corvuspay.PaymentUtils.optArray
import com.margelo.nitro.corvuspay.PaymentUtils.optBoolean
import com.margelo.nitro.corvuspay.PaymentUtils.optBooleanObj
import com.margelo.nitro.corvuspay.PaymentUtils.optDouble
import com.margelo.nitro.corvuspay.PaymentUtils.optInt
import com.margelo.nitro.corvuspay.PaymentUtils.optLongFromNumber
import com.margelo.nitro.corvuspay.PaymentUtils.optMap
import com.margelo.nitro.corvuspay.PaymentUtils.optString
import com.margelo.nitro.corvuspay.PaymentUtils.parseCardType
import com.margelo.nitro.corvuspay.PaymentUtils.parseCurrency
import com.margelo.nitro.corvuspay.PaymentUtils.parseLanguage
import com.margelo.nitro.corvuspay.PaymentUtils.requireString
import com.margelo.nitro.corvuspay.PaymentUtils.toDiscountList
import com.margelo.nitro.corvuspay.PaymentUtils.tripleFromRange
import com.corvuspay.sdk.models.Cardholder
import com.corvuspay.sdk.models.Checkout
import com.corvuspay.sdk.models.DynamicInstallmentParams
import com.corvuspay.sdk.models.InstallmentParams
import com.corvuspay.sdk.models.InstallmentsMap
import com.facebook.react.bridge.ReadableMap

object CheckoutBuilder {

    fun buildCheckoutFromParams(params: ReadableMap, signature: String): Checkout {
        val storeId = params.getInt("storeId")
        val orderId = requireString(params, "orderId")
        val cart = requireString(params, "cart")
        val language = parseLanguage(optString(params, "language"))
        val currency = parseCurrency(optString(params, "currency"))
        val amount = params.getDouble("amount")
        val requireComplete = optBoolean(params, "requireComplete", false)
        val bestBefore = optLongFromNumber(params, "bestBefore")
        val discountAmount = optDouble(params, "discountAmount")
        val installmentParams = buildInstallmentParams(params)
        val cardholder = buildCardholder(params)
        val installmentsMap = buildInstallmentsMap(params)
        val useCardProfiles = optBooleanObj(params, "useCardProfiles")
        val userCardProfilesId = optString(params, "userCardProfilesId")
        val voucherAmount = optDouble(params, "voucherAmount")
        val hideTab = optString(params, "hideTab")
        val hideTabs = optString(params, "hideTabs")
        val creditorReference = optString(params, "creditorReference")
        val debtorIban = optString(params, "debtorIban")
        val shopAccountId = optString(params, "shopAccountId")

        return Checkout(
            storeId = storeId,
            orderId = orderId,
            language = language,
            cart = cart,
            currency = currency,
            amount = amount,
            requireComplete = requireComplete,
            signature = signature,
            bestBefore = bestBefore,
            discountAmount = discountAmount,
            installmentParams = installmentParams,
            cardholder = cardholder,
            installmentsMap = installmentsMap,
            sdkVersion = SDK_VERSION_NUM,
            isSdk = IS_SDK_FLAG,
            version = CHECKOUT_VERSION_STR,
            useCardProfiles = useCardProfiles,
            userCardProfilesId = userCardProfilesId,
            voucherAmount = voucherAmount,
            hideTab = hideTab,
            hideTabs = hideTabs,
            creditorReference = creditorReference,
            debtorIban = debtorIban,
            shopAccountId = shopAccountId
        )
    }

    fun buildCardholder(root: ReadableMap): Cardholder? {
        if (!root.hasKey("cardholder") || root.isNull("cardholder")) return null

        val m = root.getMap("cardholder") ?: return null

        val address = optString(m, "address")
        val city = optString(m, "city")
        val country = optString(m, "country")
        val countryCode = optString(m, "countryCode")
        val email = optString(m, "email")
        val firstName = optString(m, "firstName")
        val phone = optString(m, "phone")
        val lastName = optString(m, "lastName")
        val zip = optString(m, "zip")

        if (address == null && city == null && country == null && countryCode == null &&
            email == null && firstName == null && lastName == null && phone == null && zip == null
        ) {
            return null
        }

        return Cardholder(
            cardholderAddress = address,
            cardholderCity = city,
            cardholderCountry = country,
            cardholderCountryCode = countryCode,
            cardholderEmail = email,
            cardholderName = firstName,
            cardholderPhone = phone,
            cardholderSurname = lastName,
            cardholderZipCode = zip
        )
    }

    fun buildInstallmentParams(root: ReadableMap): InstallmentParams? {
        val m = optMap(root, "installmentParams") ?: return null

        optMap(m, "paymentAll")?.let { all ->
            val enabled = optBoolean(all, "enabled", false)
            val min = optInt(all, "min") ?: 0
            val max = optInt(all, "max") ?: 0
            if (enabled) {
                val safeMin = min.coerceAtLeast(0)
                val safeMax = max.coerceAtLeast(safeMin)
                return InstallmentParams.createUsingPaymentAllWith(true, safeMin, safeMax)
            }
        }

        optInt(m, "numberOfInstallments")?.let { numberOfInstallments ->
            if (numberOfInstallments > 0) return InstallmentParams.createUsingNumberOfInstallmentsWith(
                numberOfInstallments
            )
        }

        optMap(m, "paymentAllDynamic")?.let { paymentAllDynamic ->
            buildDynamicInstallmentParams(paymentAllDynamic)?.let { dynamic ->
                return InstallmentParams.createUsingPaymentAllDynamicWith(dynamic)
            }
        }
        return null
    }

    fun buildDynamicInstallmentParams(root: ReadableMap?): DynamicInstallmentParams? {
        if (root == null) return null

        val amex = tripleFromRange(optMap(root, "paymentAmex"))
        val visa = tripleFromRange(optMap(root, "paymentVisa"))
        val master = tripleFromRange(optMap(root, "paymentMaster"))
        val maestro = tripleFromRange(optMap(root, "paymentMaestro"))
        val jcb = tripleFromRange(optMap(root, "paymentJcb"))
        val discover = tripleFromRange(optMap(root, "paymentDiscover"))
        val diners = tripleFromRange(optMap(root, "paymentDiners"))
        val dina = tripleFromRange(optMap(root, "paymentDina"))

        val builder = DynamicInstallmentParams.Builder()
            .addAmex(amex.first, amex.second, amex.third)
            .addVisa(visa.first, visa.second, visa.third)
            .addMaster(master.first, master.second, master.third)
            .addMaestro(maestro.first, maestro.second, maestro.third)
            .addJcb(jcb.first, jcb.second, jcb.third)
            .addDiscover(discover.first, discover.second, discover.third)
            .addDiners(diners.first, diners.second, diners.third)
            .addDina(dina.first, dina.second, dina.third)

        return builder.build()
    }

    fun buildInstallmentsMap(root: ReadableMap): InstallmentsMap? {
        val im = optMap(root, "installmentsMap") ?: return null
        val items = optArray(im, "items") ?: return null

        val builder = InstallmentsMap.Builder()
        var addedAny = false

        for (i in 0 until items.size()) {
            if (items.isNull(i)) continue
            val item = items.getMap(i) ?: continue

            val type = parseCardType(optString(item, "cardType")) ?: continue
            val discounts = toDiscountList(optArray(item, "discounts"))
            if (discounts.isEmpty()) continue

            builder.addDiscountsFor(type, discounts)
            addedAny = true
        }

        return if (addedAny) builder.build() else null
    }
}

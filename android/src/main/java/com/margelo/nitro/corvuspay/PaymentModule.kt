package com.margelo.nitro.corvuspay

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.margelo.nitro.corvuspay.CheckoutBuilder.buildCheckoutFromParams
import com.corvuspay.sdk.constants.CheckoutCodes
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class PaymentModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val NAME = "PaymentModule"

        const val ENV_TEST = "test"
        const val ENV_PRODUCTION = "production"

        const val IS_SDK_FLAG = true
        const val CHECKOUT_VERSION_STR = "1.3"
        const val SDK_VERSION_NUM = 1.3
    }

    private var pendingPromise: Promise? = null
    private var environment = ENV_TEST

    override fun getName(): String {
        return NAME
    }

    /**
     * Configure environment: "TEST" or "PRODUCTION".
     */
    @ReactMethod
    fun configureEnvironment(env: String) {
        environment = if (ENV_PRODUCTION.equals(env, ignoreCase = true)) {
            ENV_PRODUCTION
        } else {
            ENV_TEST
        }
    }

    /**
     *
     * Example JS object
     *
     * const checkoutParams = {
     *   // REQUIRED
     *   storeId: 12345,
     *   orderId: "ORDER-2025-00001",
     *   cart: "iPhone 15 x 1; AirPods Pro x 2; Charger x 1",,
     *   language: "EN",   // enum string -> Language.EN
     *   currency: "EUR",  // enum string -> Currency.EUR
     *   amount: 539.99,
     *
     *   // OPTIONAL
     *   requireComplete: false,
     *   bestBefore: Math.floor(Date.now() / 1000) + 600, // epoch seconds (≤ 900s in future)
     *   discountAmount: 10.00,
     *   voucherAmount: 0.00,
     *
     *   // OPTIONAL — Card Profiles
     *   useCardProfiles: true,
     *   userCardProfilesId: "user_abc_01",
     *
     *   // OPTIONAL — UI / flow tuning
     *   hideTab: "WALLET",
     *   hideTabs: "WALLET,CARDS",
     *   creditorReference: "RF18539007547034",
     *   debtorIban: "RS35160005001061195179",
     *   shopAccountId: "shop-001",
     *
     *   // OPTIONAL — Cardholder data
     *   cardholder: {
     *     address: "Bulevar Oslobođenja 1",
     *     city: "Novi Sad",
     *     country: "Serbia",
     *     countryCode: "RS",
     *     email: "test@example.com",
     *     firstName: "Petar",
     *     lastName: "Petrović",
     *     phone: "+381601234567",
     *     zip: "21000",
     *   },
     *
     *   // OPTIONAL — Installment parameters (pick ONE form)
     *   // A) Fixed
     *   // installmentParams: { numberOfInstallments: 6 },
     *
     *   // B) Payment All
     *   // installmentParams: { paymentAll: { enabled: true, min: 2, max: 12 } },
     *
     *   // C) Dynamic per-brand
     *   installmentParams: {
     *     paymentVisa:     { enabled: true, min: 2, max: 12 },
     *     paymentMaster:   { enabled: true, min: 2, max: 12 },
     *     paymentMaestro:  { enabled: true, min: 2, max: 12 },
     *     paymentAmex:     { enabled: true, min: 3, max: 6 },
     *     paymentDiners:   { enabled: true, min: 2, max: 6 },
     *     paymentDiscover: { enabled: false, min: 0, max: 0 },
     *     paymentDina:     { enabled: false, min: 0, max: 0 },
     *     paymentJcb:      { enabled: false, min: 0, max: 0 }
     *   },
     *
     *   // OPTIONAL — Installments Map (per cardType, each with list of discounts)
     *   installmentsMap: {
     *     items: [
     *       {
     *         cardType: "VISA",
     *         discounts: [
     *           { numberOfInstallments: 1, amount: 539.99, discountedAmount: 536.99 },
     *           { numberOfInstallments: 2, amount: 539.99, discountedAmount: 537.99 },
     *         ]
     *       },
     *       {
     *         cardType: "DINERS",
     *         discounts: [
     *           { numberOfInstallments: 3, amount: 539.99, discountedAmount: 538.99 }
     *         ]
     *       }
     *     ]
     *   }
     * };
     *
     * // Usage from JS:
     * // const { PaymentModule } = NativeModules;
     * // const signature = await fetchSignatureFromBackend(checkoutParams);
     * // await PaymentModule.checkoutWithSignature(checkoutParams, signature, promise);
     */

    @ReactMethod
    fun checkoutWithSignature(params: ReadableMap, signature: String, promise: Promise) {
        if (pendingPromise != null) {
            promise.reject("ALREADY_RUNNING", "Another checkout flow is in progress.")
            return
        }

        try {
            // Store promise for handling activity result
            pendingPromise = promise
            
            // Build checkout from parameters
            val checkout = buildCheckoutFromParams(params, signature)
            
            Log.d("PaymentModule", "Checkout called: Order=${PaymentUtils.optString(params, "orderId")}, Amount=${params.getDouble("amount")}")
            
            // TODO: Call CorvusPay SDK when available
            // For now, just log
            // val activity = reactApplicationContext.currentActivity
            // if (activity != null) {
            //     com.corvuspay.sdk.CorvusPay.checkout(activity, checkout, environment)
            // }
            
            val result = Arguments.createMap()
            result.putString("status", "checkout_initiated")
            result.putString("environment", environment)
            result.putString("orderId", PaymentUtils.optString(params, "orderId"))
            result.putDouble("amount", params.getDouble("amount"))
            result.putString("message", "CorvusPay SDK stub - checkout parameters logged")
            
            promise.resolve(result)
            pendingPromise = null

        } catch (t: Throwable) {
            Log.e("PaymentModule", "Checkout error", t)
            promise.reject("CHECKOUT_INIT_ERROR", t.message, t)
            pendingPromise = null
        }
    }

    @ReactMethod
    fun checkoutWithSecret(params: ReadableMap, secretKey: String, promise: Promise) {
        try {
            val temp = buildCheckoutFromParams(params = params, signature = "")
            
            // Create a string representation for signing
            val stringToBeSigned = buildString {
                append(params.getInt("storeId"))
                append("|")
                append(PaymentUtils.optString(params, "orderId") ?: "")
                append("|")
                append(params.getDouble("amount"))
                append("|")
                append(PaymentUtils.optString(params, "currency") ?: "EUR")
            }
            
            val signedCheckout =
                EncryptionHelper.generateHashWithHmac256(stringToBeSigned, secretKey)

            checkoutWithSignature(params = params, signature = signedCheckout, promise = promise)

        } catch (t: Throwable) {
            promise.reject("SIGNING_ERROR", t.message, t)
        }
    }
}

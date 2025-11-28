package com.margelo.nitro.corvuspay

import android.app.Activity
import android.content.Intent
import com.margelo.nitro.corvuspay.CheckoutBuilder.buildCheckoutFromParams
import com.corvuspay.sdk.CorvusPay
import com.corvuspay.sdk.constants.CheckoutCodes
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class PaymentModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), ActivityEventListener {

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
    private val mReactContext = reactContext

    init {
        reactContext.addActivityEventListener(this)
    }

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
        val activity = mReactContext.currentActivity as? Activity
        if (activity == null) {
            promise.reject("NO_ACTIVITY", "Current activity is null.")
            return
        }
        if (pendingPromise != null) {
            promise.reject("ALREADY_RUNNING", "Another checkout flow is in progress.")
            return
        }

        try {
            val checkout = buildCheckoutFromParams(params, signature)
            pendingPromise = promise

            CorvusPay.checkout(activity, checkout, environment)

        } catch (t: Throwable) {
            promise.reject("CHECKOUT_INIT_ERROR", t.message, t)
        }
    }

    @ReactMethod
    fun checkoutWithSecret(params: ReadableMap, secretKey: String, promise: Promise) {
        try {
            val temp = buildCheckoutFromParams(params = params, signature = "")
            val stringToBeSigned = temp.generateStringForSignature()
            val signedCheckout =
                EncryptionHelper.generateHashWithHmac256(stringToBeSigned, secretKey)

            checkoutWithSignature(params = params, signature = signedCheckout, promise = promise)

        } catch (t: Throwable) {
            promise.reject("SIGNING_ERROR", t.message, t)
        }
    }

    override fun onActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == CheckoutCodes.REQUEST_CHECKOUT) {
            val promise = pendingPromise
            pendingPromise = null

            if (promise == null) return

            try {
                when (resultCode) {
                    CheckoutCodes.RESULT_CODE_CHECKOUT_SUCCESS -> {
                        // You may extract reference/transaction id from the Intent extras if provided by the SDK demo.
                        // For safety, return a minimal payload and let your backend confirm the status server-to-server.
                        
                        // Log Intent data for debugging
                        println("PaymentModule - Checkout Success - Intent data:")
                        println("PaymentModule -Intent is null: ${data == null}")
                        
                        if (data != null) {
                            println("PaymentModule -Intent action: ${data.action}")
                            println("PaymentModule -Intent data URI: ${data.data}")
                            println("PaymentModule -Intent type: ${data.type}")
                            println("PaymentModule -Intent categories: ${data.categories}")
                            println("PaymentModule -Intent component: ${data.component}")
                            println("PaymentModule -Intent package: ${data.`package`}")
                            
                            val extras = data.extras
                            println("PaymentModule -Intent extras is null: ${extras == null}")
                            
                            if (extras != null) {
                                println("PaymentModule -Intent extras size: ${extras.size()}")
                                println("PaymentModule -Intent extras keys: ${extras.keySet()}")
                                
                                // Log each extra key-value pair
                                for (key in extras.keySet()) {
                                    val value = extras.get(key)
                                    println("PaymentModule -Extra '$key': $value (type: ${value?.javaClass?.simpleName})")
                                }
                            }
                        }
                        
                        promise.resolve(Arguments.createMap()) // or pass back fields from data.getExtras()
                    }

                    CheckoutCodes.RESULT_CODE_CHECKOUT_ABORTED -> {
                        promise.reject("CHECKOUT_ABORTED", "User cancelled checkout.")
                    }

                    CheckoutCodes.RESULT_CODE_CHECKOUT_FAILURE -> {
                        var err = "Checkout failed."
                        if (data?.extras != null) {
                            // Optionally propagate SDK error messages if available in extras
                            val sdkMsg = data.extras?.get("errorMessage")
                            if (sdkMsg != null) err = sdkMsg.toString()
                        }
                        promise.reject("CHECKOUT_FAILURE", err)
                    }

                    else -> {
                        var err = "Checkout failed."
                        if (data?.extras != null) {
                            // Optionally propagate SDK error messages if available in extras
                            val sdkMsg = data.extras?.get("errorMessage")
                            if (sdkMsg != null) err = sdkMsg.toString()
                        }
                        promise.reject("CHECKOUT_FAILURE", err)
                    }
                }
            } catch (t: Throwable) {
                promise.reject("RESULT_PARSE_ERROR", t.message, t)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        // Not used; SDK returns via startActivityForResult per docs.
    }
}

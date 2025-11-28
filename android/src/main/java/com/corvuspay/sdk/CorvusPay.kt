package com.corvuspay.sdk

import android.app.Activity
import com.corvuspay.sdk.models.Checkout

object CorvusPay {
    fun checkout(activity: Activity, checkout: Checkout, environment: String) {
        // Placeholder for actual implementation
        // When CorvusPay SDK is available, this will be implemented
        // For now, just log the checkout attempt
        android.util.Log.d("CorvusPay", "Checkout called with:")
        android.util.Log.d("CorvusPay", "  Store ID: ${checkout.storeId}")
        android.util.Log.d("CorvusPay", "  Order ID: ${checkout.orderId}")
        android.util.Log.d("CorvusPay", "  Amount: ${checkout.amount}")
        android.util.Log.d("CorvusPay", "  Currency: ${checkout.currency}")
        android.util.Log.d("CorvusPay", "  Environment: $environment")
    }
}

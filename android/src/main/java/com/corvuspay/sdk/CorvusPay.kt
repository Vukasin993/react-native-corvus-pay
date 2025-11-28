package com.corvuspay.sdk

import android.app.Activity
import android.content.Intent
import com.corvuspay.sdk.models.Checkout

object CorvusPay {
    fun checkout(activity: Activity, checkout: Checkout, environment: String) {
        // Log the checkout attempt
        android.util.Log.d("CorvusPay", "Checkout called with:")
        android.util.Log.d("CorvusPay", "  Store ID: ${checkout.storeId}")
        android.util.Log.d("CorvusPay", "  Order ID: ${checkout.orderId}")
        android.util.Log.d("CorvusPay", "  Amount: ${checkout.amount}")
        android.util.Log.d("CorvusPay", "  Currency: ${checkout.currency}")
        android.util.Log.d("CorvusPay", "  Environment: $environment")
        
        // Start WebView-based checkout activity
        val intent = Intent(activity, CheckoutWebViewActivity::class.java)
        intent.putExtra(CheckoutWebViewActivity.EXTRA_STORE_ID, checkout.storeId)
        intent.putExtra(CheckoutWebViewActivity.EXTRA_ORDER_ID, checkout.orderId)
        intent.putExtra(CheckoutWebViewActivity.EXTRA_AMOUNT, checkout.amount)
        
        activity.startActivity(intent)
    }
}

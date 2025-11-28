package com.corvuspay.sdk

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Test/Mock Checkout Activity for CorvusPay
 * This simulates the CorvusPay checkout UI
 * In production, this would be the real CorvusPay activity from their SDK
 */
class CheckoutActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORE_ID = "storeId"
        const val EXTRA_ORDER_ID = "orderId"
        const val EXTRA_AMOUNT = "amount"
        const val EXTRA_CURRENCY = "currency"
        const val EXTRA_CART = "cart"
        const val EXTRA_LANGUAGE = "language"
        
        const val RESULT_SUCCESS = 1
        const val RESULT_CANCELED = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val storeId = intent.getIntExtra(EXTRA_STORE_ID, 0)
        val orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: ""
        val amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0)
        val currency = intent.getStringExtra(EXTRA_CURRENCY) ?: "EUR"
        val cart = intent.getStringExtra(EXTRA_CART) ?: ""
        val language = intent.getStringExtra(EXTRA_LANGUAGE) ?: "EN"

        // Create UI programmatically
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setPadding(20, 20, 20, 20)

        // Title
        val title = TextView(this)
        title.text = "CorvusPay Checkout (Test)"
        title.textSize = 20f
        title.setTextColor(android.graphics.Color.BLACK)
        val titleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleParams.bottomMargin = 20
        mainLayout.addView(title, titleParams)

        // Order details
        val detailsText = """
            Store ID: $storeId
            Order ID: $orderId
            Amount: $${"%.2f".format(amount)} $currency
            Cart: $cart
            Language: $language
            
            ⚠️ This is a TEST checkout screen
            In production, this would be the real CorvusPay UI
            
            Test Cards:
            • 4111 1111 1111 1111 (VISA - Success)
            • 5555 5555 5555 4444 (MASTER - Success)
            • 3782 822463 10005 (AMEX - Success)
            
            CVC: Any 3 digits
            Expiry: Any future date
        """.trimIndent()

        val details = TextView(this)
        details.text = detailsText
        details.textSize = 14f
        details.setTextColor(android.graphics.Color.DKGRAY)
        details.setLineSpacing(8f, 1f)
        val detailsParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        detailsParams.bottomMargin = 20
        mainLayout.addView(details, detailsParams)

        // Success button
        val successBtn = Button(this)
        successBtn.text = "✅ Complete Payment (Test)"
        successBtn.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        successBtn.setTextColor(android.graphics.Color.WHITE)
        val successParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        successParams.bottomMargin = 10
        successBtn.setOnClickListener {
            setResult(RESULT_SUCCESS)
            finish()
        }
        mainLayout.addView(successBtn, successParams)

        // Cancel button
        val cancelBtn = Button(this)
        cancelBtn.text = "❌ Cancel"
        cancelBtn.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
        cancelBtn.setTextColor(android.graphics.Color.WHITE)
        cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        mainLayout.addView(cancelBtn, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
}

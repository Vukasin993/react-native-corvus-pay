package com.corvuspay.sdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

/**
 * WebView-based Checkout Activity for CorvusPay
 * Simulates the real CorvusPay checkout UI using WebView
 * In production, this would be replaced with the real CorvusPay Activity
 */
class CheckoutWebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHECKOUT_URL = "checkoutUrl"
        const val EXTRA_STORE_ID = "storeId"
        const val EXTRA_ORDER_ID = "orderId"
        const val EXTRA_AMOUNT = "amount"
        const val EXTRA_SIGNATURE = "signature"
        
        const val RESULT_SUCCESS = 1
        const val RESULT_CANCELED = 0
    }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storeId = intent.getIntExtra(EXTRA_STORE_ID, 0)
        val orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: ""
        val amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0)
        val signature = intent.getStringExtra(EXTRA_SIGNATURE) ?: ""
        val checkoutUrl = intent.getStringExtra(EXTRA_CHECKOUT_URL) ?: ""

        Log.d("CheckoutWebView", "Checkout WebView - Store: $storeId, Order: $orderId, Amount: $amount")

        // Create WebView
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("CheckoutWebView", "Page loaded: $url")
            }
        }

        val layout = LinearLayout(this)
        layout.addView(webView)
        setContentView(layout)

        // If real checkout URL provided, load it
        if (checkoutUrl.isNotEmpty()) {
            webView.loadUrl(checkoutUrl)
        } else {
            // Load mock HTML checkout form
            loadMockCheckoutUI(storeId, orderId, amount, signature)
        }
    }

    private fun loadMockCheckoutUI(storeId: Int, orderId: String, amount: Double, signature: String) {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>CorvusPay Checkout</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }
                    .container {
                        background: white;
                        border-radius: 12px;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                        max-width: 500px;
                        width: 100%;
                        padding: 40px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #667eea;
                        margin-bottom: 10px;
                    }
                    .order-info {
                        background: #f8f9fa;
                        padding: 15px;
                        border-radius: 8px;
                        margin-bottom: 25px;
                        font-size: 14px;
                        color: #555;
                    }
                    .order-info p {
                        margin: 8px 0;
                        display: flex;
                        justify-content: space-between;
                    }
                    .order-info .amount {
                        font-size: 20px;
                        font-weight: bold;
                        color: #667eea;
                    }
                    .form-group {
                        margin-bottom: 20px;
                    }
                    label {
                        display: block;
                        margin-bottom: 8px;
                        font-weight: 500;
                        color: #333;
                        font-size: 14px;
                    }
                    input[type="text"],
                    input[type="email"],
                    input[type="tel"],
                    select {
                        width: 100%;
                        padding: 12px;
                        border: 1px solid #ddd;
                        border-radius: 6px;
                        font-size: 14px;
                        font-family: inherit;
                        transition: border-color 0.3s;
                    }
                    input:focus,
                    select:focus {
                        outline: none;
                        border-color: #667eea;
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    }
                    .row {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 15px;
                    }
                    .test-cards {
                        background: #f0f7ff;
                        border: 1px solid #b3d9ff;
                        border-radius: 8px;
                        padding: 15px;
                        margin-bottom: 25px;
                        font-size: 13px;
                    }
                    .test-cards h4 {
                        margin-bottom: 10px;
                        color: #0066cc;
                    }
                    .test-card {
                        margin: 8px 0;
                        font-family: monospace;
                        color: #333;
                    }
                    .button {
                        width: 100%;
                        padding: 14px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        border: none;
                        border-radius: 6px;
                        font-size: 16px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: transform 0.2s, box-shadow 0.2s;
                        margin-top: 10px;
                    }
                    .button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
                    }
                    .button:active {
                        transform: translateY(0);
                    }
                    .button-cancel {
                        background: #6c757d;
                    }
                    .button-cancel:hover {
                        box-shadow: 0 10px 20px rgba(108, 117, 125, 0.3);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">💳 CorvusPay</div>
                        <p style="color: #999; font-size: 14px;">Secure Payment Gateway</p>
                    </div>

                    <div class="order-info">
                        <p>
                            <span>Store:</span>
                            <strong>#$storeId</strong>
                        </p>
                        <p>
                            <span>Order:</span>
                            <strong>$orderId</strong>
                        </p>
                        <p class="amount">
                            <span>Total:</span>
                            <span>${"%.2f".format(amount)}</span>
                        </p>
                    </div>

                    <div class="test-cards">
                        <h4>🧪 Test Mode - Use Test Cards</h4>
                        <div class="test-card">💳 VISA: 4111 1111 1111 1111</div>
                        <div class="test-card">💳 MASTER: 5555 5555 5555 4444</div>
                        <div class="test-card">💳 AMEX: 3782 822463 10005</div>
                        <div class="test-card">📅 Any future date | 🔐 Any 3 digits</div>
                    </div>

                    <form id="checkoutForm" onsubmit="handleSubmit(event)">
                        <div class="form-group">
                            <label>Card Number</label>
                            <input type="text" id="cardNumber" placeholder="4111 1111 1111 1111" 
                                   pattern="[0-9 ]*" maxlength="19" required>
                        </div>

                        <div class="row">
                            <div class="form-group">
                                <label>Expiry (MM/YY)</label>
                                <input type="text" id="expiry" placeholder="12/25" 
                                       pattern="[0-9/]*" maxlength="5" required>
                            </div>
                            <div class="form-group">
                                <label>CVC</label>
                                <input type="text" id="cvc" placeholder="123" 
                                       pattern="[0-9]*" maxlength="4" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Cardholder Name</label>
                            <input type="text" id="cardholderName" placeholder="John Doe" required>
                        </div>

                        <button type="button" class="button" onclick="completePayment()">
                            ✅ Complete Payment - ${"%.2f".format(amount)}
                        </button>
                        <button type="button" class="button button-cancel" onclick="cancelPayment()">
                            ❌ Cancel
                        </button>
                    </form>
                </div>

                <script>
                    function completePayment() {
                        const form = document.getElementById('checkoutForm');
                        if (!form.checkValidity()) {
                            alert('Please fill in all fields correctly');
                            return;
                        }

                        // In a real scenario, this would send data to server
                        // For now, we'll just close and return success
                        if (window.Android) {
                            window.Android.paymentSuccess(
                                JSON.stringify({
                                    orderId: '$orderId',
                                    transactionId: 'TXN_' + Math.random().toString(36).substr(2, 9).toUpperCase(),
                                    amount: $amount,
                                    status: 'SUCCESS',
                                    timestamp: new Date().toISOString()
                                })
                            );
                        }
                    }

                    function cancelPayment() {
                        if (window.Android) {
                            window.Android.paymentCancelled();
                        }
                    }

                    // Format card number input
                    document.getElementById('cardNumber').addEventListener('input', function(e) {
                        let value = e.target.value.replace(/\s/g, '');
                        let formatted = value.replace(/(\d{4})/g, '$1 ').trim();
                        e.target.value = formatted;
                    });

                    // Format expiry input
                    document.getElementById('expiry').addEventListener('input', function(e) {
                        let value = e.target.value.replace(/\D/g, '');
                        if (value.length >= 2) {
                            value = value.slice(0, 2) + '/' + value.slice(2, 4);
                        }
                        e.target.value = value;
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }

    // JavaScript interface for WebView
    class JavaScriptInterface(private val activity: CheckoutWebViewActivity) {
        @android.webkit.JavascriptInterface
        fun paymentSuccess(resultJson: String) {
            Log.d("CheckoutWebView", "Payment success: $resultJson")
            activity.setResult(RESULT_SUCCESS)
            activity.finish()
        }

        @android.webkit.JavascriptInterface
        fun paymentCancelled() {
            Log.d("CheckoutWebView", "Payment cancelled")
            activity.setResult(RESULT_CANCELED)
            activity.finish()
        }
    }
}

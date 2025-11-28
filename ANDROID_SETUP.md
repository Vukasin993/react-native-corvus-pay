# CorvusPay Payment Module - Android Setup & Integration

## File Structure

All payment-related files are now organized in the SDK:

```
android/src/main/java/com/margelo/nitro/corvuspay/
├── PaymentModule.kt              # Main React Native module
├── PaymentPackage.kt             # React package registration
├── CheckoutBuilder.kt            # Checkout parameter builder
├── PaymentUtils.kt               # Utility functions
├── EncryptionHelper.kt           # HMAC-SHA256 signing

android/src/main/java/com/corvuspay/sdk/
├── CorvusPay.kt                  # SDK launcher
├── enums/
│   ├── Language.kt
│   ├── Currency.kt
│   └── CardType.kt
├── models/
│   ├── Checkout.kt
│   ├── Cardholder.kt
│   ├── InstallmentParams.kt
│   ├── DynamicInstallmentParams.kt
│   └── InstallmentsMap.kt
└── constants/
    └── CheckoutCodes.kt
```

## Setup on New Project

### 1. Copy PaymentModule Files

All required Kotlin files are in `android/src/main/java/com/margelo/nitro/corvuspay/`:

```bash
# The entire directory is ready to use
cp -r android/src/main/java/com/margelo/nitro/corvuspay/* \
  your-project/android/app/src/main/java/com/your/company/payments/
```

### 2. Register PaymentPackage

In `MainApplication.kt`:

```kotlin
import com.your.company.payments.PaymentPackage

class MainApplication : Application(), ReactApplication {
    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages.apply {
                    add(PaymentPackage())  // ← Add this line
                }
            // ... rest of configuration
        }
}
```

### 3. Usage from React Native Code

```typescript
import { PaymentModule, CheckoutParams } from './payments';
import { NativeModules } from 'react-native';

const { PaymentModule: PM } = NativeModules;

async function startCheckout() {
  try {
    PM.configureEnvironment('test');

    const params: CheckoutParams = {
      storeId: 12345,
      orderId: 'ORDER-123',
      cart: 'Product x 1',
      language: 'EN',
      currency: 'EUR',
      amount: 100.0,
    };

    // Option 1: Server-signed
    const signature = await getSignatureFromServer(params);
    const result = await PM.checkoutWithSignature(params, signature);

    // Option 2: Client-signed (less secure!)
    const result = await PM.checkoutWithSecret(params, 'your-secret-key');

    console.log('Payment successful:', result);
  } catch (error) {
    console.error('Payment failed:', error);
  }
}
```

## Customization for Other Projects

### Change Package Name

If you use a different package, replace:
- `com.margelo.nitro.corvuspay` with your package
- `com.corvuspay.sdk` can remain the same for SDK libraries

```bash
# Example for moving to your package
find . -type f -name "*.kt" -exec sed -i '' 's/com\.margelo\.nitro\.corvuspay/com.your.company.payments/g' {} \;
```

### Customize PaymentModule Constants

In `PaymentModule.kt`:

```kotlin
companion object {
    const val NAME = "PaymentModule" // Customize if needed
    const val ENV_TEST = "test"
    const val ENV_PRODUCTION = "production"
    
    const val IS_SDK_FLAG = true
    const val CHECKOUT_VERSION_STR = "1.3"
    const val SDK_VERSION_NUM = 1.3
}
```

### Customize CheckoutBuilder

If your CorvusPay SDK has different field names:

```kotlin
// In CheckoutBuilder.kt, adjust the mapping:
fun buildCheckoutFromParams(params: ReadableMap, signature: String): Checkout {
    val storeId = params.getInt("storeId") // Customize key if needed
    // ... rest
}
```

## Using Encryption

The module uses HMAC-SHA256 for signing:

```kotlin
// In EncryptionHelper.kt
val signature = EncryptionHelper.generateHashWithHmac256(
    stringToBeSigned = "storeId|orderId|amount|currency",
    key = "your-secret-key"
)
```

## Testing

### Android Studio

```bash
cd android
./gradlew :app:build

# or with verbose options
./gradlew :app:build --info
```

### React Native CLI

```bash
react-native run-android

# or
yarn android
```

## Payment Features

The module supports:

- ✅ Different card types (VISA, MASTER, MAESTRO, AMEX, etc.)
- ✅ Different currencies (EUR, USD, GBP, etc.)
- ✅ Different languages (EN, DE, FR, etc.)
- ✅ Installments (fixed, flexible, dynamic per card type)
- ✅ Installment discounts per card
- ✅ Saved cards (Card Profiles)
- ✅ Cardholder information
- ✅ SEPA/Direct Debit integration

## Security Notes

1. **Never share secret key with frontend code** in production
2. Use server-side signing (option 1) for production
3. Validate all transactions on the backend
4. Use HTTPS for all communications
5. Implement proper error handling

## Troubleshooting

### Build Errors

If you get errors during build:

```bash
# Clean cache
rm -rf android/build
rm -rf node_modules

# Regenerate nitrogen files
yarn prepare

# Try build again
yarn android
```

### Runtime Errors

If PaymentModule is not available:

```typescript
// Check if it's registered
import { NativeModules } from 'react-native';
console.log(Object.keys(NativeModules)); // Should see PaymentModule
```

## Next Steps

1. Integrate CorvusPay SDK (waiting for provider access)
2. Update `CorvusPay.kt` with real implementation
3. Implement server-side signing on backend
4. Test with test credentials
5. Move to production

## Additional Resources

- `PAYMENT_MODULE.md` - Detailed API documentation
- `PaymentModule.ts` - TypeScript interfaces
- `example/src/App.tsx` - Complete example application

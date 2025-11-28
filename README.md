# react-native-corvus-pay

React Native payment module for fast and secure integration of the CorvusPay Internet Payment Gateway into mobile applications (iOS and Android).

yarn add react-native-corvus-pay react-native-nitro-modules
## Installation

```sh
npm install react-native-corvus-pay react-native-nitro-modules
# or
yarn add react-native-corvus-pay react-native-nitro-modules
```

> `react-native-nitro-modules` is required because this library is based on [Nitro Modules](https://nitro.margelo.com/).

## Setup

### Android

In `MainApplication.kt` add `PaymentPackage`:

```kotlin
import com.margelo.nitro.corvuspay.PaymentPackage

class MainApplication : Application(), ReactApplication {
  override val reactNativeHost: ReactNativeHost =
    object : DefaultReactNativeHost(this) {
      override fun getPackages(): List<ReactPackage> =
        PackageList(this).packages.apply {
          add(PaymentPackage())
        }
    }
}
```

### iOS

Run `pod install` in the `ios/` directory.

## Usage

```typescript
import { PaymentModule } from 'react-native-corvus-pay';

export default function PaymentScreen() {
  const handleCheckout = async () => {
    try {
      // Configure environment
      PaymentModule.configureEnvironment('test'); // or 'production'

      // Prepare checkout parameters
      const checkoutParams = {
        storeId: 12345,
        orderId: 'ORDER-2025-001',
        cart: 'iPhone 15 x 1',
        language: 'EN',
        currency: 'EUR',
        amount: 999.99,
        requireComplete: false,
      };

      // Use secret key (client-side signing)
      const result = await PaymentModule.checkoutWithSecret(
        checkoutParams,
        'your-secret-key'
      );

      if (result.status === 'success') {
        console.log('Payment successful!');
        // Update backend to confirm payment
      }
    } catch (error) {
      console.error('Payment error:', error);
    }
  };

  return <Button title="Pay" onPress={handleCheckout} />;
}
```

## API

### `PaymentModule.configureEnvironment(env: 'test' | 'production')`

Configures the environment for checkout.

```typescript
PaymentModule.configureEnvironment('test');
```

### `PaymentModule.checkoutWithSecret(params: CheckoutParams, secretKey: string): Promise<CheckoutResult>`

Opens checkout with client-side HMAC-SHA256 signing (faster, but less secure).

```typescript
const result = await PaymentModule.checkoutWithSecret(checkoutParams, secretKey);
```

### `PaymentModule.checkoutWithSignature(params: CheckoutParams, signature: string): Promise<CheckoutResult>`

Opens checkout with server-side signing (recommended for production).

```typescript
const signature = await fetchSignatureFromBackend(checkoutParams);
const result = await PaymentModule.checkoutWithSignature(checkoutParams, signature);
```

## CheckoutParams

```typescript
interface CheckoutParams {
  // REQUIRED
  storeId: number;          // Store ID
  orderId: string;          // Unique order ID
  cart: string;             // Product description
  language?: string;        // 'EN', 'DE', 'FR', etc.
  currency?: string;        // 'EUR', 'USD', 'GBP', etc.
  amount: number;           // Amount in decimals

  // OPTIONAL
  requireComplete?: boolean;
  bestBefore?: number;      // Epoch seconds
  discountAmount?: number;
  voucherAmount?: number;
  useCardProfiles?: boolean;
  userCardProfilesId?: string;

  // Cardholder info
  cardholder?: {
    firstName?: string;
    lastName?: string;
    email?: string;
    phone?: string;
    address?: string;
    city?: string;
    country?: string;
    countryCode?: string;
    zip?: string;
  };

  // Installment (only one type)
  installmentParams?: {
    numberOfInstallments?: number; // Fixed
    paymentAll?: { enabled: boolean; min: number; max: number }; // Flexible
    paymentAllDynamic?: { /* ... */ }; // Per card
  };
}
```

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)

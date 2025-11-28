# react-native-corvus-pay

React Native payment module za brzu i sigurnu integraciju CorvusPay Internet Payment Gatewaya u mobilne aplikacije (iOS i Android).

## Instalacija

```sh
npm install react-native-corvus-pay react-native-nitro-modules
# ili
yarn add react-native-corvus-pay react-native-nitro-modules
```

> `react-native-nitro-modules` je obavezan jer se biblioteka bazira na [Nitro Modules](https://nitro.margelo.com/).

## Setup

### Android

U `MainApplication.kt` dodaj `PaymentPackage`:

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

Pokreni `pod install` u `ios/` direktorijumu.

## Korišćenje

```typescript
import { PaymentModule } from 'react-native-corvus-pay';

export default function PaymentScreen() {
  const handleCheckout = async () => {
    try {
      // Konfiguruj okruženje
      PaymentModule.configureEnvironment('test'); // ili 'production'

      // Pripremi checkout parametre
      const checkoutParams = {
        storeId: 12345,
        orderId: 'ORDER-2025-001',
        cart: 'iPhone 15 x 1',
        language: 'EN',
        currency: 'EUR',
        amount: 999.99,
        requireComplete: false,
      };

      // Koristi secret key (client-side signing)
      const result = await PaymentModule.checkoutWithSecret(
        checkoutParams,
        'your-secret-key'
      );

      if (result.status === 'success') {
        console.log('Plaćanje uspešno!');
        // Ažuriraj backend da potvrdi plaćanje
      }
    } catch (error) {
      console.error('Greška pri plaćanju:', error);
    }
  };

  return <Button title="Plati" onPress={handleCheckout} />;
}
```

## API

### `PaymentModule.configureEnvironment(env: 'test' | 'production')`

Konfigurira okruženje za checkout.

```typescript
PaymentModule.configureEnvironment('test');
```

### `PaymentModule.checkoutWithSecret(params: CheckoutParams, secretKey: string): Promise<CheckoutResult>`

Otvara checkout sa client-side HMAC-SHA256 potpisivanjem (brže, ali manje sigurno).

```typescript
const result = await PaymentModule.checkoutWithSecret(checkoutParams, secretKey);
```

### `PaymentModule.checkoutWithSignature(params: CheckoutParams, signature: string): Promise<CheckoutResult>`

Otvara checkout sa server-side potpisivanjem (preporučeno za produkciju).

```typescript
const signature = await fetchSignatureFromBackend(checkoutParams);
const result = await PaymentModule.checkoutWithSignature(checkoutParams, signature);
```

## CheckoutParams

```typescript
interface CheckoutParams {
  // OBAVEZNI
  storeId: number;          // ID prodavnice
  orderId: string;          // Jedinstveni ID porudžbine
  cart: string;             // Opis proizvoda
  language?: string;        // 'EN', 'DE', 'FR', itd.
  currency?: string;        // 'EUR', 'USD', 'GBP', itd.
  amount: number;           // Iznos u decimalima

  // OPCIONI
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

  // Rate (samo jedan oblik)
  installmentParams?: {
    numberOfInstallments?: number; // Fiksno
    paymentAll?: { enabled: boolean; min: number; max: number }; // Fleksibilno
    paymentAllDynamic?: { /* ... */ }; // Po kartici
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

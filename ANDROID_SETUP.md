# CorvusPay Payment Module - Android Setup & Integracija

## Struktura Fajlova

Svi payment-related fajlovi su sada organizovani u SDK-u:

```
android/src/main/java/com/margelo/nitro/corvuspay/
├── PaymentModule.kt              # Glavna React Native  modul
├── PaymentPackage.kt             # React package registracija
├── CheckoutBuilder.kt            # Checkout parametar builder
├── PaymentUtils.kt               # Utility funkcije
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

## Setup na Novom Projektu

### 1. Kopiranja PaymentModule fajlova

Svi potrebni Kotlin fajlovi su u `android/src/main/java/com/margelo/nitro/corvuspay/`:

```bash
# Čitav direktorijum je spreman za korišćenje
cp -r android/src/main/java/com/margelo/nitro/corvuspay/* \
  tvoj-projekt/android/app/src/main/java/com/tvoja/kompanija/payments/
```

### 2. Registracija PaymentPackage

U `MainApplication.kt`:

```kotlin
import com.tvoja.kompanija.payments.PaymentPackage

class MainApplication : Application(), ReactApplication {
    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages.apply {
                    add(PaymentPackage())  // ← Dodaj ovu liniju
                }
            // ... ostatak konfiguracije
        }
}
```

### 3. Korišćenje iz React Native koda

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

    // Option 2: Client-signed (manje sigurno!)
    const result = await PM.checkoutWithSecret(params, 'your-secret-key');

    console.log('Payment successful:', result);
  } catch (error) {
    console.error('Payment failed:', error);
  }
}
```

## Prilagođavanje za Drugih Projekte

### Promeniti paket ime

Ako koristiš drugačiji paket, zameni:
- `com.margelo.nitro.corvuspay` sa tvojim paketom
- `com.corvuspay.sdk` možeš zadržati isti za SDK biblioteke

```bash
# Primer za premeštanje u svoj paket
find . -type f -name "*.kt" -exec sed -i '' 's/com\.margelo\.nitro\.corvuspay/com.tvoja.kompanija.payments/g' {} \;
```

### Prilagođavanje PaymentModule konstanti

U `PaymentModule.kt`:

```kotlin
companion object {
    const val NAME = "PaymentModule" // Prilagodi ako trebaš drugi naziv
    const val ENV_TEST = "test"
    const val ENV_PRODUCTION = "production"
    
    const val IS_SDK_FLAG = true
    const val CHECKOUT_VERSION_STR = "1.3"
    const val SDK_VERSION_NUM = 1.3
}
```

### Prilagođavanje CheckoutBuilder

Ako tvoj CorvusPay SDK ima drugačije nazive polja:

```kotlin
// U CheckoutBuilder.kt, prilagodi mapiranje:
fun buildCheckoutFromParams(params: ReadableMap, signature: String): Checkout {
    val storeId = params.getInt("storeId") // Prilagodi ključ ako trebaš
    // ... ostatak
}
```

## Korišćenje Encryptiona

Modul koristi HMAC-SHA256 za potpisivanje:

```kotlin
// U EncryptionHelper.kt
val signature = EncryptionHelper.generateHashWithHmac256(
    stringToBeSigned = "storeId|orderId|amount|currency",
    key = "your-secret-key"
)
```

## Testiranje

### Android Studio

```bash
cd android
./gradlew :app:build

# ili sa verbose opcijama
./gradlew :app:build --info
```

### React Native CLI

```bash
react-native run-android

# ili
yarn android
```

## Mogućnosti Plaćanja

Modul podržava:

- ✅ Različite vrste kartica (VISA, MASTER, MAESTRO, AMEX, itd.)
- ✅ Različite valute (EUR, USD, GBP, itd.)
- ✅ Različite jezike (EN, DE, FR, itd.)
- ✅ Rate (fiksne, fleksibilne, dinamske po kartici)
- ✅ Instalmen popuste per kartica
- ✅ Sačuvane kartice (Card Profiles)
- ✅ Cardholder informacije
- ✅ SEPA/Direct Debit integracija

## Sigurnosne Napomene

1. **Nikada ne deli secret ključ sa frontend kodom** u produkciji
2. Koristiti server-side signing (opcija 1) za produkciju
3. Validate sve transakcije na backendu
4. Koristi HTTPS za sve komunikacije
5. Implementiraj proper error handling

## Troubleshooting

### Build greške

Ako dobijaš greške tokom build-a:

```bash
# Očisti cache
rm -rf android/build
rm -rf node_modules

# Regeneriši nitrogen files
yarn prepare

# Pokušaj build ponovo
yarn android
```

### Runtime greške

Ako PaymentModule nije dostupan:

```typescript
// Proveri da li je registrovan
import { NativeModules } from 'react-native';
console.log(Object.keys(NativeModules)); // Trebalo bi da vidiš PaymentModule
```

## Next Steps

1. Integruj CorvusPay SDK (čeka se dostupa od tvog provajdera)
2. Update `CorvusPay.kt` sa pravom implementacijom
3. Implementiraj server-side signing na backendu
4. Testiraj sa test kredencijalni
5. Pomeri na produkciju

## Dodatne Resurse

- `PAYMENT_MODULE.md` - Detaljno API dokumentacija
- `PaymentModule.ts` - TypeScript interfejsi
- `example/src/App.tsx` - Kompletan primer aplikacije

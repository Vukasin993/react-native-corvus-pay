# CorvusPay Payment Module

Integracija CorvusPay Payment Gateway-a u React Native aplikacije. Modul omogućava brzu i sigurnu obradu plaćanja sa podrškom za razne kartice, rate, i napredne opcije.

## Instalacija

Modul je već integriram u SDK, trebate samo da ga koristite kroz `PaymentModule`.

## Osnovna Upotreba

### 1. Konfiguracija Okruženja

```typescript
import { PaymentModule } from 'react-native-corvus-pay';

// Postavi test okruženje
PaymentModule.configureEnvironment('test');

// Ili produkcijsko okruženje
PaymentModule.configureEnvironment('production');
```

### 2. Osnovna Checkout Transakcija

#### Opcija A: Sa Signaturom (Preporučeno - Backend Signing)

```typescript
const checkoutParams = {
  storeId: 12345,
  orderId: 'ORDER-2025-00001',
  cart: 'iPhone 15 x 1; AirPods Pro x 2',
  language: 'EN',
  currency: 'EUR',
  amount: 1299.97,
};

// Prati tvoj backend da generiše signature
const signature = await fetchSignatureFromBackend(checkoutParams);

try {
  const result = await PaymentModule.checkoutWithSignature(
    checkoutParams,
    signature
  );
  console.log('Checkout successful:', result);
} catch (error) {
  console.error('Checkout failed:', error);
}
```

#### Opcija B: Sa Secret Ključem (Client-Side Signing)

```typescript
const checkoutParams = {
  storeId: 12345,
  orderId: 'ORDER-2025-00001',
  cart: 'Product x 1',
  language: 'EN',
  currency: 'EUR',
  amount: 100.0,
};

try {
  const result = await PaymentModule.checkoutWithSecret(
    checkoutParams,
    'your-secret-key-here'
  );
  console.log('Checkout successful:', result);
} catch (error) {
  console.error('Checkout failed:', error);
}
```

## Napredne Opcije

### Cardholder Informacije

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  cardholder: {
    address: 'Bulevar Oslobođenja 1',
    city: 'Novi Sad',
    country: 'Serbia',
    countryCode: 'RS',
    email: 'user@example.com',
    firstName: 'Petar',
    lastName: 'Petrović',
    phone: '+381601234567',
    zip: '21000',
  },
};
```

### Installment Opcije

#### Fiksni Broj Rata

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  installmentParams: {
    numberOfInstallments: 6, // Fiksnih 6 rata
  },
};
```

#### Payment All (Fleksibilne Rate)

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  installmentParams: {
    paymentAll: {
      enabled: true,
      min: 2,  // Minimalno 2 rate
      max: 12, // Maksimalno 12 rata
    },
  },
};
```

#### Dinamske Rate Po Kartici

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  installmentParams: {
    paymentAllDynamic: {
      paymentVisa: { enabled: true, min: 2, max: 12 },
      paymentMaster: { enabled: true, min: 2, max: 12 },
      paymentMaestro: { enabled: true, min: 2, max: 12 },
      paymentAmex: { enabled: true, min: 3, max: 6 },
      paymentDiners: { enabled: true, min: 2, max: 6 },
      paymentDiscover: { enabled: false, min: 0, max: 0 },
      paymentDina: { enabled: false, min: 0, max: 0 },
      paymentJcb: { enabled: false, min: 0, max: 0 },
    },
  },
};
```

### Installments Map (Sa Popustima)

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  installmentsMap: {
    items: [
      {
        cardType: 'VISA',
        discounts: [
          {
            numberOfInstallments: 1,
            amount: 100.0,
            discountedAmount: 100.0,
          },
          {
            numberOfInstallments: 2,
            amount: 100.0,
            discountedAmount: 99.0, // 1% popust
          },
          {
            numberOfInstallments: 6,
            amount: 100.0,
            discountedAmount: 97.0, // 3% popust
          },
        ],
      },
      {
        cardType: 'MASTER',
        discounts: [
          {
            numberOfInstallments: 1,
            amount: 100.0,
            discountedAmount: 100.0,
          },
          {
            numberOfInstallments: 3,
            amount: 100.0,
            discountedAmount: 98.5,
          },
        ],
      },
    ],
  },
};
```

### Card Profiles (Sačuvane Kartice)

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  useCardProfiles: true,
  userCardProfilesId: 'user_abc_01', // ID korisnika za sačuvane kartice
};
```

### Dodatne Opcije

```typescript
const checkoutParams = {
  // ... obavezna polja ...
  
  // Zahtevaj kompletnu uplatu
  requireComplete: true,
  
  // Vreme isticanja (epoch sekunde, max 900s u budućnosti)
  bestBefore: Math.floor(Date.now() / 1000) + 600,
  
  // Popust na iznos
  discountAmount: 10.0,
  
  // Voucher iznos
  voucherAmount: 5.0,
  
  // Skrivanje određenih tabova
  hideTab: 'WALLET', // Skriva jedan tab
  hideTabs: 'WALLET,CARDS', // Skriva više tabova
  
  // SEPA/Direktni debit opcije
  creditorReference: 'RF18539007547034',
  debtorIban: 'RS35160005001061195179',
  
  // Shop Account ID
  shopAccountId: 'shop-001',
};
```

## Kompletan Primer

```typescript
import React, { useState } from 'react';
import { View, Button, Alert } from 'react-native';
import { PaymentModule, CheckoutParams } from 'react-native-corvus-pay';

export default function CheckoutScreen() {
  const [loading, setLoading] = useState(false);

  const handleCheckout = async () => {
    setLoading(true);
    try {
      PaymentModule.configureEnvironment('test');

      const params: CheckoutParams = {
        storeId: 12345,
        orderId: `ORDER-${Date.now()}`,
        cart: 'Premium Subscription x 1',
        language: 'EN',
        currency: 'EUR',
        amount: 99.99,
        cardholder: {
          email: 'customer@example.com',
          firstName: 'John',
          lastName: 'Doe',
        },
        installmentParams: {
          paymentAll: {
            enabled: true,
            min: 2,
            max: 12,
          },
        },
      };

      const result = await PaymentModule.checkoutWithSecret(
        params,
        'your-secret-key'
      );

      Alert.alert('Success', 'Payment completed');
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Button
        title={loading ? 'Processing...' : 'Pay Now'}
        onPress={handleCheckout}
        disabled={loading}
      />
    </View>
  );
}
```

## Greške i Hendlovanje

### Moguće Greške

- `NO_ACTIVITY` - Aktivnost nije dostupna
- `ALREADY_RUNNING` - Još jedan checkout je u toku
- `CHECKOUT_INIT_ERROR` - Greška pri inicijalizaciji
- `SIGNING_ERROR` - Greška pri potpisivanju
- `CHECKOUT_ABORTED` - Korisnik je otkazao transakciju
- `CHECKOUT_FAILURE` - Checkout je neuspešan
- `RESULT_PARSE_ERROR` - Greška pri parsiranju rezultata

```typescript
try {
  const result = await PaymentModule.checkoutWithSecret(params, secretKey);
} catch (error: any) {
  if (error.code === 'CHECKOUT_ABORTED') {
    console.log('User cancelled checkout');
  } else if (error.code === 'CHECKOUT_FAILURE') {
    console.log('Payment failed:', error.message);
  } else {
    console.log('Unknown error:', error);
  }
}
```

## Okruženja

- `test` - Test okruženje (za razvoj i testiranje)
- `production` - Produkcijsko okruženje (za live transakcije)

## Zahtevani Parametri

- `storeId`: Tvoj CorvusPay store ID
- `orderId`: Jedinstveni ID narudžbine u tvojoj aplikaciji
- `cart`: Opis proizvoda/usluga
- `language`: Jezikod plaćanja ('EN', 'DE', itd.)
- `currency`: Valuta ('EUR', 'USD', itd.)
- `amount`: Iznos u decimalnom formatu

## Sigurnost

- Nikada ne deli tvoj secret ključ sa frontend klijentima
- Uvek validuj transakcije na svom backend serveru
- Koristi HTTPS za sve komunikacije
- Implementiraj server-to-server verifikaciju statusa

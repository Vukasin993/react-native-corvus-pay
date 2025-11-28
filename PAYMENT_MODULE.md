# CorvusPay Payment Module

Integration of CorvusPay Payment Gateway into React Native applications. The module enables fast and secure payment processing with support for various card types, installments, and advanced options.

## Installation

The module is already integrated in the SDK, you just need to use it through `PaymentModule`.

## Basic Usage

### 1. Configure Environment

```typescript
import { PaymentModule } from 'react-native-corvus-pay';

// Set test environment
PaymentModule.configureEnvironment('test');

// Or production environment
PaymentModule.configureEnvironment('production');
```

### 2. Basic Checkout Transaction

#### Option A: With Signature (Recommended - Backend Signing)

```typescript
const checkoutParams = {
  storeId: 12345,
  orderId: 'ORDER-2025-00001',
  cart: 'iPhone 15 x 1; AirPods Pro x 2',
  language: 'EN',
  currency: 'EUR',
  amount: 1299.97,
};

// Ask your backend to generate signature
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

#### Option B: With Secret Key (Client-Side Signing)

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

## Advanced Options

### Cardholder Information

```typescript
const checkoutParams = {
  // ... required fields ...
  cardholder: {
    address: 'Main Street 1',
    city: 'New York',
    country: 'United States',
    countryCode: 'US',
    email: 'user@example.com',
    firstName: 'John',
    lastName: 'Doe',
    phone: '+1234567890',
    zip: '10001',
  },
};
```

### Installment Options

#### Fixed Number of Installments

```typescript
const checkoutParams = {
  // ... required fields ...
  installmentParams: {
    numberOfInstallments: 6, // Fixed 6 installments
  },
};
```

#### Payment All (Flexible Installments)

```typescript
const checkoutParams = {
  // ... required fields ...
  installmentParams: {
    paymentAll: {
      enabled: true,
      min: 2,  // Minimum 2 installments
      max: 12, // Maximum 12 installments
    },
  },
};
```

#### Dynamic Installments Per Card Type

```typescript
const checkoutParams = {
  // ... required fields ...
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

### Installments Map (With Discounts)

```typescript
const checkoutParams = {
  // ... required fields ...
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
            discountedAmount: 99.0, // 1% discount
          },
          {
            numberOfInstallments: 6,
            amount: 100.0,
            discountedAmount: 97.0, // 3% discount
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

### Card Profiles (Saved Cards)

```typescript
const checkoutParams = {
  // ... required fields ...
  useCardProfiles: true,
  userCardProfilesId: 'user_abc_01', // User ID for saved cards
};
```

### Additional Options

```typescript
const checkoutParams = {
  // ... required fields ...
  
  // Require complete payment
  requireComplete: true,
  
  // Expiration time (epoch seconds, max 900s in future)
  bestBefore: Math.floor(Date.now() / 1000) + 600,
  
  // Discount on amount
  discountAmount: 10.0,
  
  // Voucher amount
  voucherAmount: 5.0,
  
  // Hide specific tabs
  hideTab: 'WALLET', // Hides one tab
  hideTabs: 'WALLET,CARDS', // Hides multiple tabs
  
  // SEPA/Direct Debit options
  creditorReference: 'RF18539007547034',
  debtorIban: 'RS35160005001061195179',
  
  // Shop Account ID
  shopAccountId: 'shop-001',
};
```

## Complete Example

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

## Error Handling

### Possible Errors

- `NO_ACTIVITY` - Activity is not available
- `ALREADY_RUNNING` - Another checkout is in progress
- `CHECKOUT_INIT_ERROR` - Error during initialization
- `SIGNING_ERROR` - Error during signing
- `CHECKOUT_ABORTED` - User cancelled transaction
- `CHECKOUT_FAILURE` - Checkout failed
- `RESULT_PARSE_ERROR` - Error parsing result

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

## Environments

- `test` - Test environment (for development and testing)
- `production` - Production environment (for live transactions)

## Required Parameters

- `storeId`: Your CorvusPay store ID
- `orderId`: Unique order ID in your application
- `cart`: Description of products/services
- `language`: Payment language code ('EN', 'DE', etc.)
- `currency`: Currency ('EUR', 'USD', etc.)
- `amount`: Amount in decimal format

## Security

- Never share your secret key with frontend clients
- Always validate transactions on your backend server
- Use HTTPS for all communications
- Implement server-to-server verification of status

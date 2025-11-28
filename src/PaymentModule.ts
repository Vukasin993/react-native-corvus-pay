import { NativeModules } from 'react-native';

export interface CheckoutParams {
  // REQUIRED
  storeId: number;
  orderId: string;
  cart: string;
  language?: string; // 'EN', 'DE', etc.
  currency?: string; // 'EUR', 'USD', etc.
  amount: number;

  // OPTIONAL
  requireComplete?: boolean;
  bestBefore?: number; // epoch seconds
  discountAmount?: number;
  voucherAmount?: number;

  // OPTIONAL — Card Profiles
  useCardProfiles?: boolean;
  userCardProfilesId?: string;

  // OPTIONAL — UI / flow tuning
  hideTab?: string; // e.g. "WALLET"
  hideTabs?: string; // e.g. "WALLET,CARDS"
  creditorReference?: string;
  debtorIban?: string;
  shopAccountId?: string;

  // OPTIONAL — Cardholder data
  cardholder?: {
    address?: string;
    city?: string;
    country?: string;
    countryCode?: string;
    email?: string;
    firstName?: string;
    lastName?: string;
    phone?: string;
    zip?: string;
  };

  // OPTIONAL — Installment parameters
  installmentParams?: {
    numberOfInstallments?: number;
    paymentAll?: {
      enabled: boolean;
      min: number;
      max: number;
    };
    paymentAllDynamic?: {
      paymentAmex?: { enabled: boolean; min: number; max: number };
      paymentVisa?: { enabled: boolean; min: number; max: number };
      paymentMaster?: { enabled: boolean; min: number; max: number };
      paymentMaestro?: { enabled: boolean; min: number; max: number };
      paymentJcb?: { enabled: boolean; min: number; max: number };
      paymentDiscover?: { enabled: boolean; min: number; max: number };
      paymentDiners?: { enabled: boolean; min: number; max: number };
      paymentDina?: { enabled: boolean; min: number; max: number };
    };
  };

  // OPTIONAL — Installments Map
  installmentsMap?: {
    items: Array<{
      cardType: string; // 'VISA', 'MASTER', etc.
      discounts: Array<{
        numberOfInstallments: number;
        amount: number;
        discountedAmount: number;
      }>;
    }>;
  };
}

export interface PaymentModuleInterface {
  configureEnvironment(env: 'test' | 'production'): void;
  checkoutWithSignature(
    params: CheckoutParams,
    signature: string
  ): Promise<any>;
  checkoutWithSecret(params: CheckoutParams, secretKey: string): Promise<any>;
}

export const PaymentModule: PaymentModuleInterface =
  NativeModules.PaymentModule;

import { NitroModules } from 'react-native-nitro-modules';
import type { CorvusPay } from './CorvusPay.nitro';
export {
  PaymentModule,
  type CheckoutParams,
  type PaymentModuleInterface,
} from './PaymentModule';

const CorvusPayHybridObject =
  NitroModules.createHybridObject<CorvusPay>('CorvusPay');

export function multiply(a: number, b: number): number {
  return CorvusPayHybridObject.multiply(a, b);
}

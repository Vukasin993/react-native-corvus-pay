import { useState } from 'react';
import { Text, View, StyleSheet, Button, Alert } from 'react-native';
import { multiply, PaymentModule } from 'react-native-corvus-pay';

const result = multiply(3, 7);

export default function App() {
  const [loading, setLoading] = useState(false);

  const handleCheckout = async () => {
    setLoading(true);
    try {
      // Configure environment
      PaymentModule.configureEnvironment('test');

      const checkoutParams = {
        storeId: 12345,
        orderId: 'ORDER-2025-00001',
        cart: 'Test Item x 1',
        language: 'EN',
        currency: 'EUR',
        amount: 100.0,
        requireComplete: false,
      };

      // Option 1: Using signature (requires backend)
      // const signature = await fetchSignatureFromBackend(checkoutParams);
      // await PaymentModule.checkoutWithSignature(checkoutParams, signature);

      // Option 2: Using secret key (client-side signing)
      const secretKey = 'your-secret-key-here';
      const checkoutResult = await PaymentModule.checkoutWithSecret(
        checkoutParams,
        secretKey
      );
      Alert.alert('Checkout Success', JSON.stringify(checkoutResult));
    } catch (error) {
      Alert.alert('Checkout Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Corvus Pay Example</Text>
      <Text>Result: {result}</Text>
      <Button
        title={loading ? 'Processing...' : 'Start Checkout'}
        onPress={handleCheckout}
        disabled={loading}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
});

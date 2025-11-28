package com.corvuspay.sdk.models

sealed class InstallmentParams {
    class PaymentAll(val enabled: Boolean, val min: Int, val max: Int) : InstallmentParams()
    class NumberOfInstallments(val count: Int) : InstallmentParams()
    class DynamicInstallments(val params: DynamicInstallmentParams) : InstallmentParams()

    companion object {
        fun createUsingPaymentAllWith(enabled: Boolean, min: Int, max: Int): InstallmentParams {
            return PaymentAll(enabled, min, max)
        }

        fun createUsingNumberOfInstallmentsWith(count: Int): InstallmentParams {
            return NumberOfInstallments(count)
        }

        fun createUsingPaymentAllDynamicWith(params: DynamicInstallmentParams): InstallmentParams {
            return DynamicInstallments(params)
        }
    }
}

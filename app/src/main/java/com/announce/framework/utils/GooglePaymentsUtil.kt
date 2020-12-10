package com.announce.framework.utils

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*

object GooglePaymentsUtil {

    private const val CURRENCY_CODE = "RUB"
    private val SUPPORTED_NETWORKS = arrayListOf(WalletConstants.CARD_NETWORK_OTHER,
        WalletConstants.CARD_NETWORK_VISA,
        WalletConstants.CARD_NETWORK_MASTERCARD)

    fun createGoogleApiClientForPay(context: Context): PaymentsClient =
        Wallet.getPaymentsClient(context,
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .setTheme(WalletConstants.THEME_LIGHT)
                .build())


    fun checkIsReadyGooglePay(paymentsClient: PaymentsClient,
                              callback: (res: Boolean) -> Unit) {

        val isReadyRequest = IsReadyToPayRequest.newBuilder()
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
            .build()

        val task = paymentsClient.isReadyToPay(isReadyRequest)
        task.addOnCompleteListener {
            try {
                if (it.getResult(ApiException::class.java) == true)
                    callback.invoke(true)
                else
                    callback.invoke(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                callback.invoke(false)
            }
        }
    }

    fun createPaymentDataRequest(price: String): PaymentDataRequest {
        val transaction = createTransaction(price)
        val request = generatePaymentRequest(transaction)
        return request
    }

    fun createTransaction(price: String): TransactionInfo =
        TransactionInfo.newBuilder()
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setTotalPrice(price)
            .setCurrencyCode(CURRENCY_CODE)
            .build()

    private fun generatePaymentRequest(transactionInfo: TransactionInfo): PaymentDataRequest {
        val tokenParams = PaymentMethodTokenizationParameters
            .newBuilder()
            .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_DIRECT)
            .addParameter("publicKey", "hpwgxfp2g3w6jzpm")
            .build()

        return PaymentDataRequest.newBuilder()
            .setPhoneNumberRequired(false)
            .setEmailRequired(true)
            .setShippingAddressRequired(false)
            .setTransactionInfo(transactionInfo)
            .addAllowedPaymentMethods(listOf(WalletConstants.PAYMENT_METHOD_CARD))
            .setCardRequirements(CardRequirements.newBuilder()
                .addAllowedCardNetworks(SUPPORTED_NETWORKS)
                .setAllowPrepaidCards(false)
                .setBillingAddressRequired(false)
                .build())
            .setPaymentMethodTokenizationParameters(tokenParams)
            .setUiRequired(true)
            .build()
    }

}



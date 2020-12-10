package com.announce.presenter.pay.ui

import android.app.Activity
import android.content.Intent
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.announce.R
import com.announce.databinding.PayMenuLayoutBinding
import com.announce.framework.utils.PaymentsUtil
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.PaymentMethodNonce
import com.google.android.gms.wallet.*
import java.lang.Exception


class PayActivity: AppCompatActivity(),
    PaymentMethodNonceCreatedListener,
    BraintreeCancelListener,
    BraintreeErrorListener{

    companion object {
        private const val REQUEST_PAY_CODE = 1
        const val KEY_ORDERED_TIME_MILLIS = "keyOrderedTimeMillis"
    }

    private lateinit var binding: PayMenuLayoutBinding
    private lateinit var paymentsClient: PaymentsClient
    private lateinit var braintreeFragment: BraintreeFragment

    private var orderedTimeMillis: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = PayMenuLayoutBinding.inflate(
            layoutInflater
        )

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        paymentsClient = PaymentsUtil.createPaymentsClient(this)


        initGooglePay()
        initButtons()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initGooglePay() {
        val json = PaymentsUtil.isReadyToPayRequest()!!.toString()
        val request = IsReadyToPayRequest.fromJson(json)
        val task = paymentsClient.isReadyToPay(request)

        task.addOnCompleteListener {
            if(it.result == true)
                showToast("Готов к оплате")
            else
                showToast("Не готов к оплате")
        }

    }


    private fun initButtons() {
        binding.run {
            btnBuy30Min.setOnClickListener {
                orderedTimeMillis = TimeUnit.MINUTES.toMillis(30)
                pay(getString(R.string.cost_30_min))
            }
            btnBuy1Hour.setOnClickListener {
                orderedTimeMillis = TimeUnit.HOURS.toMillis(1)
                pay(getString(R.string.cost_1_hour))
            }
            btnBuy3Hour.setOnClickListener {
                orderedTimeMillis = TimeUnit.HOURS.toMillis(3)
                pay(getString(R.string.cost_3_hour))
            }

        }
    }

    private fun pay(price: String) {


        val paymentData = PaymentsUtil.getPaymentDataRequest(price)
        val paymentRequest = PaymentDataRequest.fromJson(paymentData.toString())


        showToast("Requesting")

        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(paymentRequest),
            this,
            REQUEST_PAY_CODE
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_PAY_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (data == null)
                            return

                        handleSuccessPayment()
                    }
                    Activity.RESULT_CANCELED -> {
                        handleCanceledPayment()
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        if (data == null)
                            return

                        val status = AutoResolveHelper.getStatusFromIntent(data)
                        Log.e(
                            "GooglePayException",
                            "Load payment data has failed with status: $status"
                        )
                    }
                    else -> {
                    }
                }
            }
            else -> { }
        }
    }

    private fun handleCanceledPayment() {
        showToast(getString(R.string.canceled_payment))
    }

    private fun handleSuccessPayment() {
        showToast(getString(R.string.succesful_payment))

        val intent = Intent()
        intent.putExtra(KEY_ORDERED_TIME_MILLIS, orderedTimeMillis)

        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        showToast("Created")
    }

    override fun onCancel(requestCode: Int) {
        showToast("Canceled")
    }

    override fun onError(error: Exception) {
        showToast("Error")
        Log.e("GooglePayError", error.message, error)
    }
}
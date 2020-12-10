package com.announce.presenter.pay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject


class PayFragmentViewModel(application: Application)
    : AndroidViewModel(application) {

    private val context = application.applicationContext

    val baseCardPaymentMethod = JSONObject().apply {
        put("type", "CARD")
        put("parameters", JSONObject().apply {
            put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD")))
            put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
        })
    }

    val googlePayBaseConfiguration = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }



    var isReadyToPay = false
        private set

    init {


    }


    fun pay(price: Float) {



    }

}
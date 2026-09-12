package com.sahih

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sahih.data.LocalRiskEngine
import com.sahih.model.EvidencePack
import com.sahih.model.SellerVerification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SahihViewModel : ViewModel() {
    var verificationInput by mutableStateOf("")
        private set

    var assessment by mutableStateOf(LocalRiskEngine.assess(""))
        private set

    var sellerName by mutableStateOf("")
    var recipientName by mutableStateOf("")
    var accountNumber by mutableStateOf("")
    var amount by mutableStateOf("")

    var sellerVerification by mutableStateOf<SellerVerification?>(null)
        private set

    var evidence by mutableStateOf<EvidencePack?>(null)
        private set

    fun setVerificationInput(value: String) {
        verificationInput = value
    }

    fun analyseSharedContent(value: String?) {
        if (!value.isNullOrBlank()) {
            verificationInput = value
            analyseVerification()
        }
    }

    fun analyseVerification() {
        assessment = LocalRiskEngine.assess(verificationInput)
    }

    fun analyseCheckout() {
        sellerVerification = LocalRiskEngine.verifySeller(
            sellerName,
            recipientName,
            accountNumber
        )

        assessment = LocalRiskEngine.assess(
            text = "Payment to $recipientName for $sellerName",
            seller = sellerName,
            accountHolder = recipientName,
            accountNumber = accountNumber
        )
    }

    fun createEvidence() {
        evidence = EvidencePack(
            timestamp = SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale.US
            ).format(Date()),
            assessment = assessment,
            sellerVerification = sellerVerification
        )
    }
}
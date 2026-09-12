package com.sahih.callshield

import android.telecom.Call
import android.telecom.CallScreeningService
import com.sahih.data.LocalRiskEngine   // was com.sahih.app.data
import com.sahih.model.RiskLevel        // was com.sahih.app.model

/**
 * Background call detector ("works like Truecaller"). Android routes every
 * incoming call here before it rings, when Sahih is set as the system's
 * default call-screening app (Settings > Apps > Default apps > Caller ID
 * and spam app, on Android 10+).
 *
 * This is a stub: replace lookUpNumber() with a real call to your
 * SSM / Semak Mule / community-report backend.
 */
class SahihCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart ?: return
        val isHighRisk = lookUpNumber(number)

        val response = CallResponse.Builder()
            // Never silently block on a hackathon build -- surface the
            // warning UI (CallShieldScreen) instead of rejecting outright.
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(!isHighRisk)
            .build()

        respondToCall(callDetails, response)

        // Android does not permit this service to show an arbitrary overlay by
        // default. The in-app Call Shield screen exposes the supported demo
        // flow and explains the required caller-ID role.
    }

    private fun lookUpNumber(number: String): Boolean {
        // Offline demo data only. This deliberately does not claim a live caller-ID lookup.
        return LocalRiskEngine.assess(number).level == RiskLevel.HIGH
    }
}

package com.sahih.callshield

import android.telecom.Call
import android.telecom.CallScreeningService

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

        // TODO: launch an overlay / full-screen intent showing
        // CallShieldScreen when isHighRisk is true, similar to how
        // Truecaller shows its identification overlay.
    }

    private fun lookUpNumber(number: String): Boolean {
        // Placeholder -- wire this to the real risk-scoring backend.
        return false
    }
}

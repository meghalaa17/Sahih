package com.sahih.data

/**
 * Minimal parser for EMVCo-style payment QR codes (the standard behind
 * DuitNow QR, PromptPay, PayNow, etc). These are TLV-encoded strings:
 * a 2-digit tag, a 2-digit length, then that many characters of value,
 * repeated. Tag 59 = Merchant Name, Tag 60 = Merchant City, Tag 54 =
 * Transaction Amount (only present on dynamic/fixed-amount QR codes).
 */
object QrPaymentParser {

    data class ParsedQr(
        val merchantName: String? = null,
        val merchantCity: String? = null,
        val amount: String? = null,
        val rawValue: String,
    )

    fun looksLikePaymentQr(rawValue: String): Boolean {
        // EMV QR codes always start with tag "00" (Payload Format Indicator)
        return rawValue.length > 4 && rawValue.startsWith("00")
    }

    fun parse(rawValue: String): ParsedQr {
        val fields = parseTlv(rawValue)
        return ParsedQr(
            merchantName = fields["59"]?.trim(),
            merchantCity = fields["60"]?.trim(),
            amount = fields["54"]?.trim(),
            rawValue = rawValue,
        )
    }

    private fun parseTlv(raw: String): Map<String, String> {
        val fields = mutableMapOf<String, String>()
        var i = 0
        while (i + 4 <= raw.length) {
            val tag = raw.substring(i, i + 2)
            val lengthStr = raw.substring(i + 2, i + 4)
            val length = lengthStr.toIntOrNull() ?: break
            val valueStart = i + 4
            val valueEnd = valueStart + length
            if (valueEnd > raw.length) break
            fields[tag] = raw.substring(valueStart, valueEnd)
            i = valueEnd
        }
        return fields
    }
}
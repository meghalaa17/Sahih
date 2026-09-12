package com.sahih.data


object ReceiptParser {

    private val accountNumberPattern = Regex("""\b\d(?:[ -]?\d){8,15}\b""")
    private val amountPattern = Regex("""(?:RM|MYR)\s?([\d,]+\.\d{2}|[\d,]+)""", RegexOption.IGNORE_CASE)
    private val recipientLabelPattern = Regex(
        """(?:recipient|beneficiary|account name|to|payee)\s*[:\-]?\s*([A-Za-z][A-Za-z '&.\-]{2,50})""",
        RegexOption.IGNORE_CASE
    )
    private val sellerLabelPattern = Regex(
        """(?:merchant|seller|reference|from)\s*[:\-]?\s*([A-Za-z][A-Za-z '&.\-]{2,50})""",
        RegexOption.IGNORE_CASE
    )

    data class ParsedReceipt(
        val sellerName: String? = null,
        val recipientName: String? = null,
        val accountNumber: String? = null,
        val amount: String? = null,
    )

    fun parse(rawText: String): ParsedReceipt {
        val accountNumber = accountNumberPattern.find(rawText)?.value?.filter(Char::isDigit)
        val amount = amountPattern.find(rawText)?.groupValues?.getOrNull(1)?.replace(",", "")
        val recipient = recipientLabelPattern.find(rawText)?.groupValues?.getOrNull(1)?.trim()
        val seller = sellerLabelPattern.find(rawText)?.groupValues?.getOrNull(1)?.trim()
        return ParsedReceipt(seller, recipient, accountNumber, amount)
    }
}
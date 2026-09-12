package com.sahih.enrichment

data class BankCheckResult(
    val legit: Boolean,
    val claimedBusinessName: String?,
    val accountHolderName: String,
    val ssmMatch: Boolean,
    val semakMuleFlagged: Boolean,
    val semakMuleCaseCount: Int = 0,
    val reason: String,
)

interface BankAccountCheckService {
    suspend fun check(
        accountHolderName: String,
        bankAccountNumber: String?,
        claimedBusinessName: String?,
    ): BankCheckResult
}

/**
 * STUB. Real implementation should cross-check bankAccountNumber against
 * PDRM's Semak Mule database and claimedBusinessName against SSM company
 * registration. Swap this class out -- CheckoutScreen doesn't need to
 * change when you do.
 */
class MockBankAccountCheckService : BankAccountCheckService {
    override suspend fun check(
        accountHolderName: String,
        bankAccountNumber: String?,
        claimedBusinessName: String?,
    ): BankCheckResult {
        // TODO: replace with a real SSM registration lookup + Semak Mule check.
        val mismatch = claimedBusinessName != null &&
                !accountHolderName.contains("Enterprise", ignoreCase = true) &&
                !accountHolderName.contains("Sdn", ignoreCase = true) &&
                !accountHolderName.contains("Trading", ignoreCase = true)

        return if (mismatch) {
            BankCheckResult(
                legit = false,
                claimedBusinessName = claimedBusinessName,
                accountHolderName = accountHolderName,
                ssmMatch = false,
                semakMuleFlagged = false,
                reason = "The seller claims to be \"$claimedBusinessName\", but this account belongs to an " +
                        "unlinked personal name. Legitimate businesses usually receive payment through a " +
                        "registered business account.",
            )
        } else {
            BankCheckResult(
                legit = true,
                claimedBusinessName = claimedBusinessName,
                accountHolderName = accountHolderName,
                ssmMatch = true,
                semakMuleFlagged = false,
                reason = "Account holder name matches the claimed business, and this account has no " +
                        "reports in Semak Mule.",
            )
        }
    }
}
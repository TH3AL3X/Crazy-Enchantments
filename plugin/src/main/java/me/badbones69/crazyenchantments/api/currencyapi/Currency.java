package me.badbones69.crazyenchantments.api.currencyapi

enum class Currency(
    /**
     * Get the name of the currency.
     * @return The name of the currency.
     */
    override val name: String
) {
    VAULT("Vault"), XP_LEVEL("XP_Level"), XP_TOTAL("XP_Total");

    companion object {
        /**
         * Checks if it is a compatible currency.
         * @param currency The currency name you are checking.
         * @return True if it is supported and false if not.
         */
        fun isCurrency(currency: String): Boolean {
            for (c in values()) {
                if (currency.equals(c.name, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }

        /**
         * Get a currency enum.
         * @param currency The currency you want.
         * @return The currency enum.
         */
        fun getCurrency(currency: String): Currency? {
            for (c in values()) {
                if (currency.equals(c.name, ignoreCase = true)) {
                    return c
                }
            }
            return null
        }
    }
}
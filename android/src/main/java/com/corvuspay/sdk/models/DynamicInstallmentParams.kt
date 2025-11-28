package com.corvuspay.sdk.models

data class DynamicInstallmentParams(
    val amex: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val visa: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val master: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val maestro: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val jcb: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val discover: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val diners: Triple<Boolean, Int, Int> = Triple(false, 0, 0),
    val dina: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
) {
    class Builder {
        private var amex: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var visa: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var master: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var maestro: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var jcb: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var discover: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var diners: Triple<Boolean, Int, Int> = Triple(false, 0, 0)
        private var dina: Triple<Boolean, Int, Int> = Triple(false, 0, 0)

        fun addAmex(enabled: Boolean, min: Int, max: Int) = apply { amex = Triple(enabled, min, max) }
        fun addVisa(enabled: Boolean, min: Int, max: Int) = apply { visa = Triple(enabled, min, max) }
        fun addMaster(enabled: Boolean, min: Int, max: Int) = apply { master = Triple(enabled, min, max) }
        fun addMaestro(enabled: Boolean, min: Int, max: Int) = apply { maestro = Triple(enabled, min, max) }
        fun addJcb(enabled: Boolean, min: Int, max: Int) = apply { jcb = Triple(enabled, min, max) }
        fun addDiscover(enabled: Boolean, min: Int, max: Int) = apply { discover = Triple(enabled, min, max) }
        fun addDiners(enabled: Boolean, min: Int, max: Int) = apply { diners = Triple(enabled, min, max) }
        fun addDina(enabled: Boolean, min: Int, max: Int) = apply { dina = Triple(enabled, min, max) }

        fun build(): DynamicInstallmentParams = DynamicInstallmentParams(
            amex = amex,
            visa = visa,
            master = master,
            maestro = maestro,
            jcb = jcb,
            discover = discover,
            diners = diners,
            dina = dina
        )
    }
}

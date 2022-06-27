package nl.t64.cot.screens.loot

import nl.t64.cot.components.loot.Loot
import nl.t64.cot.screens.ParchmentScreen


abstract class LootScreen : ParchmentScreen() {

    lateinit var loot: Loot
    lateinit var lootTitle: String

    override fun show() {
        setInputProcessors(stage)
        val lootUI = LootUI({ resolveLootAndCloseScreen(it) }, loot, lootTitle)
        lootUI.show(stage)
    }

    override fun render(dt: Float) {
        renderStage(dt)
    }

    abstract fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean)

}

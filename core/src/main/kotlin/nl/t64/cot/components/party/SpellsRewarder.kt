package nl.t64.cot.components.party

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.abilities.AbilityDatabase
import nl.t64.cot.components.party.abilities.AbilityItem


object SpellsRewarder {

    fun receivePossibleSpells(lootId: String) {
        val reward: Loot = gameData.loot.getLoot(lootId)
        if (!reward.isTaken()) {
            receiveSpells(reward)
        }
    }

    private fun receiveSpells(reward: Loot) {
        // todo, it's always Mozes now who gets the spells.
        val mozes: HeroItem = gameData.party.getPlayer()

        val spellsToLearn: List<AbilityItem> = reward.content.map { AbilityDatabase.createAbilityItem(it.key) }
        spellsToLearn
            .filter { mozes.getAbilityById(it.id) == null }
            .forEach { mozes.learn(it, 0) }
        showMessageTooltipRewardSpells(spellsToLearn)
        reward.clearContent()
    }

    private fun showMessageTooltipRewardSpells(spellItems: List<AbilityItem>) {
        stopAllSe()
        playSe(AudioEvent.SE_REWARD)
        val builder = StringBuilder()
        spellItems.forEach { builder.appendLine("+ ${it.name}") }
        builder.deleteAt(builder.lastIndex)
        worldScreen.showMessageTooltip(builder.toString())
    }

}

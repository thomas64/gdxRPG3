package nl.t64.cot.components.party

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.spells.SpellDatabase
import nl.t64.cot.components.party.spells.SpellItem


object SpellsRewarder {

    fun receivePossibleSpells(lootId: String) {
        val reward = gameData.loot.getLoot(lootId)
        if (!reward.isTaken()) {
            receiveSpells(reward)
        }
    }

    private fun receiveSpells(reward: Loot) {
        val spellsToLearn = reward.content.map { SpellDatabase.createSpellItem(it.key, it.value) }
        // todo, it's always Mozes now who gets the spells.
        val mozes = gameData.party.getPlayer()
        spellsToLearn.forEach { learnSpell(mozes, it) }
        showMessageTooltipRewardSpells(spellsToLearn)
        reward.clearContent()
    }

    private fun learnSpell(hero: HeroItem, spellToLearn: SpellItem) {
        val knownSpell = hero.getSpellById(spellToLearn.id)
        while (knownSpell.rank < spellToLearn.rank) {
            hero.doUpgrade(knownSpell, 0, 0)
        }
    }

    private fun showMessageTooltipRewardSpells(spellItems: List<SpellItem>) {
        stopAllSe()
        playSe(AudioEvent.SE_REWARD)
        val builder = StringBuilder()
        // todo, it only shows which spells and not how much ranks.
        spellItems.forEach { builder.appendLine("+ ${it.name}") }
        builder.deleteAt(builder.lastIndex)
        brokerManager.messageObservers.notifyShowMessageTooltip(builder.toString())
    }

}

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
        val actuallyLearned = mutableListOf<AbilityItem>()
        reward.content.forEach { possibleTeachSpellToParty(it, actuallyLearned) }
        if (actuallyLearned.isNotEmpty()) {
            showMessageTooltipRewardSpells(actuallyLearned)
        }
        reward.clearContent()
    }

    private fun possibleTeachSpellToParty(rewardEntry: Map.Entry<String, Int>,
                                          actuallyLearned: MutableList<AbilityItem>) {
        val party: List<HeroItem> = gameData.party.getAllHeroesAlive()
        val player: HeroItem = gameData.party.getPlayer()

        val spellToLearn: AbilityItem = AbilityDatabase.createAbilityItem(rewardEntry.key)
        if (rewardEntry.value == 99) {
            party.forEach { it.possibleLearnSpell(spellToLearn, actuallyLearned) }
        } else {
            player.possibleLearnSpell(spellToLearn, actuallyLearned)
        }
    }

    private fun HeroItem.possibleLearnSpell(spellToLearn: AbilityItem, actuallyLearned: MutableList<AbilityItem>) {
        if (this.getAbilityById(spellToLearn.id) == null) {
            this.learn(spellToLearn, 0)
            actuallyLearned.add(spellToLearn)
        }
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

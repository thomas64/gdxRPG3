package nl.t64.cot.components.party

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.abilities.AbilityDatabase
import nl.t64.cot.components.party.abilities.AbilityItemId


object SpellsRewarder {

    fun receivePossibleSpells(lootId: String) {
        val reward: Loot = gameData.loot.getLoot(lootId)
        if (!reward.isTaken()) {
            receiveSpells(reward)
        }
    }

    private fun receiveSpells(reward: Loot) {
        val spellsToShowInPopup = mutableListOf<AbilityItemId>()
        reward.content.forEach { possibleTeachSpellToParty(it, spellsToShowInPopup) }
        if (spellsToShowInPopup.isNotEmpty()) {
            showMessageTooltipRewardSpells(spellsToShowInPopup)
        }
        reward.clearContent()
    }

    private fun possibleTeachSpellToParty(rewardEntry: Map.Entry<String, Int>,
                                          spellsToShowInPopup: MutableList<AbilityItemId>) {
        val party: List<HeroItem> = gameData.party.getAllHeroesAlive()
        val player: HeroItem = gameData.party.getPlayer()

        val spellToLearn: AbilityItemId = AbilityItemId.valueOf(rewardEntry.key.uppercase())
        if (rewardEntry.value == 99) {
            party.forEach { it.possibleLearnSpell(spellToLearn, spellsToShowInPopup) }
        } else {
            player.possibleLearnSpell(spellToLearn, spellsToShowInPopup)
        }
    }

    private fun HeroItem.possibleLearnSpell(spellIdToLearn: AbilityItemId,
                                            spellsToShowInPopup: MutableList<AbilityItemId>) {
        if (this.getAbilityById(spellIdToLearn) == null) {
            this.learn(spellIdToLearn, 0)
            spellsToShowInPopup.add(spellIdToLearn)
        }
    }

    private fun showMessageTooltipRewardSpells(spellItems: List<AbilityItemId>) {
        val titlesToShow: List<String> = spellItems.map { AbilityDatabase.createAbilityItem(it).name }
        stopAllSe()
        playSe(AudioEvent.SE_REWARD)
        val builder = StringBuilder()
        titlesToShow.forEach { builder.appendLine("+ $it") }
        builder.deleteAt(builder.lastIndex)
        worldScreen.showMessageTooltip(builder.toString())
    }

}

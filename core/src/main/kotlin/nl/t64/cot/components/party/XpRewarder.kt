package nl.t64.cot.components.party

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.loot.Loot


object XpRewarder {

    fun receivePossibleXp(lootId: String) {
        val reward = gameData.loot.getLoot(lootId)
        if (!reward.isXpGained()) {
            receiveXp(reward)
        }
    }

    private fun receiveXp(reward: Loot) {
        val levelUpMessage = StringBuilder()
        gameData.party.gainXp(reward.xp, levelUpMessage)
        val finalMessage = levelUpMessage.toString().trim()
        showMessageTooltipRewardXp(reward.xp, finalMessage)
        reward.clearXp()
    }

    private fun showMessageTooltipRewardXp(xp: Int, levelUpMessage: String) {
        stopAllSe()
        if (levelUpMessage.isEmpty()) {
            showMessageTooltipXpOnly(xp)
        } else {
            showMessageTooltipXpAndLevelUp(xp, levelUpMessage)
        }
    }

    private fun showMessageTooltipXpOnly(xp: Int) {
        playSe(AudioEvent.SE_REWARD)
        screenManager.getWorldScreen().showMessageTooltip("+ $xp XP")
    }

    private fun showMessageTooltipXpAndLevelUp(xp: Int, levelUpMessage: String) {
        playSe(AudioEvent.SE_LEVELUP)
        screenManager.getWorldScreen().showMessageTooltip("+ $xp XP" + System.lineSeparator() + levelUpMessage)
    }

}

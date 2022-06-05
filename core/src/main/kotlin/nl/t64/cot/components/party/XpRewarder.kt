package nl.t64.cot.components.party

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
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
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        if (levelUpMessage.isEmpty()) {
            showMessageTooltipXpOnly(xp)
        } else {
            showMessageTooltipXpAndLevelUp(xp, levelUpMessage)
        }
    }

    private fun showMessageTooltipXpOnly(xp: Int) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
        brokerManager.messageObservers.notifyShowMessageTooltip("+ $xp XP")
    }

    private fun showMessageTooltipXpAndLevelUp(xp: Int, levelUpMessage: String) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_LEVELUP)
        brokerManager.messageObservers.notifyShowMessageTooltip("+ $xp XP" + System.lineSeparator() + levelUpMessage)
    }

}

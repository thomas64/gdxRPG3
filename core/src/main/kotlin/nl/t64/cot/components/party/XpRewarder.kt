package nl.t64.cot.components.party

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
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
        gameData.party.gainXp(reward.xp)
        showMessageTooltipRewardXp(reward.xp)
        reward.clearXp()
    }

    private fun showMessageTooltipRewardXp(xp: Int) {
        stopAllSe()
        playSe(AudioEvent.SE_REWARD)
        worldScreen.showMessageTooltip("+ $xp XP")
    }

}

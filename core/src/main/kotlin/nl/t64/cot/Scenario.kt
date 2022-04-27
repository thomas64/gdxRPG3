package nl.t64.cot

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.constants.Constant


class Scenario {

    fun startNewGame() {
        addMozesToParty()
        addGoldToInventory()
        addQuestGraceToLogbook()
    }

    fun startSecondCycle() {
        reviveMozes()
        gameData.resetCycle()
        setQuestGraceComplete()
        addQuestArdorToLogbook()
    }

    private fun addMozesToParty() {
        val mozes = gameData.heroes.getCertainHero(Constant.PLAYER_ID)
        gameData.heroes.removeHero(Constant.PLAYER_ID)
        gameData.party.addHero(mozes)
    }

    private fun addGoldToInventory() {
        val gold = InventoryDatabase.createInventoryItem("gold")
        gameData.inventory.autoSetItem(gold)
    }

    private fun addQuestGraceToLogbook() {
        val questGrace = gameData.quests.getQuestById("quest_grace_is_missing")
        questGrace.accept()
    }

    private fun reviveMozes() {
        val mozes = gameData.party.getPlayer()
        mozes.revive()
    }

    private fun setQuestGraceComplete() {
        val questGrace = gameData.quests.getQuestById("quest_grace_is_missing")
        questGrace.setTaskComplete("4", false)
        questGrace.setTaskComplete("5", false)
        questGrace.forceCompleteQuest()
    }

    private fun addQuestArdorToLogbook() {
        val questArdor = gameData.quests.getQuestById("quest_royal_sacrifice")
        questArdor.accept()
    }

}
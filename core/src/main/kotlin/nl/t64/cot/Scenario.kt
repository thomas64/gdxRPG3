package nl.t64.cot

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.constants.Constant


class Scenario {

    fun startNewGame() {
        addMozesToParty()
        addGoldToInventory()
        addItemsToStorage()
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

    private fun addItemsToStorage() {
        val hafted = InventoryDatabase.createInventoryItem("basic_mace")
        val missile = InventoryDatabase.createInventoryItem("basic_shortbow")
        val shield = InventoryDatabase.createInventoryItem("basic_light_shield")
        val gloves = InventoryDatabase.createInventoryItem("basic_light_shoulders")
        val gold = InventoryDatabase.createInventoryItem("gold", 8)
        val potion = InventoryDatabase.createInventoryItem("curing_potion")
        gameData.storage.autoSetItem(hafted)
        gameData.storage.autoSetItem(missile)
        gameData.storage.autoSetItem(shield)
        gameData.storage.autoSetItem(gloves)
        gameData.storage.autoSetItem(gold)
        gameData.storage.autoSetItem(potion)
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
        questGrace.setTaskComplete("4", showTooltip = false)
        questGrace.setTaskComplete("5", showTooltip = false)
        questGrace.forceFinish()
    }

    private fun addQuestArdorToLogbook() {
        val questArdor = gameData.quests.getQuestById("quest_royal_sacrifice")
        questArdor.accept()
    }

}

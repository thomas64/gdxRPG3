package nl.t64.cot

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.constants.Constant


class Scenario {

    fun startNewGame() {
        addMozesToParty()
        addItemsToInventory()
        addItemsToStorage()
        addQuestGraceToLogbook()
    }

    fun startSecondCycle() {
        reviveMozes()
        setQuestGraceComplete()
        gameData.resetCycle()
        addQuestArdorToLogbook()
        profileManager.saveProfile()
    }

    fun startThirdCycle() {
        reviveMozes()
        gameData.clock.start()
        gameData.resetCycle()
        addCrystalToInventory()
        addQuestLastdennToLogbook()
        profileManager.saveProfile()
    }

    private fun addMozesToParty() {
        val mozes = gameData.heroes.getCertainHero(Constant.PLAYER_ID)
        gameData.heroes.removeHero(Constant.PLAYER_ID)
        gameData.party.addHero(mozes)
    }

    private fun addItemsToInventory() {
        val hafted = InventoryDatabase.createInventoryItem("basic_mozes_mace")
        val missile = InventoryDatabase.createInventoryItem("basic_mozes_bow")
        val gold = InventoryDatabase.createInventoryItem("gold")
        gameData.inventory.autoSetItem(hafted)
        gameData.inventory.autoSetItem(missile)
        gameData.inventory.autoSetItem(gold)
    }

    private fun addItemsToStorage() {
        val gloves = InventoryDatabase.createInventoryItem("basic_light_shoulders")
        val gold = InventoryDatabase.createInventoryItem("gold", 4)
        val potion = InventoryDatabase.createInventoryItem("curing_potion")
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

    private fun addCrystalToInventory() {
        val crystal = InventoryDatabase.createInventoryItem("crystal_of_time")
        gameData.inventory.autoSetItem(crystal)
    }

    private fun addQuestLastdennToLogbook() {
        val questYlarus = gameData.quests.getQuestById("quest_god_of_power")
        questYlarus.accept()
    }

}

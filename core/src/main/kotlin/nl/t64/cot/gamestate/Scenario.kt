package nl.t64.cot.gamestate

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.AbilityItemId
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant


class Scenario {

    fun startNewGame() {
        addMozesToParty()
        addItemsToInventory()
        addItemsToStorage()
        addQuestGraceToLogbook()
        gameData.clock.start()
        gameData.numberOfCycles = 1

        if (preferenceManager.isDebugModeOn) {
            setupDebugMode()
        }
    }

    fun startSecondCycle() {
        reviveParty()
        setQuestGraceComplete()
        gameData.resetCycle()
        addQuestArdorToLogbook()
        addQuestVoiceToLogbook()
        gameData.clock.start()
        profileManager.saveProfile()
    }

    fun startThirdCycle() {
        reviveParty()
        gameData.resetCycle()
        gameData.clock.start()
        profileManager.saveProfile()
    }

    fun startFourthCycle() {
        reviveParty()
        gameData.resetCycle()
        hideQuestVoiceFromLogbook()
        addQuestYlarusToLogbook()
        gameData.clock.start()
        profileManager.saveProfile()
    }

    private fun addMozesToParty() {
        val mozes = gameData.heroes.getCertainHero(Constant.PLAYER_ID)
        gameData.heroes.removeHero(Constant.PLAYER_ID)
        gameData.party.addHero(mozes)
    }

    private fun setupDebugMode() {
        addPartyMembers()

        equipPartyWithFullArmorSets()
        equipMozesWithBasicGear()
        equipRyiahWithThunderStaff()

        teachPartyFocusedStrike()
        teachMozesWizardAndMagicShield()
        upgradeMozesStats()
        upgradeReignaldStats()

        addDebugItemsToInventory()

        setQuestGraceComplete()
        addQuestArdorToLogbook()
        addQuestVoiceToLogbook()
        hideQuestVoiceFromLogbook()
        addQuestYlarusToLogbook()
        finishQuestsLastdenn()

        gameData.numberOfCycles = 5
    }

    private fun addItemsToInventory() {
        val axe = InventoryDatabase.createInventoryItem("basic_mozes_axe")
        gameData.inventory.autoSetItem(axe)
        val sword = InventoryDatabase.createInventoryItem("basic_mozes_sword")
        gameData.inventory.autoSetItem(sword)
        val shield = InventoryDatabase.createInventoryItem("basic_mozes_shield")
        gameData.inventory.autoSetItem(shield)
        val gold = InventoryDatabase.createInventoryItem("gold")
        gameData.inventory.autoSetItem(gold)
    }

    private fun addItemsToStorage() {
        val shoulders = InventoryDatabase.createInventoryItem("basic_light_cloak")
        val gold = InventoryDatabase.createInventoryItem("gold", 4)
        val healingPotion = InventoryDatabase.createInventoryItem("healing_potion", 2)
        val energyPotion = InventoryDatabase.createInventoryItem("energy_potion")
        gameData.storage.autoSetItem(shoulders)
        gameData.storage.autoSetItem(gold)
        gameData.storage.autoSetItem(healingPotion)
        gameData.storage.autoSetItem(energyPotion)
    }

    private fun addQuestGraceToLogbook() {
        val questGrace = gameData.quests.getQuestById("quest_grace_is_missing")
        questGrace.accept()
    }

    private fun reviveParty() {
        gameData.party.getPlayer().revive()
        gameData.party.getAllHeroesAlive().forEach { it.revive() }
    }

    private fun addPartyMembers() {
        val luana = gameData.heroes.getCertainHero("luana")
        gameData.heroes.removeHero("luana")
        gameData.party.addHero(luana)

        val valter = gameData.heroes.getCertainHero("valter")
        gameData.heroes.removeHero("valter")
        gameData.party.addHero(valter)

        val reignald = gameData.heroes.getCertainHero("reignald")
        gameData.heroes.removeHero("reignald")
        gameData.party.addHero(reignald)

        val ryiah = gameData.heroes.getCertainHero("ryiah")
        gameData.heroes.removeHero("ryiah")
        gameData.party.addHero(ryiah)

        val galen = gameData.heroes.getCertainHero("galen")
        gameData.heroes.removeHero("galen")
        gameData.party.addHero(galen)
    }

    private fun equipPartyWithFullArmorSets() {
        gameData.party.getAllHeroes().forEach { equipWithFullArmorSet(it, getArmorSetPrefixOf(it)) }
    }

    private fun getArmorSetPrefixOf(hero: HeroItem): String {
        return when (hero.id) {
            "reignald" -> "basic_medium"
            "galen" -> "fine_medium"
            else -> "basic_light"
        }
    }

    private fun equipWithFullArmorSet(hero: HeroItem, armorSetPrefix: String) {
        InventoryGroup.entries
            .filter { it.isPartArmorOfSet() }
            .forEach { hero.forceSetInventoryItemFor(it, createArmorPiece(armorSetPrefix, it)) }
    }

    private fun createArmorPiece(armorSetPrefix: String, inventoryGroup: InventoryGroup): InventoryItem {
        return InventoryDatabase.createInventoryItem("${armorSetPrefix}_${inventoryGroup.name.lowercase()}")
    }

    private fun equipMozesWithBasicGear() {
        val mozes: HeroItem = gameData.party.getPlayer()
        val sword: InventoryItem = InventoryDatabase.createInventoryItem("basic_sword")
        val shield: InventoryItem = InventoryDatabase.createInventoryItem("basic_light_shield")
        val ribbon: InventoryItem = InventoryDatabase.createInventoryItem("basic_grace_ribbon")
        val ring: InventoryItem = InventoryDatabase.createInventoryItem("fine_fairy_ring")
        mozes.forceSetInventoryItemFor(InventoryGroup.WEAPON, sword)
        mozes.forceSetInventoryItemFor(InventoryGroup.SHIELD, shield)
        mozes.forceSetInventoryItemFor(InventoryGroup.ACCESSORY, ribbon)
        mozes.forceSetInventoryItemFor(InventoryGroup.RING, ring)
    }

    private fun equipRyiahWithThunderStaff() {
        val ryiah: HeroItem = gameData.party.getCertainHero("ryiah")
        val staff: InventoryItem = InventoryDatabase.createInventoryItem("basic_staff_thunder")
        ryiah.forceSetInventoryItemFor(InventoryGroup.WEAPON, staff)
    }

    private fun teachPartyFocusedStrike() {
        gameData.party.getAllHeroes().forEach { it.learn(AbilityItemId.STRIKE_3F, 0) }
    }

    private fun teachMozesWizardAndMagicShield() {
        val mozes: HeroItem = gameData.party.getPlayer()
        val skill: SkillItem = mozes.getSkillById(SkillItemId.WIZARD)
        mozes.doUpgrade(skill, 0)
        mozes.learn(AbilityItemId.MAGIC_SHIELD, 0)
    }

    private fun upgradeMozesStats() {
        val mozes: HeroItem = gameData.party.getPlayer()
        upgradeStatOf(mozes, StatItemId.STRENGTH, 2)
        upgradeStatOf(mozes, StatItemId.DEXTERITY, 2)
    }

    private fun upgradeReignaldStats() {
        val reignald: HeroItem = gameData.party.getCertainHero("reignald")
        upgradeStatOf(reignald, StatItemId.SPEED, 4)
    }

    private fun upgradeStatOf(hero: HeroItem, statItemId: StatItemId, amountOfRanks: Int) {
        val stat: StatItem = hero.getStatById(statItemId)
        repeat(amountOfRanks) { hero.doUpgrade(stat, 0) }
    }

    private fun addDebugItemsToInventory() {
        val crystalOfTime = InventoryDatabase.createInventoryItem("crystal_of_time")
        gameData.inventory.autoSetItem(crystalOfTime)

        listOf("basic_sword", "basic_spear", "basic_axe", "basic_dagger", "basic_knife", "basic_bow")
            .forEach { weaponId ->
                repeat(2) {
                    val item: InventoryItem = InventoryDatabase.createInventoryItem(weaponId)
                    gameData.inventory.autoSetItem(item)
                }
            }

//        val epic = InventoryDatabase.createInventoryItem("epic_ring_of_healing")
//        gameData.inventory.autoSetItem(epic)
//        val power = InventoryDatabase.createInventoryItem("epic_power_bracelet")
//        gameData.inventory.autoSetItem(power)
//        val medkit = InventoryDatabase.createInventoryItem("epic_medkit")
//        gameData.inventory.autoSetItem(medkit)
//        val keyLastdennJail = InventoryDatabase.createInventoryItem("key_lastdenn_jail")
//        gameData.inventory.autoSetItem(keyLastdennJail)

        val horseshoes = InventoryDatabase.createInventoryItem("horseshoe", 8)
        gameData.inventory.autoSetItem(horseshoes)
        val horsemedicine = InventoryDatabase.createInventoryItem("horse_medicine", 8)
        gameData.inventory.autoSetItem(horsemedicine)
        val towrope = InventoryDatabase.createInventoryItem("tow_rope", 2)
        gameData.inventory.autoSetItem(towrope)
        val ghostRepeller = InventoryDatabase.createInventoryItem("ghost_repeller", 1)
        gameData.inventory.autoSetItem(ghostRepeller)
    }

    private fun setQuestGraceComplete() {
        val questGrace = gameData.quests.getQuestById("quest_grace_is_missing")
        questGrace.setTaskComplete("9", showTooltip = false)    // "_9_"
        questGrace.setTaskComplete("10", showTooltip = false)   // "_10_"
        questGrace.finish(false)
    }

    private fun addQuestArdorToLogbook() {
        gameData.quests.getQuestById("quest_royal_sacrifice").accept()
        gameData.quests.getQuestById("quest_sub_royal_sacrifice").accept()
    }

    private fun addQuestVoiceToLogbook() {
        gameData.quests.getQuestById("quest_to_lastdenn_then").accept()
    }

    private fun hideQuestVoiceFromLogbook() {
        gameData.quests.getQuestById("quest_to_lastdenn_then").isHidden = true
    }

    private fun addQuestYlarusToLogbook() {
        gameData.quests.getQuestById("quest_to_lastdenn_then2").accept()
    }

    private fun finishQuestsLastdenn() {
        gameData.quests.getQuestById("quest_to_lastdenn_then2").forceSetAllTasksComplete()
        gameData.quests.getQuestById("quest_to_lastdenn_then2").finish(false)
        gameData.quests.getQuestById("quest_lastdenn_santino").forceSetAllTasksComplete()
        gameData.quests.getQuestById("quest_lastdenn_santino").finish(false)
    }

}

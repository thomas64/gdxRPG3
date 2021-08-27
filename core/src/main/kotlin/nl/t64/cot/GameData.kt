package nl.t64.cot

import nl.t64.cot.components.battle.BattleContainer
import nl.t64.cot.components.conversation.ConversationContainer
import nl.t64.cot.components.conversation.PhraseIdContainer
import nl.t64.cot.components.cutscene.CutsceneContainer
import nl.t64.cot.components.door.DoorContainer
import nl.t64.cot.components.event.EventContainer
import nl.t64.cot.components.loot.LootContainer
import nl.t64.cot.components.loot.SpoilsContainer
import nl.t64.cot.components.party.HeroContainer
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryDatabase.createInventoryItem
import nl.t64.cot.components.quest.QuestContainer
import nl.t64.cot.constants.Constant
import nl.t64.cot.subjects.ProfileObserver


class GameData : ProfileObserver {

    lateinit var heroes: HeroContainer
    lateinit var party: PartyContainer
    lateinit var inventory: InventoryContainer
    lateinit var battles: BattleContainer
    lateinit var conversations: ConversationContainer
    lateinit var quests: QuestContainer
    lateinit var events: EventContainer
    lateinit var loot: LootContainer
    lateinit var spoils: SpoilsContainer
    lateinit var doors: DoorContainer
    lateinit var cutscenes: CutsceneContainer
    var isTooltipEnabled = false
    var isComparingEnabled = false

    override fun onNotifyCreateProfile(profileManager: ProfileManager) {
        heroes = HeroContainer()
        party = PartyContainer()
        inventory = InventoryContainer()
        battles = BattleContainer()
        conversations = ConversationContainer()
        quests = QuestContainer()
        events = EventContainer()
        loot = LootContainer()
        spoils = SpoilsContainer()
        doors = DoorContainer()
        cutscenes = CutsceneContainer()
        isTooltipEnabled = true
        isComparingEnabled = true
        addFirstHeroToParty()
        addFirstItemsToInventory()
        onNotifySaveProfile(profileManager)
    }

    override fun onNotifySaveProfile(profileManager: ProfileManager) {
        profileManager.setProperty("heroes", heroes)
        profileManager.setProperty("party", party)
        profileManager.setProperty("inventory", inventory)
        profileManager.setProperty("battles", battles)
        profileManager.setProperty("conversations", conversations.createPhraseIdContainer())
        profileManager.setProperty("quests", quests)
        profileManager.setProperty("events", events)
        profileManager.setProperty("loot", loot)
        profileManager.setProperty("spoils", spoils)
        profileManager.setProperty("doors", doors)
        profileManager.setProperty("cutscenes", cutscenes)
        profileManager.setProperty("isTooltipEnabled", isTooltipEnabled)
        profileManager.setProperty("isComparingEnabled", isComparingEnabled)
    }

    override fun onNotifyLoadProfile(profileManager: ProfileManager) {
        heroes = profileManager.getProperty("heroes")
        party = profileManager.getProperty("party")
        inventory = profileManager.getProperty("inventory")
        battles = profileManager.getProperty("battles")
        conversations = ConversationContainer().apply {
            val container: PhraseIdContainer = profileManager.getProperty("conversations")
            this.setCurrentPhraseIds(container)
        }
        quests = profileManager.getProperty("quests")
        events = profileManager.getProperty("events")
        loot = profileManager.getProperty("loot")
        spoils = profileManager.getProperty("spoils")
        doors = profileManager.getProperty("doors")
        cutscenes = profileManager.getProperty("cutscenes")
        isTooltipEnabled = profileManager.getProperty("isTooltipEnabled")
        isComparingEnabled = profileManager.getProperty("isComparingEnabled")
    }

    private fun addFirstHeroToParty() {
        val hero = heroes.getCertainHero(Constant.PLAYER_ID)
        heroes.removeHero(Constant.PLAYER_ID)
        party.addHero(hero)
    }

    private fun addFirstItemsToInventory() {
        val mace = createInventoryItem("basic_mace")
        val gold = createInventoryItem("gold")
        inventory.autoSetItem(mace)
        inventory.autoSetItem(gold)
    }

}

package nl.t64.cot

import nl.t64.cot.Utils.scenario
import nl.t64.cot.components.battle.BattleContainer
import nl.t64.cot.components.conversation.ConversationContainer
import nl.t64.cot.components.conversation.PhraseIdContainer
import nl.t64.cot.components.cutscene.CutsceneContainer
import nl.t64.cot.components.door.DoorContainer
import nl.t64.cot.components.event.EventContainer
import nl.t64.cot.components.loot.LootContainer
import nl.t64.cot.components.loot.ShopContainer
import nl.t64.cot.components.loot.SpoilsContainer
import nl.t64.cot.components.party.HeroContainer
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.PlayerInventoryContainer
import nl.t64.cot.components.portal.PortalContainer
import nl.t64.cot.components.quest.QuestContainer
import nl.t64.cot.components.time.Clock
import nl.t64.cot.subjects.ProfileObserver


class GameData : ProfileObserver {

    lateinit var clock: Clock
    lateinit var heroes: HeroContainer
    lateinit var party: PartyContainer
    lateinit var inventory: PlayerInventoryContainer
    lateinit var storage: InventoryContainer
    lateinit var shops: ShopContainer
    lateinit var battles: BattleContainer
    lateinit var conversations: ConversationContainer
    lateinit var quests: QuestContainer
    lateinit var events: EventContainer
    lateinit var loot: LootContainer
    lateinit var spoils: SpoilsContainer
    lateinit var doors: DoorContainer
    lateinit var cutscenes: CutsceneContainer
    lateinit var portals: PortalContainer
    var isTooltipEnabled = false
    var isComparingEnabled = false

    fun resetCycle() {
        clock.reset()
        shops = ShopContainer()
        battles = BattleContainer()
        val phraseIdToKeep = conversations.getConversationById("fairy_welcome").currentPhraseId
        conversations = ConversationContainer()
        loot = LootContainer()
        spoils = SpoilsContainer()
        quests.reset()

        conversations.getConversationById("fairy_welcome").currentPhraseId = phraseIdToKeep
    }

    override fun onNotifyCreateProfile(profileManager: ProfileManager) {
        clock = Clock()
        heroes = HeroContainer()
        party = PartyContainer()
        inventory = PlayerInventoryContainer(66)
        storage = InventoryContainer(176)
        shops = ShopContainer()
        battles = BattleContainer()
        conversations = ConversationContainer()
        quests = QuestContainer()
        events = EventContainer()
        loot = LootContainer()
        spoils = SpoilsContainer()
        doors = DoorContainer()
        cutscenes = CutsceneContainer()
        portals = PortalContainer()
        isTooltipEnabled = true
        isComparingEnabled = true
        scenario.startNewGame()
        onNotifySaveProfile(profileManager)
    }

    override fun onNotifySaveProfile(profileManager: ProfileManager) {
        profileManager.setProperty("clock", clock)
        profileManager.setProperty("heroes", heroes)
        profileManager.setProperty("party", party)
        profileManager.setProperty("inventory", inventory)
        profileManager.setProperty("storage", storage)
        profileManager.setProperty("shops", shops)
        profileManager.setProperty("battles", battles)
        profileManager.setProperty("conversations", conversations.createPhraseIdContainer())
        profileManager.setProperty("quests", quests)
        profileManager.setProperty("events", events)
        profileManager.setProperty("loot", loot)
        profileManager.setProperty("spoils", spoils)
        profileManager.setProperty("doors", doors)
        profileManager.setProperty("cutscenes", cutscenes)
        profileManager.setProperty("portals", portals)
        profileManager.setProperty("isTooltipEnabled", isTooltipEnabled)
        profileManager.setProperty("isComparingEnabled", isComparingEnabled)
    }

    override fun onNotifyLoadProfile(profileManager: ProfileManager) {
        clock = profileManager.getProperty<Clock>("clock").apply { possibleAddSomeExtraLoadingTime() }
        heroes = profileManager.getProperty("heroes")
        party = profileManager.getProperty("party")
        inventory = profileManager.getProperty("inventory")
        storage = profileManager.getProperty("storage")
        shops = profileManager.getProperty("shops")
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
        portals = profileManager.getProperty("portals")
        isTooltipEnabled = profileManager.getProperty("isTooltipEnabled")
        isComparingEnabled = profileManager.getProperty("isComparingEnabled")
    }

}

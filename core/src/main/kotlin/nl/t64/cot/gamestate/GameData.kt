package nl.t64.cot.gamestate

import nl.t64.cot.Utils.scenario
import nl.t64.cot.components.battle.BattleContainer
import nl.t64.cot.components.battle.BattleProgress
import nl.t64.cot.components.blocker.RemovedBlockerContainer
import nl.t64.cot.components.conversation.ConversationContainer
import nl.t64.cot.components.conversation.PhraseIdContainer
import nl.t64.cot.components.cutscene.CutsceneContainer
import nl.t64.cot.components.door.DoorContainer
import nl.t64.cot.components.door.DoorProgress
import nl.t64.cot.components.event.EventContainer
import nl.t64.cot.components.event.EventProgress
import nl.t64.cot.components.loot.LootContainer
import nl.t64.cot.components.loot.LootProgress
import nl.t64.cot.components.loot.ShopContainer
import nl.t64.cot.components.loot.SpoilsContainer
import nl.t64.cot.components.party.HeroContainer
import nl.t64.cot.components.party.HeroProgress
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryProgress
import nl.t64.cot.components.party.inventory.PartyInventoryContainer
import nl.t64.cot.components.portal.PortalContainer
import nl.t64.cot.components.quest.QuestContainer
import nl.t64.cot.components.time.Clock
import nl.t64.cot.subjects.ProfileObserver


private const val INVENTORY_SLOTS = 66
private const val STORAGE_SLOTS = 176

class GameData : ProfileObserver {

    lateinit var clock: Clock
    lateinit var heroes: HeroContainer
    lateinit var party: PartyContainer
    lateinit var inventory: PartyInventoryContainer
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
    lateinit var removedBlockers: RemovedBlockerContainer
    var isTooltipEnabled = false
    var isComparingEnabled = false
    var numberOfCycles = 0

    fun resetCycle() {
        clock.reset()
        shops = ShopContainer()
        battles.reset()
        conversations.reset()
        loot.reset()
        spoils = SpoilsContainer()
        quests.reset()
        cutscenes.reset()
        numberOfCycles++
    }

    override fun onNotifyCreateProfile(profileManager: ProfileManager) {
        clock = Clock()
        heroes = HeroContainer()
        party = PartyContainer()
        inventory = PartyInventoryContainer(INVENTORY_SLOTS)
        storage = InventoryContainer(STORAGE_SLOTS)
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
        removedBlockers = RemovedBlockerContainer()
        isTooltipEnabled = true
        isComparingEnabled = true
        scenario.startNewGame()
        onNotifySaveProfile(profileManager)
    }

    override fun onNotifySaveProfile(profileManager: ProfileManager) {
        profileManager.setProperty("clock", clock)
        profileManager.setProperty("heroes", heroes.toProgress())
        profileManager.setProperty("party", party.toProgress())
        profileManager.setProperty("inventory", inventory.toProgress())
        profileManager.setProperty("storage", storage.toProgress())
        profileManager.setProperty("shops", shops.toProgress())
        profileManager.setProperty("battles", battles.toProgress())
        profileManager.setProperty("conversations", conversations.createPhraseIdContainer())
        profileManager.setProperty("quests", quests)
        profileManager.setProperty("events", events.toProgress())
        profileManager.setProperty("loot", loot.toProgress())
        profileManager.setProperty("spoils", spoils)
        profileManager.setProperty("doors", doors.toProgress())
        profileManager.setProperty("cutscenes", cutscenes)
        profileManager.setProperty("portals", portals)
        profileManager.setProperty("removedBlockers", removedBlockers)
        profileManager.setProperty("isTooltipEnabled", isTooltipEnabled)
        profileManager.setProperty("isComparingEnabled", isComparingEnabled)
        profileManager.setProperty("numberOfCycles", numberOfCycles)
    }

    override fun onNotifyLoadProfile(profileManager: ProfileManager) {
        clock = profileManager.getProperty<Clock>("clock").apply { possibleAddSomeExtraLoadingTime() }
        heroes = HeroContainer().apply {
            val progress: Map<String, HeroProgress> = profileManager.getProperty("heroes")
            this.applyProgress(progress)
        }
        party = PartyContainer().apply {
            val progress: Map<String, HeroProgress> = profileManager.getProperty("party")
            this.applyProgress(progress)
        }
        inventory = PartyInventoryContainer(INVENTORY_SLOTS).apply {
            val progress: InventoryProgress = profileManager.getProperty("inventory")
            this.applyProgress(progress)
        }
        storage = InventoryContainer(STORAGE_SLOTS).apply {
            val progress: InventoryProgress = profileManager.getProperty("storage")
            this.applyProgress(progress)
        }
        shops = ShopContainer().apply {
            val progress: Map<String, InventoryProgress> = profileManager.getProperty("shops")
            this.applyProgress(progress)
        }
        battles = BattleContainer().apply {
            val progress: Map<String, BattleProgress> = profileManager.getProperty("battles")
            this.applyProgress(progress)
        }
        conversations = ConversationContainer().apply {
            val container: PhraseIdContainer = profileManager.getProperty("conversations")
            this.setCurrentPhraseIds(container)
        }
        quests = profileManager.getProperty("quests")
        events = EventContainer().apply {
            val progress: Map<String, EventProgress> = profileManager.getProperty("events")
            this.applyProgress(progress)
        }
        loot = LootContainer().apply {
            val progress: Map<String, LootProgress> = profileManager.getProperty("loot")
            this.applyProgress(progress)
        }
        spoils = profileManager.getProperty("spoils")
        doors = DoorContainer().apply {
            val progress: Map<String, DoorProgress> = profileManager.getProperty("doors")
            this.applyProgress(progress)
        }
        cutscenes = profileManager.getProperty("cutscenes")
        portals = profileManager.getProperty("portals")
        removedBlockers = profileManager.getProperty("removedBlockers")
        isTooltipEnabled = profileManager.getProperty("isTooltipEnabled")
        isComparingEnabled = profileManager.getProperty("isComparingEnabled")
        numberOfCycles = profileManager.getProperty("numberOfCycles")

        updateOutdatedSaveGame()
    }

    private fun updateOutdatedSaveGame() {
        quests.updateOutdatedData()
        cutscenes.updateOutdatedData()
        portals.updateOutdatedData()
    }

}

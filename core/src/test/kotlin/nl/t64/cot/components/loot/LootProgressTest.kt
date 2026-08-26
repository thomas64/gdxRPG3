package nl.t64.cot.components.loot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


/**
 * Loot is not stored as a whole in the save file. Only what deviates from the config is stored, so that a
 * changed config still lands in an existing save file. `changedContent` carries three different states:
 *
 * | changedContent | what happened | what the loot holds after loading |
 * |----------------|---------------|-----------------------------------|
 * | null           | untouched     | the content from the config       |
 * | empty          | fully taken   | nothing                           |
 * | filled         | partly taken  | whatever was left behind          |
 *
 * The trap, the lock and the xp work the same way: they come from the config, unless the save file says they
 * were consumed. Everything else, like the conditions and the gather level, always comes from the config.
 */
internal class LootProgressTest {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // saving: what ends up in the save file
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    fun `when loot is untouched, should store no content in save file`() {
        val untouchedLootFromSaveFile: Loot = createSomeLoot()

        val lootFromConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = untouchedLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.changedContent).isNull()
    }

    @Test
    fun `when nothing happened to the loot, should not be stored at all`() {
        val untouchedLootFromSaveFile: Loot = createSomeLoot()

        val lootFromConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = untouchedLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.isChanged()).isFalse()
    }

    @Test
    fun `when loot is emptied, should store empty content`() {
        val emptiedLootFromSaveFile: Loot = createSomeLoot()
        emptiedLootFromSaveFile.clearContent()

        val lootFromConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = emptiedLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.changedContent).isEmpty()
        assertThat(progress.isChanged()).isTrue()
    }

    @Test
    fun `when loot is partly taken, should store what is left`() {
        val partlyTakenLootFromSaveFile: Loot = createSomeLoot()
        partlyTakenLootFromSaveFile.updateContent(mutableMapOf("herb" to 1))

        val lootFromConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = partlyTakenLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.changedContent).containsExactlyInAnyOrderEntriesOf(mapOf("herb" to 1))
    }

    @Test
    fun `when trap is disarmed and lock is picked, should store both`() {
        val openedLootFromSaveFile: Loot = createSomeLoot()
        openedLootFromSaveFile.disarmTrap()
        openedLootFromSaveFile.pickLock()

        val lootFromConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = openedLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.isTrapDisarmed).isTrue()
        assertThat(progress.isLockPicked).isTrue()
    }

    @Test
    fun `when xp is gained, should store that xp is gained`() {
        val rewardedLootFromSaveFile: Loot = createSomeLoot()
        rewardedLootFromSaveFile.clearXp()

        val lootFromConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = rewardedLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.isXpGained).isTrue()
    }

    @Test
    fun `when loot never had a trap or a lock, should store nothing disarmed or picked`() {
        val untouchedLootFromSaveFile = Loot(content = mutableMapOf("herb" to 3))

        val lootFromConfig = Loot(content = mutableMapOf("herb" to 3))

        // saving
        val progress: LootProgress = untouchedLootFromSaveFile.toProgress(lootFromConfig)

        assertThat(progress.isTrapDisarmed).isFalse()
        assertThat(progress.isLockPicked).isFalse()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // loading: what the save file does to the loot that comes from the config
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    fun `when untouched loot is loaded after config changed, should use new config content`() {
        val untouchedLootFromSaveFile: Loot = createSomeLoot()

        val lootFromOldConfig: Loot = createSomeLoot()

        val lootFromNewConfig: Loot = createSomeLootWithExtraGold()

        // saving
        val progress: LootProgress = untouchedLootFromSaveFile.toProgress(lootFromOldConfig)

        // loading save file into new config
        lootFromNewConfig.applyProgress(progress)

        assertThat(lootFromNewConfig.content).containsExactlyInAnyOrderEntriesOf(mapOf("herb" to 3, "gold" to 50))
    }

    @Test
    fun `when emptied loot is loaded after config changed, should stay empty`() {
        val emptiedLootFromSaveFile: Loot = createSomeLoot()
        emptiedLootFromSaveFile.clearContent()

        val lootFromOldConfig: Loot = createSomeLoot()

        val lootFromNewConfig: Loot = createSomeLootWithExtraGold()

        // saving
        val progress: LootProgress = emptiedLootFromSaveFile.toProgress(lootFromOldConfig)

        // loading save file into new config
        lootFromNewConfig.applyProgress(progress)

        assertThat(lootFromNewConfig.isTaken()).isTrue()
    }

    @Test
    fun `when partly taken loot is loaded after config changed, should keep what was left`() {
        val partlyTakenLootFromSaveFile: Loot = createSomeLoot()
        partlyTakenLootFromSaveFile.updateContent(mutableMapOf("herb" to 1))

        val lootFromOldConfig: Loot = createSomeLoot()

        val lootFromNewConfig: Loot = createSomeLootWithExtraGold()

        // saving
        val progress: LootProgress = partlyTakenLootFromSaveFile.toProgress(lootFromOldConfig)

        // loading save file into new config
        lootFromNewConfig.applyProgress(progress)

        assertThat(lootFromNewConfig.content).containsExactlyInAnyOrderEntriesOf(mapOf("herb" to 1))
    }

    @Test
    fun `when untouched loot is loaded after lock removed from config, should keep the trap and lose the lock`() {
        val untouchedLootFromSaveFile: Loot = createSomeLoot()

        val lootFromOldConfig: Loot = createSomeLoot()

        val lootFromNewConfig: Loot = createSomeLootWithoutLock()

        // saving
        val progress: LootProgress = untouchedLootFromSaveFile.toProgress(lootFromOldConfig)

        // loading save file into new config
        lootFromNewConfig.applyProgress(progress)

        assertThat(lootFromNewConfig.isLocked()).isFalse()
        assertThat(lootFromNewConfig.isTrapped()).isTrue()
    }

    @Test
    fun `when loot with a disarmed trap and a picked lock is loaded, should stay disarmed and picked`() {
        val openedLootFromSaveFile: Loot = createSomeLoot()
        openedLootFromSaveFile.disarmTrap()
        openedLootFromSaveFile.pickLock()

        val lootFromOldConfig: Loot = createSomeLoot()

        val lootFromSameConfig: Loot = createSomeLoot()

        // saving
        val progress: LootProgress = openedLootFromSaveFile.toProgress(lootFromOldConfig)

        // loading save file into config
        lootFromSameConfig.applyProgress(progress)

        assertThat(lootFromSameConfig.isTrapped()).isFalse()
        assertThat(lootFromSameConfig.isLocked()).isFalse()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun createSomeLoot(): Loot {
        return Loot(xp = 10, content = mutableMapOf("herb" to 3), trapLevel = 2, lockLevel = 4)
    }

    private fun createSomeLootWithExtraGold(): Loot {
        return Loot(xp = 10, content = mutableMapOf("herb" to 3, "gold" to 50), trapLevel = 2, lockLevel = 4)
    }

    private fun createSomeLootWithoutLock(): Loot {
        return Loot(xp = 10, content = mutableMapOf("herb" to 3), trapLevel = 2, lockLevel = 0)
    }

}

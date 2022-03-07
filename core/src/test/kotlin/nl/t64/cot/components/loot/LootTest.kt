package nl.t64.cot.components.loot

import nl.t64.cot.GameTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class LootTest : GameTest() {

    private lateinit var lootContainer: LootContainer

    @BeforeEach
    private fun beforeEach() {
        lootContainer = LootContainer()
    }

    @Test
    fun whenSparklesAreCreated_ShouldHaveCorrectDataInside() {
        val sparkle = lootContainer.getLoot("sparkle0001")
        val expected = mapOf(Pair("gemstone", 1))
        assertThat(sparkle.isTaken()).isFalse
        assertThat(sparkle.content).hasSize(1)
        val (key, value) = expected.iterator().next()
        assertThat(sparkle.content).containsOnly(entry(key, value))
    }

    @Test
    fun whenChestsAreCreated_ShouldHaveCorrectDataInside() {
        val chest = lootContainer.getLoot("chest0001")
        val expected = mapOf(Pair("healing_potion", 3))
        assertThat(chest.isTaken()).isFalse
        assertThat(chest.isTrapped()).isFalse
        assertThat(chest.isLocked()).isFalse
        assertThat(chest.content).hasSize(1)
        val (key, value) = expected.iterator().next()
        assertThat(chest.content).containsOnly(entry(key, value))
    }

    @Test
    fun whenQuestsAreCreated_ShouldHaveCorrectDataInside() {
        val reward = lootContainer.getLoot("quest0001")
        assertThat(reward.isTaken()).isFalse
        assertThat(reward.content).hasSize(1)
        assertThat(reward.xp).isEqualTo(2)
        reward.clearXp()
        assertThat(reward.content).hasSize(1)
        assertThat(reward.xp).isZero
    }

    @Test
    fun whenLootIdDoesNotExistLikeInQuestWithNoReward_ShouldReturnEmptyReward() {
        val reward = lootContainer.getLoot("doesNotExist")
        assertThat(reward.content).isEmpty()
        assertThat(reward.xp).isZero
        assertThat(reward.isTaken()).isTrue
        assertThat(reward.isTrapped()).isFalse
        assertThat(reward.canDisarmTrap(0)).isTrue
        assertThat(reward.isLocked()).isFalse
        assertThat(reward.canPickLock(0)).isTrue
    }

    @Test
    fun whenChestsHasTrapOrLock_ShouldNeedToDisarmOrPick() {
        val thieflevel = 8
        val mechaniclevel = 8
        val chest = lootContainer.getLoot("chest0005")
        assertThat(chest.isTaken()).isFalse
        assertThat(chest.isTrapped()).isTrue
        assertThat(chest.trapLevel).isEqualTo(6)
        assertThat(chest.canDisarmTrap(mechaniclevel)).isTrue
        assertThat(chest.isLocked()).isTrue
        assertThat(chest.lockLevel).isEqualTo(10)
        assertThat(chest.canPickLock(thieflevel)).isFalse
        assertThat(chest.content).hasSize(1)
        assertThat(chest.content).isEqualTo(mapOf(Pair("gold", 1000)))
        chest.disarmTrap()
        chest.pickLock()
        chest.clearContent()
        assertThat(chest.isTaken()).isTrue
        assertThat(chest.isTrapped()).isFalse
        assertThat(chest.trapLevel).isZero
        assertThat(chest.canDisarmTrap(mechaniclevel)).isTrue
        assertThat(chest.isLocked()).isFalse
        assertThat(chest.lockLevel).isZero
        assertThat(chest.canPickLock(thieflevel)).isTrue
        assertThat(chest.content).isEmpty()
        chest.updateContent(mutableMapOf(Pair("herb", 10)))
        assertThat(chest.isTaken()).isFalse
        assertThat(chest.isTrapped()).isFalse
        assertThat(chest.trapLevel).isZero
        assertThat(chest.canDisarmTrap(mechaniclevel)).isTrue
        assertThat(chest.isLocked()).isFalse
        assertThat(chest.lockLevel).isZero
        assertThat(chest.canPickLock(thieflevel)).isTrue
        assertThat(chest.content).hasSize(1)
    }

    @Test
    fun whenQuestHasBonusRemoved_ShouldRemoveBonus() {
        val reward = lootContainer.getLoot("quest0006")
        assertThat(reward.isTaken()).isFalse
        assertThat(reward.content).hasSize(3)
        assertThat(reward.content).isEqualTo(mapOf(Pair("gold", 1), Pair("bonus_gold", 1), Pair("bonus_herb", 1)))
        assertThat(reward.xp).isEqualTo(2)
        reward.removeBonus()
        assertThat(reward.content).hasSize(1)
        assertThat(reward.content).isEqualTo(mapOf(Pair("gold", 1)))
    }

    @Test
    fun whenQuestHasBonusHandled_ShouldMergeAndRemoveBonus() {
        val reward = lootContainer.getLoot("quest0006")
        assertThat(reward.isTaken()).isFalse
        assertThat(reward.content).hasSize(3)
        assertThat(reward.content).isEqualTo(mapOf(Pair("gold", 1), Pair("bonus_gold", 1), Pair("bonus_herb", 1)))
        assertThat(reward.xp).isEqualTo(2)
        reward.handleBonus()
        assertThat(reward.content).hasSize(2)
        assertThat(reward.content).isEqualTo(mapOf(Pair("gold", 2), Pair("herb", 1)))
    }

}

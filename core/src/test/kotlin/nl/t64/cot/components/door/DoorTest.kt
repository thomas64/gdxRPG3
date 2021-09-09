package nl.t64.cot.components.door

import nl.t64.cot.GameTest
import nl.t64.cot.audio.AudioEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class DoorTest : GameTest() {

    private lateinit var doorContainer: DoorContainer

    @BeforeEach
    private fun setup() {
        doorContainer = DoorContainer()
    }

    @Test
    fun whenDoorsAreCreated_ShouldHaveCorrectDataInside() {
        val door = doorContainer.getDoor("door_simple_left")
        assertThat(door.type).isEqualTo(DoorType.SMALL)
        assertThat(door.spriteId).isEqualTo("simple_left")
        assertThat(door.keyId).isNull()
        assertThat(door.audio).isEqualTo(AudioEvent.SE_SMALL_DOOR)
        assertThat(door.width).isEqualTo(48f)
        assertThat(door.height).isEqualTo(96f)

        assertThat(door.isLocked).isFalse
        assertThat(door.isClosed).isTrue
        door.open()
        assertThat(door.isClosed).isFalse
        door.close()
        assertThat(door.isClosed).isTrue
    }

    @Test
    fun whenDoorsWithLocksAreCreated_ShouldHaveCorrectDataInside() {
        val door = doorContainer.getDoor("door_gate_mysterious_tunnel")
        assertThat(door.type).isEqualTo(DoorType.GATE)
        assertThat(door.spriteId).isEqualTo("gate_with_lock")
        assertThat(door.keyId).contains("key_mysterious_tunnel")
        assertThat(door.audio).isEqualTo(AudioEvent.SE_METAL_GATE)
        assertThat(door.width).isEqualTo(48f)
        assertThat(door.height).isEqualTo(96f)

        assertThat(door.isLocked).isTrue
        assertThat(door.isClosed).isTrue
        door.unlock()
        assertThat(door.isLocked).isFalse
        assertThat(door.isClosed).isTrue
    }

    @Test
    fun testForCoverage_UnusedConstructorsByJackson() {
        val door1 = Door(DoorType.LARGE, "large_round")
        val door2 = Door(DoorType.GATE, "gate_with_lock", "key_mysterious_tunnel")
        assertThat(door1.keyId).isNull()
        assertThat(door2.keyId).contains("key_mysterious_tunnel")
    }

}

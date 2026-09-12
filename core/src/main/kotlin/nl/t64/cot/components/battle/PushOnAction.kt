package nl.t64.cot.components.battle


const val PUSH_ON_SP: Int = 10
private const val PUSH_ON_AP: Int = 1

class PushOnAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character
    private val message = """
        Pushing on will give ${character.name}
        $PUSH_ON_AP extra AP for ${character.gender} current turn.

        """

    fun isAble(): Pair<Boolean, String> {
        return when {
            currentParticipant.currentAP >= MAXIMUM_AP -> {
                Pair(false, (message + "Already at maximum AP!").trimIndent())
            }
            character.currentSp < PUSH_ON_SP -> {
                Pair(false, (message + "Not enough SP!").trimIndent())
            }
            else -> {
                Pair(true, (message + "Do you want to push on? ($PUSH_ON_SP SP)").trimIndent())
            }
        }
    }

    fun handle(): String {
        character.currentSp -= PUSH_ON_SP
        currentParticipant.currentAP += PUSH_ON_AP
        return "+$PUSH_ON_AP AP"
    }

}

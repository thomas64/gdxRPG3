package nl.t64.cot.screens.loot

import com.badlogic.gdx.utils.Null
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


class RewardScreen : LootScreen() {

    @Null
    private var levelUpMessage: String? = null

    companion object {
        fun load(reward: Loot, @Null levelUpMessage: String?) {
            val rewardScreen = screenManager.getScreen(ScreenType.REWARD) as RewardScreen
            rewardScreen.loot = reward
            rewardScreen.lootTitle = "   Reward"
            rewardScreen.levelUpMessage = levelUpMessage
            screenManager.openParchmentLoadScreen(ScreenType.REWARD)
        }
    }

    override fun show() {
        super.show()
        levelUpMessage?.let { showLevelUpMessage() }
    }

    override fun resolveAfterClearingContent() {
        brokerManager.lootObservers.notifyRewardTaken()
    }

    private fun showLevelUpMessage() {
        val messageDialog = MessageDialog(levelUpMessage!!)
        messageDialog.setLeftAlignment()
        messageDialog.show(stage, AudioEvent.SE_LEVELUP)
    }

}

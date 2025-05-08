package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.menu.MenuPause
import nl.t64.cot.screens.world.Camera
import kotlin.concurrent.thread


class BattleScreen : Screen {

    private lateinit var battleObserver: BattleSubject
    private lateinit var battleId: String
    private lateinit var stage: Stage
    private lateinit var currentBgm: AudioEvent

    private lateinit var enemies: EnemyContainer
    private lateinit var turnManager: TurnManager
    private lateinit var currentParticipant: Participant
    private lateinit var currentTarget: Participant

    private lateinit var battleField: BattleField
    private lateinit var tableManager: BattleTableManager
    private lateinit var menuManager: BattleMenuManager
    private lateinit var dialogManager: BattleDialogManager

    private val screenBuilder = BattleScreenBuilder()
    private val shapeRenderer = ShapeRenderer()

    private var isBgmFading: Boolean = false
    private var isLoaded: Boolean = false
    private var isPreBattle: Boolean = false
    private var isDelayingTurn: Boolean = false
    private var isEnemyActing: Boolean = false
    private var hasWon: Boolean = false
    private var hasLost: Boolean = false
    private var shouldKeepState: Boolean = false

    private val listeners = BattleListeners().apply {
        setScreenListeners(
            ::winBattle, ::openPauseMenu, ::startBattle, ::endTurn,
            ::heroIsSelectedForPrePreview, ::heroIsSelectedForReposition,
            ::showInventoryScreenPreBattle, ::showInventoryScreen,
            ::showDelayTurnDialog, ::showFleeDialog, ::showPreviewDialog,
            ::showConfirmRestDialog, ::showConfirmMoveDialog, ::showConfirmAttackDialog,
            ::showConfirmPotionDialog, ::showConfirmWeaponDialog
        )
    }

    companion object {
        fun load(battleId: String, battleObserver: BattleObserver) {
            val screen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
            screen.battleObserver = BattleSubject(battleObserver)
            screen.battleId = battleId
            screen.currentBgm = AudioEvent.getRandomBattleMusic()
            screen.shouldKeepState = false
            screenManager.setScreen(ScreenType.BATTLE)
        }
    }

    override fun show() {
        playBgm(currentBgm)
        if (shouldKeepState) {
            Gdx.input.inputProcessor = stage
            Utils.setGamepadInputProcessor(stage)
            shouldKeepState = false
            return
        }

        enemies = EnemyContainer(battleId)
        turnManager = TurnManager(gameData.party.getAllHeroesAlive(), enemies.getAll())
        currentParticipant = turnManager.participants.first { it.character.id == Constant.PLAYER_ID }

        isLoaded = false
        isPreBattle = false
        hasWon = false
        hasLost = false

        val camera = Camera()
        stage = Stage(camera.viewport)

        battleField = BattleField(turnManager.participants, { currentParticipant })
        tableManager = BattleTableManager(stage, screenBuilder, { currentParticipant })
        menuManager = BattleMenuManager(stage, screenBuilder, listeners, turnManager, battleField, { currentParticipant })
        dialogManager = BattleDialogManager(stage, { currentParticipant })

        val battleTitle = screenBuilder.createBattleTitle()
        stage.addActor(battleTitle)

        stage.addAction(Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.fadeIn(Constant.FADE_DURATION),
                Actions.delay(1f),
                Actions.fadeOut(Constant.FADE_DURATION),
                Actions.visible(false)
            ), battleTitle),
            Actions.delay(2.1f),
            Actions.run {
                camera.zoom = 1f
                Gdx.input.inputProcessor = stage
                Utils.setGamepadInputProcessor(stage)
                stage.addActor(Utils.createBattleBack(battleId))
                isLoaded = true
                menuManager.setupPreBattleTable()
                isPreBattle = true
                render(0f)

                listOf("guide_event_battle_1",
                       "guide_event_battle_2",
                       "guide_event_battle_3")
                    .map { gameData.events.getEventById(it) }
                    .firstOrNull { !gameData.events.hasEventPlayed(it) }
                    ?.possibleStart(stage)
            }
        ))
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)

        stage.act(dt)
        handleAudioFading()
        stage.draw()

        if (!isLoaded || isBgmFading || isDelayingTurn || hasWon || hasLost) {
            return
        }

        if (stage.actors.items.any { it is Dialog }) {
            return
        }

        tableManager.updateHeroTable(gameData.party.getAllHeroes(), { turnManager.getParticipant(it).currentAP })
        tableManager.updateEnemyTable(enemies.getAll())
        tableManager.updateTurnTable(turnManager)

        if (isPreBattle) {
            tableManager.updateBattleField(battleField)
            return
        }
        currentParticipant = turnManager.currentParticipant
        tableManager.updateBattleField(battleField)

        when {
            enemies.getAll().none { it.isAlive } -> winBattle()
            gameData.party.getPlayer().isDead -> gameOver()
            currentParticipant.isHero -> takeTurnHero()
            else -> takeTurnEnemy()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun resize(width: Int, height: Int) {
        // empty
    }

    override fun pause() {
        // empty
    }

    override fun resume() {
        // empty
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        if (shouldKeepState) return
        stage.clear()
    }

    override fun dispose() {
        screenBuilder.dispose()
        stage.dispose()
        shapeRenderer.dispose()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun handleAudioFading() {
        if (isBgmFading) {
            audioManager.certainFadeBgmBgs()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun startBattle() {
        screenBuilder.buttonTableMainMenuIndex = 0
        menuManager.buttonTablePreBattle.remove()
        isPreBattle = false
    }

    private fun heroIsSelectedForReposition(selectedHero: String) {
        currentParticipant = turnManager.participants.first { it.character.name == selectedHero }
        menuManager.heroIsSelectedForReposition()
    }

    private fun heroIsSelectedForPrePreview(selectedHero: String) {
        currentParticipant = turnManager.participants.first { it.character.name == selectedHero }
        menuManager.heroIsSelectedForPrePreview()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showConfirmMoveDialog() {
        dialogManager.showConfirmMoveDialog(battleField = battleField,
                                            onConfirmed = { it.handle(); menuManager.returnToAction() },
                                            onCancelled = { menuManager.returnToAction() })
    }

    private fun showPreviewDialog(selectedAttack: BattleAbilityItem, selectedTarget: String) {
        val target: Participant = turnManager.getParticipant(enemies.getEnemy(selectedTarget))
        dialogManager.showPreviewDialog(selectedAttack = selectedAttack,
                                        selectedTarget = target)
    }

    private fun showConfirmAttackDialog(selectedAttack: BattleAbilityItem, selectedTarget: String) {
        val target: Participant = turnManager.getParticipant(enemies.getEnemy(selectedTarget))
        dialogManager.showConfirmAttackDialog(selectedAttack = selectedAttack,
                                              selectedTarget = target,
                                              onConfirmed = { attackConfirmed(it, target) })
    }

    private fun showConfirmPotionDialog(selectedPotion: BattlePotionItem) {
        dialogManager.showConfirmPotionDialog(selectedPotion = selectedPotion,
                                              onConfirmed = { potionConfirmed(it) })
    }

    private fun showConfirmWeaponDialog(selectedWeapon: BattleWeaponItem) {
        dialogManager.showConfirmWeaponDialog(selectedWeapon = selectedWeapon,
                                              enemies = turnManager.getOnlyEnemies(),
                                              onConfirmed = { weaponConfirmed(it) })
    }

    private fun showFleeDialog() {
        dialogManager.showFleeDialog(battleId = battleId,
                                     onConfirmed = { fleeConfirmed(it) })
    }

    private fun showDelayTurnDialog() {
        dialogManager.showDelayTurnDialog(turnManager = turnManager,
                                          onConfirmed = { delayTurnConfirmed(it) })
    }

    private fun showConfirmRestDialog() {
        dialogManager.showConfirmRestDialog(onConfirmed = { restConfirmed(it) })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun attackConfirmed(attackAction: AttackAction, target: Participant) {
        currentTarget = target
        val messages: ArrayDeque<String> = attackAction.handle()!!
        menuManager.buttonTableTarget.remove()
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            showMessages(messages)
            turnManager.removeKilledParticipants()
        }
    }

    private fun potionConfirmed(potionAction: PotionAction) {
        menuManager.buttonTablePotion.remove()
        val (message, audioEvent) = potionAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, audioEvent)
        }
    }

    private fun weaponConfirmed(weaponAction: WeaponAction) {
        menuManager.buttonTableWeapon.remove()
        val message: String = weaponAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    private fun fleeConfirmed(fleeAction: FleeAction) {
        screenBuilder.buttonTableMainMenuIndex = 0
        menuManager.buttonTableAction.remove()
        val (isSuccess, message) = fleeAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            if (isSuccess) {
                battleFledExitScreen()
            }
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    private fun delayTurnConfirmed(delayTurnAction: DelayTurnAction) {
        screenBuilder.buttonTableMainMenuIndex = 0
        menuManager.buttonTableAction.remove()
        isDelayingTurn = true
        val message = delayTurnAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            isDelayingTurn = false
        }
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    private fun restConfirmed(restAction: RestAction) {
        screenBuilder.buttonTableMainMenuIndex = 0
        menuManager.buttonTableAction.remove()
        val (message, audioEvent) = restAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, audioEvent, 0.25f)
        }
    }

    private fun endTurn() {
        screenBuilder.buttonTableMainMenuIndex = 0
        menuManager.buttonTableAction.remove()
        val message = EndTurnAction(currentParticipant).handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun takeTurnHero() {
        if (isDelayingTurn) return
        menuManager.possibleSetupActionTable()
    }

    private fun takeTurnEnemy() {
        if (isEnemyActing) return
        isEnemyActing = true
        thread {
            runCatching {
                enemyAction()
            }.onFailure {
                if (preferenceManager.isInDebugMode) it.printStackTrace()
            }.also {
                isEnemyActing = false
            }
        }
    }

    private fun enemyAction() {
        if (currentParticipant.currentAP == currentParticipant.maximumAP) {
            Thread.sleep(1000L)
        }
        val heroTarget: Participant? = battleField.possibleGetHeroTargetAndMoveEnemy()
        battleField.resetStartingSpace()
        isDelayingTurn = true
        val messages: ArrayDeque<String> = heroTarget
            ?.let { AttackAction.createForEnemy(currentParticipant, it, battleId).handle() }
            ?.also { currentTarget = heroTarget }
            ?: currentParticipant.getEndingTurnMessage()
        val isTurnEnded = messages.any {
            (it.contains("ended") && it.contains("turn")) || (it.contains("is staggered"))
        }
        if (isTurnEnded) {
            showMessages(messages)
            Thread.sleep(1000L)
            turnManager.setNextTurn()
        } else {
            showMessages(messages)
            turnManager.removeKilledParticipants()
        }
    }

    private fun openPauseMenu() {
        shouldKeepState = true
        MenuPause.loadForBattle()
    }

    private fun showInventoryScreenPreBattle() {
        shouldKeepState = true
        InventoryScreen.loadForPreBattle()
    }

    private fun showInventoryScreen() {
        shouldKeepState = true
        InventoryScreen.loadForBattle()
    }

    private fun showMessages(messages: ArrayDeque<String>) {
        if (messages.isEmpty()) {
            isDelayingTurn = false
            return
        }

        val message: String = messages.removeFirst()
        val messageDialog = MessageDialog(message)
        if (messages.isNotEmpty()) {
            messageDialog.disableClosingSound()
        }
        messageDialog.setActionAfterHide {
            showMessages(messages)
        }
        messageDialog.show(stage, getAudioEventBasedOn(message))
        if (message.contains("did") && message.contains("damage.")) {
            ShakeEffect(tableManager.battleFieldTable, currentTarget.character.name).start()
        }
    }

    private fun getAudioEventBasedOn(message: String): AudioEvent {
        return when {
            message.contains("broke!") -> AudioEvent.SE_WEAPON_BREAK // todo, not when enemy shield breaks.
            message.contains("blocked the attack.") -> AudioEvent.SE_BLOCK
            message.contains("attack failed.") -> AudioEvent.SE_DODGE
            message.contains("A critical hit!") -> AudioEvent.SE_CRIT_HIT
            message.contains("did") && message.contains("damage.") -> AudioEvent.SE_DAMAGE
            message.contains("is defeated.") -> AudioEvent.SE_VANISH
            else -> AudioEvent.SE_CONVERSATION_NEXT
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun winBattle() {
        if (isDelayingTurn || hasWon) return
        hasWon = true

        stage.addAction(Actions.sequence(
            Actions.run { isBgmFading = true },
            Actions.delay(Constant.FADE_DURATION),
            Actions.run { isBgmFading = false },
            Actions.run { stopAllBgm() },
            Actions.run { playBgm(AudioEvent.BGM_WIN_BATTLE, false) },
            Actions.run {
                gameData.battles.setBattleWon(battleId)

                val totalXpWon = enemies.getTotalXp()
                gameData.party.gainXp(totalXpWon)
                val winMessage = """
                    The enemy is defeated!
                    Party gained $totalXpWon XP.""".trimIndent()

                val messageDialog = MessageDialog(winMessage)
                messageDialog.setActionAfterHide { battleWonExitScreen() }
                messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
            }
        ))
    }

    private fun battleWonExitScreen() {
        gameData.clock.takeHalfHour()
        exitScreen { battleObserver.notifyBattleWon(battleId, enemies.getSpoils()) }
    }

    private fun battleFledExitScreen() {
        screenBuilder.buttonTableMainMenuIndex = 0
        gameData.clock.takeQuarterHour()
        exitScreen { battleObserver.notifyBattleFled() }
    }

    private fun gameOver() {
        if (isDelayingTurn) return
        hasLost = true

        stage.addAction(Actions.sequence(
            Actions.run { isBgmFading = true },
            Actions.delay(Constant.FADE_DURATION),
            Actions.run { isBgmFading = false },
            Actions.run { stopAllBgm() },
            Actions.run { playBgm(AudioEvent.BGM_LOSE_BATTLE, false) },
            Actions.run {
                val messageDialog = MessageDialog(createDeathMessage())
                messageDialog.setActionAfterHide { gameOverExitScreen() }
                messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
            }
        ))
    }

    private fun createDeathMessage(): String {
        val currentCycle = gameData.numberOfCycles
        val isFacingArdorOrOrcGenerals = enemies.getAll().all { it.id in listOf("orc_general", "ardor") }

        return when {

            currentCycle in 1..3 && isFacingArdorOrOrcGenerals -> """
                Mozes is knocked down.

                The fight is over.""".trimIndent()

            else -> """
                Mozes took a fatal blow.

                Game Over.""".trimIndent()
        }
    }

    private fun gameOverExitScreen() {
        exitScreen { battleObserver.notifyBattleLost() }
    }

    private fun exitScreen(actionAfterExit: () -> Unit) {
        stage.addAction(Actions.sequence(
            Actions.run {
                Gdx.input.inputProcessor = null
                Utils.setGamepadInputProcessor(null)
            },
            Actions.run { isBgmFading = true },
            Actions.fadeOut(Constant.FADE_DURATION),
            Actions.run { isBgmFading = false },
            Actions.run { stopAllBgm() },
            Actions.run { actionAfterExit.invoke() }
        ))
    }

}

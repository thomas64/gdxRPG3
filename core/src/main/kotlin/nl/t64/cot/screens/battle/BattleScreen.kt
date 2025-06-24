package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
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

    private lateinit var battleField: BattleField
    private lateinit var tableManager: BattleTableManager
    private lateinit var menuManager: BattleMenuManager
    private lateinit var dialogManager: BattleDialogManager
    private lateinit var confirmManager: BattleConfirmManager
    private lateinit var resultManager: BattleResultManager

    private lateinit var currentTarget: Participant

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

        isLoaded = false
        isPreBattle = false
        hasWon = false
        hasLost = false

        val camera = Camera()
        stage = Stage(camera.viewport)

        enemies = EnemyContainer(battleId)
        turnManager = TurnManager(gameData.party.getAllHeroesAlive(), enemies.getAll())
        currentParticipant = turnManager.participants.first { it.character.id == Constant.PLAYER_ID }

        battleField = BattleField(turnManager.participants, ::currentParticipant)
        tableManager = BattleTableManager(stage, screenBuilder, ::currentParticipant)
        menuManager = BattleMenuManager(stage, screenBuilder, turnManager, battleField, ::currentParticipant)
        setMenuManagerListeners()
        dialogManager = BattleDialogManager(stage, ::currentParticipant)
        confirmManager = BattleConfirmManager(stage, { isDelayingTurn = it })
        resultManager = BattleResultManager(stage, battleObserver, battleId, enemies, { isBgmFading = it })

        val battleTitle: Label = screenBuilder.createBattleTitle()
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
                screenBuilder.buttonTableMainMenuIndex = 0
                menuManager.setupPreBattleTable()
                isPreBattle = true
                isLoaded = true
                render(0f)

                listOf("guide_event_battle_rows",
                       "guide_event_battle_advantage",
                       "guide_event_battle_battle_lock",
                       "guide_event_battle_ap",
                       "guide_event_battle_turn_order",
                       "guide_event_battle_durability")
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

        tableManager.updateHeroTable(gameData.party.getAllHeroes(), turnManager::getCurrentApOf)
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
        dialogManager.showDelayTurnDialog(onConfirmed = { delayTurnConfirmed(it) })
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
        confirmManager.potionConfirmed(potionAction)
    }

    private fun weaponConfirmed(weaponAction: WeaponAction) {
        menuManager.buttonTableWeapon.remove()
        confirmManager.weaponConfirmed(weaponAction)
    }

    private fun fleeConfirmed(fleeAction: FleeAction) {
        menuManager.buttonTableAction.remove()
        confirmManager.fleeConfirmed(fleeAction, resultManager)
    }

    private fun delayTurnConfirmed(delayTurnAction: DelayTurnAction) {
        menuManager.buttonTableAction.remove()
        confirmManager.delayTurnConfirmed(delayTurnAction, turnManager)
    }

    private fun restConfirmed(restAction: RestAction) {
        menuManager.buttonTableAction.remove()
        confirmManager.restConfirmed(restAction, turnManager)
    }

    private fun endTurn() {
        menuManager.buttonTableAction.remove()
        confirmManager.endTurn(currentParticipant, turnManager)
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
        handleEffectBasedOn(message)

        val messageDialog = MessageDialog(message)
        if (messages.isNotEmpty()) {
            messageDialog.disableClosingSound()
        }
        messageDialog.setActionAfterHide {
            showMessages(messages)
        }
        messageDialog.show(stage, getAudioEventBasedOn(message))
    }

    private fun handleEffectBasedOn(message: String) {
        when {
            message.contains("is defeated.") -> {
                FadeEffect(tableManager.battleFieldTable, currentTarget.character.name).start()
            }
            message.contains("did") && message.contains("damage.") -> {
                ShakeEffect(tableManager.battleFieldTable, currentTarget.character.name).start()
            }
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
        resultManager.winBattle()
    }

    private fun gameOver() {
        if (isDelayingTurn) return
        hasLost = true
        resultManager.gameOver()
    }

    private fun setMenuManagerListeners() {
        menuManager.setListeners(
            ::winBattle,
            ::openPauseMenu,
            ::showInventoryScreenPreBattle,
            ::startBattle,
            ::heroIsSelectedForPrePreview,
            ::showPreviewDialog,
            ::showInventoryScreen,
            ::showFleeDialog,
            ::showDelayTurnDialog,
            ::showConfirmRestDialog,
            ::endTurn,
            ::showConfirmMoveDialog,
            ::showConfirmAttackDialog,
            ::showConfirmPotionDialog,
            ::showConfirmWeaponDialog
        )
    }

}

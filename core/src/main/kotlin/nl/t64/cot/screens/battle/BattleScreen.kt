package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.menu.MenuPause
import nl.t64.cot.screens.world.Camera
import kotlin.concurrent.thread
import kotlin.math.roundToInt


class BattleScreen : Screen {

    private lateinit var battleObserver: BattleSubject
    private lateinit var battleId: String
    private lateinit var stage: Stage
    private lateinit var currentBgm: AudioEvent

    private lateinit var enemies: EnemyContainer
    private lateinit var turnManager: TurnManager
    private lateinit var preBattleSelectedHero: Participant

    private lateinit var battleField: BattleField
    private lateinit var tableManager: BattleTableManager
    private lateinit var menuManager: BattleMenuManager
    private lateinit var dialogManager: BattleDialogManager
    private lateinit var confirmManager: BattleConfirmManager
    private lateinit var attackOutcomeManager: AttackOutcomeManager
    private lateinit var specialOutcomeManager: SpecialOutcomeManager
    private lateinit var resultManager: BattleResultManager

    private val currentParticipant: Participant
        get() = if (isPreBattle) preBattleSelectedHero else turnManager.currentParticipant

    private val screenBuilder = BattleScreenBuilder()
    private val shapeRenderer = ShapeRenderer()

    private var isBgmFading: Boolean = false
    private var isLoaded: Boolean = false
    private var isPreBattle: Boolean = false
    @Volatile
    private var isDelayingTurn: Boolean = false
    private var hasChosenToContinuePerforming: Boolean = false
    @Volatile
    private var isEnemyActing: Boolean = false
    private var turnOfLastBlink: Int = -1
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

        Label("preLoadFont", LabelStyle(FontProvider.fffTusjBold200, Color.BLACK))

        enemies = EnemyContainer(battleId)
        turnManager = TurnManager(gameData.party.getAllHeroesAlive(), enemies.getAll())
        preBattleSelectedHero = turnManager.participants.first { it.character.id == Constant.PLAYER_ID }
        if (preferenceManager.isDebugModeOn) printCombatPowers()

        battleField = BattleField(turnManager.participants, ::currentParticipant)
        tableManager = BattleTableManager(stage, screenBuilder, ::currentParticipant)
        menuManager = BattleMenuManager(stage, screenBuilder, turnManager, battleField, ::currentParticipant)
        setMenuManagerListeners()
        dialogManager = BattleDialogManager(stage, turnManager, ::currentParticipant, { isDelayingTurn = it }, { hasChosenToContinuePerforming = it })
        confirmManager = BattleConfirmManager(stage, turnManager, tableManager::battleFieldTable, ::currentParticipant, { isDelayingTurn = it })
        attackOutcomeManager = AttackOutcomeManager(stage, turnManager, tableManager::battleFieldTable, { isDelayingTurn = it })
        specialOutcomeManager = SpecialOutcomeManager(tableManager::battleFieldTable, { isDelayingTurn = it })
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

                sequenceOf(listOf("guide_event_battle_rows",
                                  "guide_event_battle_advantage",
                                  "guide_event_battle_battle_lock",
                                  "guide_event_battle_ap",
                                  "guide_event_battle_turn_order",
                                  "guide_event_battle_turn_order_visibility",
                                  "guide_event_battle_special",
                                  "guide_event_battle_durability"),
                           if (gameData.party.getAllHeroesAlive().size > 1)
                               listOf("guide_event_battle_enemy_focus") else emptyList()
                )
                    .flatten()
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

        updateAllTables()

        when {
            isPreBattle -> return
            enemies.getAll().none { it.isAlive } -> winBattle()
            gameData.party.getPlayer().isDead -> gameOver()
            currentParticipant.isHero -> takeTurnHero()
            else -> takeTurnEnemy()
        }
    }

    private fun printCombatPowers() {
        val heroes: List<HeroItem> = gameData.party.getAllHeroesAlive()
        val allEnemies: List<EnemyItem> = enemies.getAll()

        println("Combat powers for battle '$battleId':")
        heroes.forEach { println("  hero  ${it.name}: ${it.getCombatPower().roundToInt()}") }
        allEnemies.forEach { println("  enemy ${it.name}: ${it.getCombatPower().roundToInt()}") }
        println("  party total: ${heroes.sumOf { it.getCombatPower().toDouble() }.roundToInt()}")
        println("  enemy total: ${allEnemies.sumOf { it.getCombatPower().toDouble() }.roundToInt()}")
    }

    private fun updateAllTables() {
        tableManager.updateHeroTable(gameData.party.getAllHeroes(), turnManager::getCurrentApOf)
        tableManager.updateEnemyTable(enemies.getAll(), turnManager::getCurrentApOf)
        val visionSlots = (gameData.party.size + 2) + gameData.inventory.getTotalOfItem("vision_thingy")
        tableManager.updateTurnTable(turnManager, visionSlots)
        tableManager.updateBattleField(battleField)
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
        hasChosenToContinuePerforming = false
        turnOfLastBlink = -1
        turnManager.resetTemporaryBonusesAfterBattle()
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

        val labelStyle = LabelStyle(FontProvider.fffTusjBold200, Color.BLACK)
        val battleStartLabel = Label("Battle  Start", labelStyle)
        val centerY: Float = (Gdx.graphics.height / 2f) - (battleStartLabel.height / 2f)
        val centerX: Float = (Gdx.graphics.width / 2f) - (battleStartLabel.width / 2f)
        battleStartLabel.setPosition(Gdx.graphics.width.toFloat(), centerY)

        playSe(AudioEvent.SE_BATTLE_START)
        stage.addActor(battleStartLabel)
        stage.addAction(Actions.sequence(
            Actions.addAction(Actions.moveTo(centerX, centerY, 0.5f), battleStartLabel),
            Actions.delay(0.5f),
            Actions.addAction(Actions.fadeOut(1f), battleStartLabel),
            Actions.delay(1.5f),
            Actions.run {
                battleStartLabel.remove()
                isPreBattle = false
            }
        ))
    }

    private fun heroIsSelectedForPreEquipment(selectedHero: String) {
        preBattleSelectedHero = turnManager.getParticipant(selectedHero)
        menuManager.aHeroIsSelectedInPreviewEquipmentInPreBattle()
    }

    private fun heroIsSelectedForPrePotion(selectedHero: String) {
        preBattleSelectedHero = turnManager.getParticipant(selectedHero)
        menuManager.aHeroIsSelectedInPotionInPreBattle()
    }

    private fun heroIsSelectedForPrePreview(selectedHero: String) {
        preBattleSelectedHero = turnManager.getParticipant(selectedHero)
        menuManager.aHeroIsSelectedInPreviewAttacksInPreBattle()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showPreviewDialog(selectedAttack: BattleAbilityItem, selectedTarget: String) {
        val target: Participant = turnManager.getParticipant(selectedTarget)
        dialogManager.showPreviewDialog(selectedAttack = selectedAttack,
                                        selectedTarget = target)
    }

    private fun showConfirmAttackDialog(selectedAttack: BattleAbilityItem, selectedTarget: String) {
        val target: Participant = turnManager.getParticipant(selectedTarget)
        dialogManager.showConfirmAttackDialog(selectedAttack = selectedAttack,
                                              selectedTarget = target,
                                              onConfirmed = { attackConfirmed(it) })
    }

    private fun showConfirmSpecialDialog(selectedSpecial: BattleAbilityItem, selectedTarget: String) {
        val target: Participant =
            if (selectedTarget in listOf("All enemies", "All allies")) {
                currentParticipant // dummy, target is not used in this case
            } else {
                turnManager.getParticipant(selectedTarget)
            }
        dialogManager.showConfirmSpecialDialog(selectedSpecial = selectedSpecial,
                                               selectedTarget = target,
                                               onConfirmed = { specialConfirmed(it) })
    }

    private fun showConfirmPotionDialogPreBattle(selectedPotion: BattlePotionItem) {
        dialogManager.showConfirmPotionDialogPreBattle(selectedPotion = selectedPotion,
                                                       onConfirmed = { potionPreBattleConfirmed(it) })
    }

    private fun showConfirmPotionDialog(selectedPotion: BattlePotionItem) {
        dialogManager.showConfirmPotionDialog(selectedPotion = selectedPotion,
                                              onConfirmed = { potionConfirmed(it) })
    }

    private fun showConfirmWeaponDialogPreBattle(selectedWeapon: BattleWeaponItem) {
        dialogManager.showConfirmWeaponDialogPreBattle(selectedWeapon = selectedWeapon,
                                                       enemies = turnManager.getOnlyEnemies(),
                                                       onConfirmed = { weaponPreBattleConfirmed(it) })
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

    private fun showPushOnDialog() {
        dialogManager.showPushOnDialog(onConfirmed = { pushOnConfirmed(it) })
    }

    private fun showConfirmRestDialog() {
        dialogManager.showConfirmRestDialog(onConfirmed = { restConfirmed(it) })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun attackConfirmed(attackAction: AttackAction) {
        menuManager.buttonTableTarget.remove()
        attackOutcomeManager.attackConfirmed(attackAction)
    }

    private fun specialConfirmed(specialAction: SpecialAction) {
        menuManager.buttonTableTarget.remove()
        specialOutcomeManager.specialConfirmed(specialAction)
    }

    private fun moveConfirmed() {
        val moveAction = MoveAction(battleField, currentParticipant)
        if (moveAction.didCharacterRemainOnTheSameSpace()) {
            menuManager.returnToActionMainMenu()
            return
        } else {
            moveAction.handle()
            menuManager.returnToActionMainMenu()
        }
    }

    private fun potionPreBattleConfirmed(potionAction: PotionAction) {
        confirmManager.potionConfirmed(potionAction)
        menuManager.returnToSelectPotionInPreBattle()
    }

    private fun potionConfirmed(potionAction: PotionAction) {
        menuManager.buttonTablePotion.remove()
        confirmManager.potionConfirmed(potionAction)
    }

    private fun weaponPreBattleConfirmed(weaponAction: WeaponAction) {
        confirmManager.weaponConfirmed(weaponAction)
        menuManager.returnToSelectWeaponInPreBattle()
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
        confirmManager.delayTurnConfirmed(delayTurnAction)
    }

    private fun pushOnConfirmed(pushOnAction: PushOnAction) {
        menuManager.buttonTableAction.remove()
        confirmManager.pushOnConfirmed(pushOnAction)
    }

    private fun restConfirmed(restAction: RestAction) {
        menuManager.buttonTableAction.remove()
        confirmManager.restConfirmed(restAction)
    }

    private fun endTurn() {
        menuManager.buttonTableAction.remove()
        confirmManager.endTurn()
        hasChosenToContinuePerforming = false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun takeTurnHero() {
        if (isDelayingTurn) return

        if (currentParticipant.isPerforming && !hasChosenToContinuePerforming) {
            dialogManager.showContinuePerformDialog()
            return
        }

        menuManager.possibleSetupActionTable()
    }

    // isEnemyActing stays true until the step that was handed to the render thread has actually run there,
    // otherwise a frame could start a second action while the first one is still queued. So every path
    // through startEnemyAction ends in an onRenderThread block that sets it back to false; here it is only
    // set back when the enemy failed to act at all.
    private fun takeTurnEnemy() {
        if (isEnemyActing) return
        isEnemyActing = true
        thread {
            runCatching {
                startEnemyAction()
            }.onFailure {
                if (preferenceManager.isDebugModeOn) it.printStackTrace()
                isDelayingTurn = false
                isEnemyActing = false
            }
        }
    }

    private fun startEnemyAction() {
        possibleBlinkCurrentParticipant()
        doEnemyAction()
    }

    private fun doEnemyAction() {
        battleField.possibleSwitchWeaponOfActingEnemy()
        val heroTarget: Participant? = battleField.possibleGetHeroTargetAndMoveEnemy()
        battleField.resetStartingSpace()
        val attackData: List<AttackData>? = heroTarget?.let {
            isDelayingTurn = true
            AttackAction.createForEnemy(currentParticipant, it, battleId).handle()
        }

        if (attackData.isNullOrEmpty()) {
            isDelayingTurn = false
            endEnemyAction()
        } else {
            onRenderThread {
                attackOutcomeManager.enemyAttackConfirmed(attackData)
                isEnemyActing = false
            }
        }
    }

    private fun endEnemyAction() {
        if (preferenceManager.isDebugModeOn) {
            println("------------------------------------------------")
        }
        (currentParticipant.handlePossibleStagger()
            ?.let { handleStagger(it) }
            ?: run { endEnemyTurn() })
    }

    private fun possibleBlinkCurrentParticipant() {
        if (turnOfLastBlink == turnManager.amountOfTurns) return
        turnOfLastBlink = turnManager.amountOfTurns
        val participantToBlink: Participant = currentParticipant

        if (isEnemyActing) Thread.sleep(500L)

        onRenderThread {
            isDelayingTurn = true
            BlinkEffect(tableManager.battleFieldTable, participantToBlink.character.name, Color.BLACK).start()
            Utils.runWithDelay(0.5f) {
                isDelayingTurn = false
            }
        }

        if (isEnemyActing) Thread.sleep(1000L)
    }

    private fun handleStagger(message: String) {
        onRenderThread {
            showEndOfTurnDialog(message)
            isEnemyActing = false
        }
    }

    private fun endEnemyTurn() {
        if (preferenceManager.isCombatDetailsOn) {
            val message = "${currentParticipant.character.name} ended ${currentParticipant.character.gender} turn."
            onRenderThread {
                showEndOfTurnDialog(message)
                isEnemyActing = false
            }
        } else {
            Thread.sleep(500L)
            onRenderThread {
                turnManager.setNextTurn()
                isEnemyActing = false
            }
        }
    }

    private fun showEndOfTurnDialog(message: String) {
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
        }
        messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
    }

    private fun onRenderThread(action: () -> Unit) {
        Gdx.app.postRunnable { action.invoke() }
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
        InventoryScreen.loadForBattle(currentParticipant.character)
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
            ::showInventoryScreen,
            ::startBattle,
            ::heroIsSelectedForPreEquipment,
            ::heroIsSelectedForPrePotion,
            ::heroIsSelectedForPrePreview,
            ::showPreviewDialog,
            ::showFleeDialog,
            ::showDelayTurnDialog,
            ::showPushOnDialog,
            ::showConfirmRestDialog,
            ::endTurn,
            ::moveConfirmed,
            ::showConfirmAttackDialog,
            ::showConfirmSpecialDialog,
            ::showConfirmPotionDialogPreBattle,
            ::showConfirmPotionDialog,
            ::showConfirmWeaponDialogPreBattle,
            ::showConfirmWeaponDialog
        )
    }

}

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
import nl.t64.cot.screens.battle.action.AttackOutcomeManager
import nl.t64.cot.screens.battle.action.BattleConfirmManager
import nl.t64.cot.screens.battle.action.BattleDialogManager
import nl.t64.cot.screens.battle.action.SpecialOutcomeManager
import nl.t64.cot.screens.battle.effects.BlinkEffect
import nl.t64.cot.screens.battle.hud.BattleHud
import nl.t64.cot.screens.battle.hud.BattleHudBuilder
import nl.t64.cot.screens.battle.menu.BattleMenuBuilder
import nl.t64.cot.screens.battle.menu.BattleMenuHandlers
import nl.t64.cot.screens.battle.menu.BattleMenuManager
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.menu.MenuPause
import nl.t64.cot.screens.world.Camera
import kotlin.concurrent.thread
import kotlin.math.roundToInt


private const val BLINK_DELAY_IN_MILLIS: Long = 500L
private const val ACTION_DELAY_IN_MILLIS: Long = 1000L
private const val END_TURN_DELAY_IN_MILLIS: Long = 500L
private const val BLINK_DURATION: Float = 0.5f
private const val FLED_EXIT_DELAY: Float = 1f

private val combatPowerCalculator = CombatPowerCalculator()

class BattleScreen : Screen {

    private lateinit var battleObserver: BattleSubject
    private lateinit var battleId: String
    private lateinit var stage: Stage
    private lateinit var currentBgm: AudioEvent

    private lateinit var enemies: EnemyContainer
    private lateinit var turnManager: TurnManager
    lateinit var battleState: BattleState; private set
    private lateinit var preBattleSelectedHero: Participant

    private lateinit var battleField: BattleField
    private lateinit var battleHud: BattleHud
    private lateinit var menuManager: BattleMenuManager
    private lateinit var dialogManager: BattleDialogManager
    private lateinit var confirmManager: BattleConfirmManager
    private lateinit var attackOutcomeManager: AttackOutcomeManager
    private lateinit var specialOutcomeManager: SpecialOutcomeManager
    private lateinit var resultManager: BattleResultManager

    private var heroesToSneak: ArrayDeque<Participant> = ArrayDeque()

    private val currentParticipant: Participant
        get() = if (battleState.phase == BattlePhase.BATTLE) turnManager.currentParticipant else preBattleSelectedHero

    private val hudBuilder = BattleHudBuilder()
    private val menuBuilder = BattleMenuBuilder()
    private val shapeRenderer = ShapeRenderer()

    private var isLoaded: Boolean = false
    @Volatile
    private var isEnemyActing: Boolean = false
    private var lastBlinkedTurn: Int = -1
    private var hasWon: Boolean = false
    private var hasLost: Boolean = false
    private var hasFled: Boolean = false
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
        hasWon = false
        hasLost = false
        hasFled = false

        val camera = Camera()
        stage = Stage(camera.viewport)

        Label("preLoadFont", LabelStyle(FontProvider.fffTusjBold200, Color.BLACK))

        enemies = EnemyContainer(battleId)
        turnManager = TurnManager(gameData.party.getAllHeroesAlive(), enemies.getAll())
        battleState = BattleState(turnManager)
        preBattleSelectedHero = turnManager.participants.first { it.character.id == Constant.PLAYER_ID }
        if (preferenceManager.isDebugModeOn) printCombatPowers()

        battleField = BattleField(turnManager.participants, ::currentParticipant)
        battleHud = BattleHud(stage, hudBuilder, ::currentParticipant, battleState)
        menuManager = BattleMenuManager(stage, menuBuilder, turnManager, battleField, ::currentParticipant, createMenuHandlers())
        dialogManager = BattleDialogManager(stage, turnManager, ::currentParticipant, battleState)
        confirmManager = BattleConfirmManager(stage, turnManager, battleHud::battleFieldTable, ::currentParticipant, battleState)
        attackOutcomeManager = AttackOutcomeManager(stage, turnManager, battleHud::battleFieldTable, battleState)
        specialOutcomeManager = SpecialOutcomeManager(battleHud::battleFieldTable, battleState)
        resultManager = BattleResultManager(stage, battleObserver, battleId, enemies, turnManager, battleState)

        val battleTitle: Label = hudBuilder.createBattleTitle()
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
                menuManager.openPreBattleMenu()
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

        if (!isLoaded || battleState.isBgmFading || battleState.isDelayingTurn || hasWon || hasLost || hasFled) {
            return
        }

        if (stage.actors.items.any { it is Dialog }) {
            return
        }

        updateAllTables()

        when {
            battleState.phase != BattlePhase.BATTLE -> return
            enemies.getAll().none { it.isAlive } -> winBattle()
            turnManager.getOnlyHeroes().isEmpty() -> fleeBattle()
            gameData.party.getPlayer().isDead -> gameOver()
            currentParticipant.isHero -> takeTurnHero()
            else -> takeTurnEnemy()
        }
    }

    private fun printCombatPowers() {
        val heroes: List<HeroItem> = gameData.party.getAllHeroesAlive()
        val allEnemies: List<EnemyItem> = enemies.getAll()

        println("Combat powers for battle '$battleId':")
        heroes.forEach { println("  hero  ${it.name}: ${combatPowerCalculator.calculate(it).roundToInt()}") }
        allEnemies.forEach { println("  enemy ${it.name}: ${combatPowerCalculator.calculate(it).roundToInt()}") }
        println("  party total: ${heroes.sumOf { combatPowerCalculator.calculate(it).toDouble() }.roundToInt()}")
        println("  enemy total: ${allEnemies.sumOf { combatPowerCalculator.calculate(it).toDouble() }.roundToInt()}")
    }

    private fun updateAllTables() {
        battleHud.updateHeroTable(gameData.party.getAllHeroes(), turnManager::getCurrentApOf, turnManager::hasFled)
        battleHud.updateEnemyTable(enemies.getAll(), turnManager::getCurrentApOf)
        val visionSlots = (gameData.party.size + 2) + gameData.inventory.getTotalOfItem("vision_thingy")
        battleHud.updateTurnTable(turnManager, visionSlots)
        battleHud.updateBattleField(battleField)
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
        lastBlinkedTurn = -1
        turnManager.resetTemporaryBonusesAfterBattle()
        stage.clear()
    }

    override fun dispose() {
        hudBuilder.dispose()
        menuBuilder.dispose()
        stage.dispose()
        shapeRenderer.dispose()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun handleAudioFading() {
        if (battleState.isBgmFading) {
            audioManager.certainFadeBgmBgs()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun startStealthMovementPhase() {
        menuManager.closeMenu()
        battleState.phase = BattlePhase.STEALTH_MOVEMENT
        heroesToSneak = ArrayDeque(turnManager.getOnlyHeroes().filter { it.getFreeStealthSteps() > 0 })
        letNextHeroSneak()
    }

    private fun letNextHeroSneak() {
        val nextHero: Participant? = heroesToSneak.removeFirstOrNull()
        if (nextHero == null) {
            battleField.resetStartingSpace()
            startBattle()
        } else {
            preBattleSelectedHero = nextHero
            menuManager.openStealthMovementMenu()
        }
    }

    private fun stealthMovementConfirmed() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        menuManager.closeMenu()
        letNextHeroSneak()
    }

    private fun startBattle() {
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
                battleState.phase = BattlePhase.BATTLE
            }
        ))
    }

    private fun heroIsSelectedForPreEquipment(selectedHero: String) {
        preBattleSelectedHero = turnManager.getParticipant(selectedHero)
        menuManager.openWeaponMenuForSelectedHero()
    }

    private fun heroIsSelectedForPrePotion(selectedHero: String) {
        preBattleSelectedHero = turnManager.getParticipant(selectedHero)
        menuManager.confirmPotionForSelectedHero()
    }

    private fun heroIsSelectedForPrePreview(selectedHero: String) {
        preBattleSelectedHero = turnManager.getParticipant(selectedHero)
        menuManager.openPreviewAttackMenuForSelectedHero()
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
        menuManager.closeMenu()
        attackOutcomeManager.attackConfirmed(attackAction)
    }

    private fun specialConfirmed(specialAction: SpecialAction) {
        menuManager.closeMenu()
        specialOutcomeManager.specialConfirmed(specialAction)
    }

    private fun moveConfirmed() {
        val moveAction = MoveAction(battleField, currentParticipant)
        if (!moveAction.didCharacterRemainOnTheSameSpace()) {
            moveAction.handle()
        }
        menuManager.goBack()
    }

    private fun potionPreBattleConfirmed(potionAction: PotionAction) {
        menuManager.closeMenu()
        confirmManager.potionConfirmed(potionAction) { menuManager.goBack() }
    }

    private fun potionConfirmed(potionAction: PotionAction) {
        menuManager.closeMenu()
        confirmManager.potionConfirmed(potionAction)
    }

    private fun weaponPreBattleConfirmed(weaponAction: WeaponAction) {
        confirmManager.weaponConfirmed(weaponAction)
        menuManager.reopenCurrentMenu()
    }

    private fun weaponConfirmed(weaponAction: WeaponAction) {
        menuManager.closeMenu()
        confirmManager.weaponConfirmed(weaponAction)
    }

    private fun fleeConfirmed(fleeAction: FleeAction) {
        menuManager.closeMenu()
        confirmManager.fleeConfirmed(fleeAction, ::heroFled)
    }

    private fun heroFled() {
        val fledParticipant: Participant = turnManager.fleeHero()
        battleField.removeFledHero(fledParticipant)
    }

    private fun delayTurnConfirmed(delayTurnAction: DelayTurnAction) {
        menuManager.closeMenu()
        confirmManager.delayTurnConfirmed(delayTurnAction)
    }

    private fun pushOnConfirmed(pushOnAction: PushOnAction) {
        menuManager.closeMenu()
        confirmManager.pushOnConfirmed(pushOnAction)
    }

    private fun restConfirmed(restAction: RestAction) {
        menuManager.closeMenu()
        confirmManager.restConfirmed(restAction)
    }

    private fun endTurn() {
        menuManager.closeMenu()
        confirmManager.endTurn()
        battleState.hasChosenToContinuePerforming = false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun takeTurnHero() {
        if (battleState.isDelayingTurn) return

        if (currentParticipant.isPerforming && !battleState.hasChosenToContinuePerforming) {
            dialogManager.showContinuePerformDialog()
            return
        }

        menuManager.possibleOpenActionMenu()
    }

    // The enemy acts on its own thread because the movement in EnemyAi walks one space per sleep,
    // which only shows up as an animation while the render thread keeps drawing the grid.
    // isEnemyActing stays true until the step that was handed to the render thread has actually run there,
    // otherwise a frame could start a second action while the first one is still queued.
    private fun takeTurnEnemy() {
        if (isEnemyActing) return
        isEnemyActing = true
        thread {
            runCatching {
                startEnemyAction()
            }.onFailure {
                if (preferenceManager.isDebugModeOn) it.printStackTrace()
                battleState.isDelayingTurn = false
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
            battleState.isDelayingTurn = true
            AttackAction.createForEnemy(currentParticipant, it, battleId).handle()
        }

        if (attackData.isNullOrEmpty()) {
            battleState.isDelayingTurn = false
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
        if (hasBlinkedThisTurn()) return
        lastBlinkedTurn = turnManager.amountOfTurns
        val participantToBlink: Participant = currentParticipant

        if (isEnemyActing) Thread.sleep(BLINK_DELAY_IN_MILLIS)

        onRenderThread {
            battleState.isDelayingTurn = true
            BlinkEffect(battleHud.battleFieldTable, participantToBlink.character.name, Color.BLACK).start()
            Utils.runWithDelay(BLINK_DURATION) {
                battleState.isDelayingTurn = false
            }
        }

        if (isEnemyActing) Thread.sleep(ACTION_DELAY_IN_MILLIS)
    }

    private fun hasBlinkedThisTurn(): Boolean {
        return lastBlinkedTurn == turnManager.amountOfTurns
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
            Thread.sleep(END_TURN_DELAY_IN_MILLIS)
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
        if (battleState.isDelayingTurn || hasWon) return
        hasWon = true
        resultManager.winBattle()
    }

    private fun fleeBattle() {
        hasFled = true
        Utils.runWithDelay(FLED_EXIT_DELAY) {
            resultManager.battleFledExitScreen()
        }
    }

    private fun gameOver() {
        if (battleState.isDelayingTurn) return
        hasLost = true
        resultManager.gameOver()
    }

    private fun createMenuHandlers(): BattleMenuHandlers {
        return BattleMenuHandlers(
            winBattle = ::winBattle,
            openPauseMenu = ::openPauseMenu,

            showInventoryScreenPreBattle = ::showInventoryScreenPreBattle,
            startBattle = ::startStealthMovementPhase,
            heroIsSelectedForPreEquipment = ::heroIsSelectedForPreEquipment,
            heroIsSelectedForPrePotion = ::heroIsSelectedForPrePotion,
            heroIsSelectedForPrePreview = ::heroIsSelectedForPrePreview,
            showConfirmWeaponDialogPreBattle = ::showConfirmWeaponDialogPreBattle,
            showConfirmPotionDialogPreBattle = ::showConfirmPotionDialogPreBattle,

            showInventoryScreen = ::showInventoryScreen,
            showFleeDialog = ::showFleeDialog,
            showDelayTurnDialog = ::showDelayTurnDialog,
            showPushOnDialog = ::showPushOnDialog,
            showConfirmRestDialog = ::showConfirmRestDialog,
            endTurn = ::endTurn,
            showConfirmAttackDialog = ::showConfirmAttackDialog,
            showConfirmSpecialDialog = ::showConfirmSpecialDialog,
            showConfirmWeaponDialog = ::showConfirmWeaponDialog,
            showConfirmPotionDialog = ::showConfirmPotionDialog,
            showPreviewDialog = ::showPreviewDialog,

            confirmMovement = ::moveConfirmed,
            confirmStealthMovement = ::stealthMovementConfirmed
        )
    }

}

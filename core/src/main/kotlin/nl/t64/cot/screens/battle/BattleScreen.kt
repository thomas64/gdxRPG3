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
    private lateinit var currentParticipant: Participant

    private lateinit var battleField: BattleField
    private lateinit var tableManager: BattleTableManager
    private lateinit var menuManager: BattleMenuManager
    private lateinit var dialogManager: BattleDialogManager
    private lateinit var confirmManager: BattleConfirmManager
    private lateinit var attackOutcomeManager: AttackOutcomeManager
    private lateinit var specialOutcomeManager: SpecialOutcomeManager
    private lateinit var resultManager: BattleResultManager

    private lateinit var currentTarget: Participant

    private val screenBuilder = BattleScreenBuilder()
    private val shapeRenderer = ShapeRenderer()

    private var isBgmFading: Boolean = false
    private var isLoaded: Boolean = false
    private var isPreBattle: Boolean = false
    private var isDelayingTurn: Boolean = false
    private var hasChosenToContinuePerforming: Boolean = false
    private var isEnemyActing: Boolean = false
    private var hasCharacterBlinked: Boolean = false
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
        currentParticipant = turnManager.participants.first { it.character.id == Constant.PLAYER_ID }
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
                                  "guide_event_battle_durability",
                                  "guide_event_battle_special"),
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

        if (isPreBattle) {
            updateAllTables()
            return
        }

        updateCurrentParticipant()
        updateAllTables()

        when {
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
        tableManager.updateTurnTable(turnManager)
        tableManager.updateBattleField(battleField)
    }

    private fun updateCurrentParticipant() {
        val tempParticipant1 = currentParticipant
        currentParticipant = turnManager.currentParticipant
        val tempParticipant2 = currentParticipant
        if (tempParticipant1 != tempParticipant2) {
            hasCharacterBlinked = false
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
        hasChosenToContinuePerforming = false
        hasCharacterBlinked = false
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
        currentParticipant = turnManager.participants.first { it.character.name == selectedHero }
        menuManager.aHeroIsSelectedInPreviewEquipmentInPreBattle()
    }

    private fun heroIsSelectedForPrePreview(selectedHero: String) {
        currentParticipant = turnManager.participants.first { it.character.name == selectedHero }
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
                                              onConfirmed = { attackConfirmed(it, target) })
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
                                               onConfirmed = { specialConfirmed(it, target) })
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

    private fun showConfirmRestDialog() {
        dialogManager.showConfirmRestDialog(onConfirmed = { restConfirmed(it) })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun attackConfirmed(attackAction: AttackAction, target: Participant) {
        menuManager.buttonTableTarget.remove()
        currentTarget = target
        attackOutcomeManager.attackConfirmed(attackAction)
    }

    private fun specialConfirmed(specialAction: SpecialAction, target: Participant) {
        menuManager.buttonTableTarget.remove()
        currentTarget = target
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

    private fun potionConfirmed(potionAction: PotionAction) {
        menuManager.buttonTablePotion.remove()
        confirmManager.potionConfirmed(potionAction)
    }

    private fun weaponPreBattleConfirmed(weaponAction: WeaponAction) {
        confirmManager.weaponConfirmed(weaponAction)
        menuManager.returnToPreBattleMainMenu()
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

    private fun takeTurnEnemy() {
        if (isEnemyActing) return
        isEnemyActing = true
        thread {
            runCatching {
                startEnemyAction()
            }.onFailure {
                if (preferenceManager.isDebugModeOn) it.printStackTrace()
            }.also {
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
        val attackData: List<AttackData>? = heroTarget
            ?.let { AttackAction.createForEnemy(currentParticipant, it, battleId).handle() }
            ?.also { currentTarget = heroTarget }

        if (attackData.isNullOrEmpty()) {
            endEnemyAction()
        } else {
            attackOutcomeManager.enemyAttackConfirmed(attackData)
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
        if (hasCharacterBlinked) return
        hasCharacterBlinked = true

        if (isEnemyActing) Thread.sleep(500L)

        thread {
            isDelayingTurn = true
            BlinkEffect(tableManager.battleFieldTable, currentParticipant.character.name, Color.BLACK).start()
            Utils.runWithDelay(0.5f) {
                isDelayingTurn = false
            }
        }

        if (isEnemyActing) Thread.sleep(1000L)
    }

    private fun handleStagger(message: String) {
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
        }
        messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
    }

    private fun endEnemyTurn() {
        if (preferenceManager.isCombatDetailsOn) {
            val message = "${currentParticipant.character.name} ended ${currentParticipant.character.gender} turn."
            val messageDialog = MessageDialog(message)
            messageDialog.setActionAfterHide {
                turnManager.setNextTurn()
            }
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        } else {
            Thread.sleep(500L)
            turnManager.setNextTurn()
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
            ::heroIsSelectedForPrePreview,
            ::showPreviewDialog,
            ::showFleeDialog,
            ::showDelayTurnDialog,
            ::showConfirmRestDialog,
            ::endTurn,
            ::moveConfirmed,
            ::showConfirmAttackDialog,
            ::showConfirmSpecialDialog,
            ::showConfirmPotionDialog,
            ::showConfirmWeaponDialogPreBattle,
            ::showConfirmWeaponDialog
        )
    }

}

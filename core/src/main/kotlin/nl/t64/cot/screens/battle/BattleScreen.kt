package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Table
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
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.screens.dialog.TwoColumnsQuestionDialog
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.menu.MenuPause
import nl.t64.cot.screens.world.Camera
import kotlin.concurrent.thread
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


class BattleScreen : Screen {

    private lateinit var battleObserver: BattleSubject
    private lateinit var battleId: String
    private lateinit var stage: Stage
    private lateinit var currentBgm: AudioEvent

    private lateinit var enemies: EnemyContainer
    private lateinit var turnManager: TurnManager
    private lateinit var battleField: BattleField
    private lateinit var currentParticipant: Participant
    private lateinit var currentTarget: Participant

    private val screenBuilder = BattleScreenBuilder()
    private val battleFieldBuilder = BattleFieldTableBuilder()
    private val shapeRenderer = ShapeRenderer()
    private var heroTable: Table = Table()
    private var enemyTable: Table = Table()
    private var turnTable: Table = Table()
    private var battleFieldTable: Table = Table()

    private var buttonTablePreBattle: Table = Table()
    private var buttonTableHero: Table = Table()
    private var buttonTableReposition: Table = Table()
    private var buttonTableAction: Table = Table()
    private var buttonTableMove: Table = Table()
    private var buttonTableAttack: Table = Table()
    private var buttonTableTarget: Table = Table()
    private var buttonTablePotion: Table = Table()
    private var buttonTableWeapon: Table = Table()
    private val allButtonTables
        get() = listOf(buttonTablePreBattle,
                       buttonTableHero,
                       buttonTableReposition,
                       buttonTableAction,
                       buttonTableMove,
                       buttonTableAttack,
                       buttonTableTarget,
                       buttonTablePotion,
                       buttonTableWeapon)

    private var isBgmFading: Boolean = false
    private var isLoaded: Boolean = false
    private var isPreBattle: Boolean = false
    private var isDelayingTurn: Boolean = false
    private var isEnemyActing: Boolean = false
    private var hasWon: Boolean = false
    private var hasLost: Boolean = false
    private var shouldKeepState: Boolean = false

    private val listenerPreBattle = SelectPreBattleListener({ winBattle() },
                                                            { openPauseMenu() },
                                                            { selectHero() },
                                                            { showInventoryScreenPreBattle() },
                                                            { startBattle() })
    private val listenerHero = SelectHeroListener({ heroIsSelected(it) }, { returnToPreBattle() })
    private val listenerReposition = SelectRepositionListener({ repositionLeft() },
                                                              { repositionRight() },
                                                              { returnToHero() },
                                                              { returnToHero() })

    private val listenerAction = SelectActionListener({ winBattle() },
                                                      { openPauseMenu() },
                                                      { selectAttack() },
                                                      { selectMove() },
                                                      { selectPotion() },
                                                      { selectWeapon() },
                                                      { selectPreviewAttack() },
                                                      { showInventoryScreen() },
                                                      { showFleeDialog() },
                                                      { showDelayTurnDialog() },
                                                      { showConfirmRestDialog() },
                                                      { endTurn() })
    private val listenerMove = SelectMoveListener({ moveLeft() },
                                                  { moveRight() },
                                                  { showConfirmMoveDialog() },
                                                  { returnToAction() })
    private val listenerPreviewAttack = SelectAttackListener({ previewAttackIsSelected(it) }, { returnToAction() })
    private val listenerAttack = SelectAttackListener({ attackIsSelected(it) }, { returnToAction() })
    private val listenerPreviewTarget = SelectTargetListener(::showPreviewDialog, { returnToPreviewAttack() })
    private val listenerTarget = SelectTargetListener(::showConfirmAttackDialog, { returnToAttack() })
    private val listenerPotion = SelectPotionListener({ showConfirmPotionDialog(it) }, { returnToAction() })
    private val listenerWeapon = SelectWeaponListener({ showConfirmWeaponDialog(it) }, { returnToAction() })

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
        battleField = BattleField(turnManager.participants)

        isLoaded = false
        isPreBattle = false
        hasWon = false
        hasLost = false

        val camera = Camera()
        stage = Stage(camera.viewport)

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
                setupPreBattleTable()
                isPreBattle = true
                render(0f)

                val event1 = gameData.events.getEventById("guide_event_battle_1")
                val event2 = gameData.events.getEventById("guide_event_battle_2")
                val event3 = gameData.events.getEventById("guide_event_battle_3")
                when {
                    !gameData.events.hasEventPlayed(event1) -> event1.possibleStart(stage)
                    !gameData.events.hasEventPlayed(event2) -> event2.possibleStart(stage)
                    !gameData.events.hasEventPlayed(event3) -> event3.possibleStart(stage)
                }
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

        updateHeroTable()
        updateEnemyTable()
        updateTurnTable()

        if (isPreBattle) {
            updateBattleField()
            return
        }
        currentParticipant = turnManager.currentParticipant
        updateBattleField()

        if (enemies.getAll().none { it.isAlive }) {
            winBattle()
            return
        }

        if (gameData.party.getPlayer().isDead) {
            gameOver()
            return
        }

        if (currentParticipant.isHero) {
            takeTurnHero()
        } else {
            takeTurnEnemy()
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

    private fun updateHeroTable() {
        heroTable.remove()
        heroTable = screenBuilder.createHeroTable(gameData.party.getAllHeroes(), turnManager.participants)
        stage.addActor(heroTable)
    }

    private fun updateEnemyTable() {
        enemyTable.remove()
        enemyTable = screenBuilder.createEnemyTable(enemies.getAll())
        stage.addActor(enemyTable)
    }

    private fun updateBattleField() {
        battleFieldTable.remove()
        battleField.removeDeadParticipants()
        battleFieldTable = battleFieldBuilder.createBattleFieldTable(battleField, currentParticipant)
        stage.addActor(battleFieldTable)
    }

    private fun updateTurnTable() {
        turnTable.remove()
        turnTable = screenBuilder.createTurnTable(turnManager.participants)
        stage.addActor(turnTable)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun startBattle() {
        buttonTablePreBattle.remove()
        isPreBattle = false
    }

    private fun selectHero() {
        buttonTablePreBattle.remove()
        setupHeroTable()
    }

    private fun heroIsSelected(selectedHero: String) {
        currentParticipant = turnManager.participants.first { it.character.name == selectedHero }
        screenBuilder.buttonTableRepositionIndex = (buttonTableHero.children.last() as GdxList<*>).selectedIndex
        buttonTableHero.remove()
        setupRepositionTable()
    }

    private fun repositionLeft() {
        battleField.repositionHeroLeft(currentParticipant)
    }

    private fun repositionRight() {
        battleField.repositionHeroRight(currentParticipant)
    }

    private fun selectMove() {
        screenBuilder.buttonTableActionIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupMoveTable()
    }

    private fun moveLeft() {
        battleField.moveHeroLeft(currentParticipant)
    }

    private fun moveRight() {
        battleField.moveHeroRight(currentParticipant)
    }

    private fun selectPreviewAttack() {
        screenBuilder.buttonTableActionIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPreviewAttackTable()
    }

    private fun previewAttackIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupPreviewTargetTable(attack)
    }

    private fun selectAttack() {
        screenBuilder.buttonTableActionIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupAttackTable()
    }

    private fun attackIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupTargetTable(attack)
    }

    private fun selectPotion() {
        screenBuilder.buttonTableActionIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPotionTable()
    }

    private fun selectWeapon() {
        screenBuilder.buttonTableActionIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupWeaponTable()
    }

    private fun returnToPreBattle() {
        buttonTableHero.remove()
        setupPreBattleTable()
    }

    private fun returnToHero() {
        buttonTableReposition.remove()
        setupHeroTable()
    }

    private fun returnToAction() {
        buttonTableMove.remove()
        buttonTableAttack.remove()
        buttonTablePotion.remove()
        buttonTableWeapon.remove()
        setupActionTable()
    }

    private fun returnToPreviewAttack() {
        buttonTableTarget.remove()
        setupPreviewAttackTable()
    }

    private fun returnToAttack() {
        buttonTableTarget.remove()
        setupAttackTable()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupPreBattleTable() {
        buttonTablePreBattle = screenBuilder.createButtonTablePreBattle()
        stage.addActor(buttonTablePreBattle)
        buttonTablePreBattle.addListener(listenerPreBattle)
        stage.keyboardFocus = buttonTablePreBattle.children.last()
    }

    private fun setupHeroTable() {
        val onlyHeroes = turnManager.participants.filter { it.isHero }
        buttonTableHero = screenBuilder.createButtonTableHero(onlyHeroes)
        stage.addActor(buttonTableHero)
        buttonTableHero.addListener(listenerHero)
        stage.keyboardFocus = buttonTableHero.children.last()
    }

    private fun setupRepositionTable() {
        buttonTableReposition = screenBuilder.createButtonTableMove()
        stage.addActor(buttonTableReposition)
        buttonTableReposition.addListener(listenerReposition)
        stage.keyboardFocus = buttonTableReposition.children.last()
    }

    private fun setupActionTable() {
        battleField.cancelMovement(currentParticipant)
        battleField.resetStartingSpace()
        val areEnemiesInRange: Boolean = battleField.getTargetableEnemiesFor(currentParticipant).isNotEmpty()
        buttonTableAction = screenBuilder.createButtonTableAction(currentParticipant, areEnemiesInRange)
        stage.addActor(buttonTableAction)
        buttonTableAction.addListener(listenerAction)
        stage.keyboardFocus = buttonTableAction.children.last()
    }

    private fun setupMoveTable() {
        battleField.setStartingSpace(currentParticipant)
        buttonTableMove = screenBuilder.createButtonTableMove()
        stage.addActor(buttonTableMove)
        buttonTableMove.addListener(listenerMove)
        stage.keyboardFocus = buttonTableMove.children.last()
    }

    private fun setupPreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant)
        stage.addActor(buttonTableAttack)
        buttonTableAttack.addListener(listenerPreviewAttack)
        stage.keyboardFocus = buttonTableAttack.children.last()
    }

    private fun setupAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTableAttack(currentParticipant)
        stage.addActor(buttonTableAttack)
        buttonTableAttack.addListener(listenerAttack)
        stage.keyboardFocus = buttonTableAttack.children.last()
    }

    private fun setupPreviewTargetTable(selectedAttack: BattleAbilityItem) {
        val onlyEnemies = turnManager.participants.filter { !it.isHero }
        buttonTableTarget = screenBuilder.createButtonTableTarget(onlyEnemies)
        stage.addActor(buttonTableTarget)
        listenerPreviewTarget.setSelectedAttack(selectedAttack)
        buttonTableTarget.addListener(listenerPreviewTarget)
        stage.keyboardFocus = buttonTableTarget.children.last()
    }

    private fun setupTargetTable(selectedAttack: BattleAbilityItem) {
        val targetableEnemies: List<Participant> = battleField.getTargetableEnemiesFor(currentParticipant)
        buttonTableTarget = screenBuilder.createButtonTableTarget(targetableEnemies)
        stage.addActor(buttonTableTarget)
        listenerTarget.setSelectedAttack(selectedAttack)
        buttonTableTarget.addListener(listenerTarget)
        stage.keyboardFocus = buttonTableTarget.children.last()
    }

    private fun setupPotionTable() {
        val battlePotions: List<BattlePotionItem> = gameData.inventory.getAllOf(InventoryGroup.POTION)
            .filter { it.name.contains(" Potion") }
            .map { BattlePotionItem(it) }
        buttonTablePotion = screenBuilder.createButtonTablePotion(battlePotions)
        stage.addActor(buttonTablePotion)
        buttonTablePotion.addListener(listenerPotion)
        stage.keyboardFocus = buttonTablePotion.children.last()
    }

    private fun setupWeaponTable() {
        val battleEquipment: List<BattleWeaponItem> =
            (gameData.inventory.getAllOf(InventoryGroup.WEAPON) + gameData.inventory.getAllOf(InventoryGroup.SHIELD))
                .map { BattleWeaponItem(it) }
        val currentWeapon: InventoryItem? = currentParticipant.character.getInventoryItem(InventoryGroup.WEAPON)
        val currentShield: InventoryItem? = currentParticipant.character.getInventoryItem(InventoryGroup.SHIELD)
        buttonTableWeapon = screenBuilder.createButtonTableWeapon(battleEquipment, currentWeapon, currentShield)
        stage.addActor(buttonTableWeapon)
        buttonTableWeapon.addListener(listenerWeapon)
        stage.keyboardFocus = buttonTableWeapon.children.last()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showConfirmMoveDialog() {
        val moveAction = MoveAction(battleField, currentParticipant)
        if (moveAction.didCharacterRemainOnTheSameSpace()) return returnToAction()

        val message = moveAction.createConfirmationMessage()
        val dialog = QuestionDialog(message) { moveConfirmed(moveAction) }
        dialog.show(stage, 0, 0.5f)
    }

    private fun moveConfirmed(moveAction: MoveAction) {
        moveAction.handle()
        returnToAction()
    }

    private fun showPreviewDialog(selectedAttack: BattleAbilityItem, selectedTarget: String) {
        val target: Participant = turnManager.getParticipant(enemies.getEnemy(selectedTarget))
        val attackAction = AttackAction(currentParticipant, target, selectedAttack)
        val message = attackAction.createPreviewMessage()
        val dialog = MessageDialog(message)
        dialog.setLeftAlignment()
        dialog.setWidthToMinimum()
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM)
    }

    private fun showConfirmAttackDialog(selectedAttack: BattleAbilityItem, selectedTarget: String) {
        currentTarget = turnManager.getParticipant(enemies.getEnemy(selectedTarget))
        val attackAction = AttackAction(currentParticipant, currentTarget, selectedAttack)
        attackAction.isCostingTooMuchApSp()?.let { notEnoughApSp ->
            showSmallLeftAlignMessageDialog(notEnoughApSp)
            return
        } ?: attackAction.isOutOfRange()?.let { outOfRange ->
            showSmallLeftAlignMessageDialog(outOfRange)
            return
        }
        val message = attackAction.createConfirmationMessage()
        val dialog = QuestionDialog(message) { attackConfirmed(attackAction) }
        dialog.setLeftAlignment()
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 1f)
    }

    private fun showSmallLeftAlignMessageDialog(message: String) {
        val dialog = MessageDialog(message)
        dialog.setLeftAlignment()
        dialog.setWidthToMinimum()
        dialog.show(stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun attackConfirmed(attackAction: AttackAction) {
        val messages: ArrayDeque<String> = attackAction.handle()!!
        buttonTableTarget.remove()
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            showMessages(messages)
            turnManager.removeKilledParticipants()
        }
    }

    private fun showConfirmPotionDialog(selectedPotion: BattlePotionItem) {
        val potionAction = PotionAction(currentParticipant, selectedPotion)
        val (isAble, message) = potionAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { potionConfirmed(potionAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
        }
    }

    private fun potionConfirmed(potionAction: PotionAction) {
        buttonTablePotion.remove()
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

    private fun showConfirmWeaponDialog(selectedWeapon: BattleWeaponItem) {
        val onlyEnemies = turnManager.participants.filter { !it.isHero }
        val weaponAction = WeaponAction(currentParticipant, selectedWeapon, onlyEnemies)
        weaponAction.isUnableToEquip()?.let { message ->
            MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
            return
        }
        val message = weaponAction.createConfirmationMessage()
        val dialog = if (message.second.isBlank() && message.third.isBlank()) {
            QuestionDialog(message.first) { weaponConfirmed(weaponAction) }
                .apply { setLeftAlignment() }
        } else {
            TwoColumnsQuestionDialog(message) { weaponConfirmed(weaponAction) }
        }
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
    }

    private fun weaponConfirmed(weaponAction: WeaponAction) {
        buttonTableWeapon.remove()
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

    private fun showFleeDialog() {
        val fleeAction = FleeAction(currentParticipant, battleId)
        val (isAble, message) = fleeAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { fleeConfirmed(fleeAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    private fun fleeConfirmed(fleeAction: FleeAction) {
        screenBuilder.buttonTableActionIndex = 0
        buttonTableAction.remove()
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

    private fun showDelayTurnDialog() {
        val delayTurnAction = DelayTurnAction(turnManager, currentParticipant)
        val (isAble, message) = delayTurnAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { delayTurnConfirmed(delayTurnAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    private fun delayTurnConfirmed(delayTurnAction: DelayTurnAction) {
        screenBuilder.buttonTableActionIndex = 0
        buttonTableAction.remove()
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

    private fun showConfirmRestDialog() {
        val restAction = RestAction(currentParticipant)
        val (isAble, message) = restAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { restConfirmed(restAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    private fun restConfirmed(restAction: RestAction) {
        screenBuilder.buttonTableActionIndex = 0
        buttonTableAction.remove()
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
        screenBuilder.buttonTableActionIndex = 0
        buttonTableAction.remove()
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
        val isNoButtonTableVisible = stage.actors.items.none { it in allButtonTables }
        if (isNoButtonTableVisible) {
            setupActionTable()
        }
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
        val heroTarget: Participant? = battleField.possibleGetHeroTargetAndMoveEnemy(currentParticipant)
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
            ShakeEffect(battleFieldTable, currentTarget.character.name).start()
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
        screenBuilder.buttonTableActionIndex = 0
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

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
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion
import nl.t64.cot.screens.menu.MenuPause
import nl.t64.cot.screens.world.Camera
import kotlin.collections.List
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

    private val screenBuilder = BattleScreenBuilder()
    private val battleFieldBuilder = BattleFieldTableBuilder()
    private val shapeRenderer = ShapeRenderer()
    private var heroTable: Table = Table()
    private var enemyTable: Table = Table()
    private var turnTable: Table = Table()
    private var battleFieldTable: Table = Table()

    private var buttonTableAction: Table = Table()
    private var buttonTableMove: Table = Table()
    private var buttonTableAttack: Table = Table()
    private var buttonTableTarget: Table = Table()
    private var buttonTablePotion: Table = Table()
    private var buttonTableWeapon: Table = Table()
    private val allButtonTables
        get() = listOf(buttonTableAction,
                       buttonTableMove,
                       buttonTableAttack,
                       buttonTableTarget,
                       buttonTablePotion,
                       buttonTableWeapon)

    private var isBgmFading: Boolean = false
    private var isLoaded: Boolean = false
    private var isDelayingTurn: Boolean = false
    private var isEnemyActing: Boolean = false
    private var hasWon: Boolean = false
    private var hasLost: Boolean = false
    private var shouldKeepState: Boolean = false

    private val listenerAction = SelectActionListener({ winBattle() },
                                                      { openPauseMenu() },
                                                      { selectAttack() },
                                                      { selectMove() },
                                                      { selectPotion() },
                                                      { selectWeapon() },
                                                      { showConfirmRestDialog() },
                                                      { selectPreviewAttack() },
                                                      { showInventoryScreen() },
                                                      { showConfirmEndTurnDialog() },
                                                      { showFleeDialog() })
    private val listenerMove = SelectMoveListener({ moveLeft() }, { moveRight() }, { showConfirmMoveDialog() }, { returnToAction() })
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
        battleField = BattleField(turnManager.participants)

        isLoaded = false
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
                isLoaded = true
                stage.addActor(Utils.createBattleBack(battleId))
                render(0f)
                gameData.events.getEventById("guide_event_battle").possibleStart(stage)
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
        currentParticipant = turnManager.currentParticipant
        updateBattleField()

        if (enemies.getAll().none { it.isAlive }) {
            winBattle()
            return
        }

        if (!gameData.party.getPlayer().isAlive) {
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

    private fun previewAttackIsSelected(attack: String) {
        buttonTableAttack.remove()
        setupPreviewTargetTable(attack)
    }

    private fun selectAttack() {
        screenBuilder.buttonTableActionIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupAttackTable()
    }

    private fun attackIsSelected(attack: String) {
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

    private fun setupActionTable() {
        battleField.cancelMovement(currentParticipant)
        battleField.resetStartingSpace()
        buttonTableAction = screenBuilder.createButtonTableAction()
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
        buttonTableAttack = screenBuilder.createButtonTableAttack(currentParticipant)
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

    private fun setupPreviewTargetTable(selectedAttack: String) {
        val onlyEnemies = turnManager.participants.filter { !it.isHero }
        buttonTableTarget = screenBuilder.createButtonTableTarget(onlyEnemies)
        stage.addActor(buttonTableTarget)
        listenerPreviewTarget.setSelectedAttack(selectedAttack)
        buttonTableTarget.addListener(listenerPreviewTarget)
        stage.keyboardFocus = buttonTableTarget.children.last()
    }

    private fun setupTargetTable(selectedAttack: String) {
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
        val battleWeapons: List<BattleWeaponItem> = gameData.inventory.getAllOf(InventoryGroup.WEAPON)
            .map { BattleWeaponItem(it) }
        val currentWeapon: InventoryItem? = currentParticipant.character.getInventoryItem(InventoryGroup.WEAPON)
        buttonTableWeapon = screenBuilder.createButtonTableWeapon(battleWeapons, currentWeapon)
        stage.addActor(buttonTableWeapon)
        buttonTableWeapon.addListener(listenerWeapon)
        stage.keyboardFocus = buttonTableWeapon.children.last()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showConfirmMoveDialog() {
        val moveAction = MoveAction(battleField, currentParticipant)
        if (moveAction.didCharacterRemainOnTheSameSpace()) return returnToAction()

        val message = moveAction.createConfirmationMessage()
        val dialog = DialogQuestion({ moveConfirmed(moveAction) }, message)
        dialog.show(stage, 0, 0.5f)
    }

    private fun moveConfirmed(moveAction: MoveAction) {
        moveAction.handle()
        returnToAction()
    }

    private fun showPreviewDialog(selectedAttack: String, selectedTarget: String) {
        val attackAction = AttackAction(currentParticipant, enemies.getEnemy(selectedTarget), selectedAttack)
        val message = attackAction.createPreviewMessage()
        val dialog = MessageDialog(message)
        dialog.show(stage)
    }

    private fun showConfirmAttackDialog(selectedAttack: String, selectedTarget: String) {
        val attackAction = AttackAction(currentParticipant, enemies.getEnemy(selectedTarget), selectedAttack)
        val message = attackAction.createConfirmationMessage()
        val dialog = DialogQuestion({ attackConfirmed(attackAction) }, message)
        dialog.show(stage, 0, 1f)
    }

    private fun attackConfirmed(attackAction: AttackAction) {
        val messages: ArrayDeque<String> = attackAction.handle()
        buttonTableTarget.remove()
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            showMessages(messages)
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
    }

    private fun showConfirmPotionDialog(selectedPotion: BattlePotionItem) {
        val potionAction = PotionAction(currentParticipant, selectedPotion)
        val message = potionAction.createConfirmationMessage()
        val dialog = DialogQuestion({ potionConfirmed(potionAction) }, message)
        dialog.show(stage, 0, 0.5f)
    }

    private fun potionConfirmed(potionAction: PotionAction) {
        buttonTablePotion.remove()
        val message: String = potionAction.handle()
        val audio: AudioEvent = if (message.contains("no effect")) AudioEvent.SE_CONVERSATION_NEXT else AudioEvent.SE_POTION
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, audio)
        }
    }

    private fun showConfirmWeaponDialog(selectedWeapon: BattleWeaponItem) {
        val weaponAction = WeaponAction(currentParticipant, selectedWeapon)
        weaponAction.isUnableToEquip()?.let { message ->
            MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
            return
        }
        val message: String = weaponAction.createConfirmationMessage()
        val dialog = DialogQuestion({ weaponConfirmed(weaponAction) }, message)
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
    }

    private fun weaponConfirmed(weaponAction: WeaponAction) {
        buttonTableWeapon.remove()
        val message: String = weaponAction.handle()
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

    private fun showConfirmRestDialog() {
        val restAction = RestAction(currentParticipant)
        val message = restAction.createConfirmationMessage()
        val dialog = DialogQuestion({ restConfirmed(restAction) }, message)
        dialog.show(stage, 0)
    }

    private fun restConfirmed(restAction: RestAction) {
        screenBuilder.buttonTableActionIndex = 0
        buttonTableAction.remove()
        val message: String = restAction.handle()
        val audio: AudioEvent = if (message.contains("skipped")) AudioEvent.SE_CONVERSATION_NEXT else AudioEvent.SE_POTION
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, audio)
        }
    }

    private fun showConfirmEndTurnDialog() {
        val dialog = DialogQuestion({ endTurnConfirmed() }, "End your turn?")
        dialog.show(stage, 0)
    }

    private fun endTurnConfirmed() {
        screenBuilder.buttonTableActionIndex = 0
        buttonTableAction.remove()
        val message = "${currentParticipant.character.name} ended their turn."
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
            }.also {
                isEnemyActing = false
                isDelayingTurn = false
            }
        }
    }

    private fun enemyAction() {
        Thread.sleep(1000L)
        val heroTarget: Participant? = battleField.moveEnemyAndPossibleHeroTargetInRange(currentParticipant)
        isDelayingTurn = true
        val messages: ArrayDeque<String> = heroTarget
            ?.let { AttackAction.createForEnemy(currentParticipant, it).handle() }
            ?: ArrayDeque(listOf("${currentParticipant.character.name} ended their turn."))
        showMessages(messages)
        Thread.sleep(1000L)
        turnManager.setNextTurn()
    }

    private fun openPauseMenu() {
        shouldKeepState = true
        MenuPause.loadForBattle()
    }

    private fun showInventoryScreen() {
        shouldKeepState = true
        InventoryScreen.loadForBattle()
    }

    private fun showMessages(messages: ArrayDeque<String>) {
        if (messages.isEmpty()) return

        val messageDialog = MessageDialog(messages.removeFirst())
        if (messages.isNotEmpty()) {
            messageDialog.disableClosingSound()
        }
        messageDialog.setActionAfterHide {
            showMessages(messages)
        }
        messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
    }

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

    private fun showFleeDialog() {
        if (!gameData.battles.isBattleEscapable(battleId)) {
            playSe(AudioEvent.SE_MENU_ERROR)
            return
        }

        val message = """
            When successful, fleeing will return you to the the location of
            your last save with all progress intact. Otherwise, the turn ends.

            Do you want to flee?""".trimIndent()
        val dialog = DialogQuestion({ battleFledExitScreen() }, message)
        dialog.show(stage, 0)
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

            currentCycle == 1 && isFacingArdorOrOrcGenerals -> """
                Mozes is knocked down.

                The fight is over.""".trimIndent()

            currentCycle in 2..3 && isFacingArdorOrOrcGenerals -> """
                Mozes took a fatal blow.

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

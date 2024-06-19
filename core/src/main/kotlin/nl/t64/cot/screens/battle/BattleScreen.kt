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
import nl.t64.cot.components.battle.EnemyContainer
import nl.t64.cot.components.battle.EnemyItem
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion
import nl.t64.cot.screens.world.Camera


class BattleScreen : Screen {

    private lateinit var battleObserver: BattleSubject
    private lateinit var battleId: String
    private lateinit var stage: Stage

    private lateinit var enemies: EnemyContainer
    private lateinit var turnManager: TurnManager
    private lateinit var currentParticipant: Participant

    private var selectedAttack: String? = null
    private var selectedEnemy: String? = null
    private var selectedPotion: BattlePotionItem? = null

    private val shapeRenderer = ShapeRenderer()
    private var heroTable: Table = Table()
    private var enemyTable: Table = Table()
    private var turnTable: Table = Table()

    private var buttonTableAction: Table = Table()
    private var buttonTableAttack: Table = Table()
    private var buttonTableTarget: Table = Table()
    private var buttonTablePotion: Table = Table()
    private val allButtonTables get() = listOf(buttonTableAction, buttonTableAttack, buttonTableTarget, buttonTablePotion)

    private var isBgmFading: Boolean = false
    private var isLoaded: Boolean = false
    private var isDelayingTurn: Boolean = false
    private var hasWon: Boolean = false
    private var hasLost: Boolean = false

    private val listenerAction = BattleScreenSelectActionListener({ winBattle() },
                                                                  { selectAttack() },
                                                                  { selectPotion() },
                                                                  { showRestDialog() },
                                                                  { showFleeDialog() })
    private val listenerAttack = BattleScreenSelectAttackListener({ attackIsSelected(it) }, { returnToAction() })
    private val listenerTarget = BattleScreenSelectTargetListener({ targetIsSelected(it) }, { returnToAttack() })
    private val listenerPotion = BattleScreenSelectPotionListener({ potionIsSelected(it) }, { returnToAction() })

    companion object {
        fun load(battleId: String, battleObserver: BattleObserver) {
            val screen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
            screen.battleObserver = BattleSubject(battleObserver)
            screen.battleId = battleId
            screenManager.setScreen(ScreenType.BATTLE)
        }
    }

    override fun show() {
        enemies = EnemyContainer(battleId)
        turnManager = TurnManager(gameData.party.getAllHeroesAlive(), enemies.getAll())

        playBgm(AudioEvent.getRandomBattleMusic())
        isLoaded = false
        hasWon = false
        hasLost = false

        val camera = Camera()
        stage = Stage(camera.viewport)

        val battleTitle = BattleScreenBuilder.createBattleTitle()
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
                stage.addActor(BattleScreenBuilder.createIntroTable())
                Gdx.input.inputProcessor = stage
                Utils.setGamepadInputProcessor(stage)
                isLoaded = true
            },
        ))
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)

        stage.act(dt)
        handleAudioFading()
        handleLineDrawing()
        stage.draw()

        if (!isLoaded || isBgmFading || isDelayingTurn || hasWon || hasLost) {
            return
        }

        if (stage.actors.items.any { it is Dialog }) {
            return
        }

        updateHeroTable()
        updateEnemyTable()

        if (enemies.getAll().none { it.isAlive }) {
            winBattle()
            return
        }

        if (!gameData.party.getPlayer().isAlive) {
            gameOver()
            return
        }

        updateTurnTable()
        currentParticipant = turnManager.currentParticipant

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
        stage.clear()
    }

    override fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun handleAudioFading() {
        if (isBgmFading) {
            audioManager.certainFadeBgmBgs()
        }
    }

    private fun handleLineDrawing() {
        if (isLoaded) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = Color.WHITE
            shapeRenderer.line(Gdx.graphics.width / 2f, 250f, Gdx.graphics.width / 2f, Gdx.graphics.height - 200f)
            shapeRenderer.end()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun updateHeroTable() {
        heroTable.remove()
        heroTable = BattleScreenBuilder.createHeroTable(gameData.party.getAllHeroes())
        stage.addActor(heroTable)
    }

    private fun updateEnemyTable() {
        enemyTable.remove()
        enemyTable = BattleScreenBuilder.createEnemyTable(enemies.getAll())
        stage.addActor(enemyTable)
    }

    private fun updateTurnTable() {
        turnTable.remove()
        turnTable = BattleScreenBuilder.createTurnTable(turnManager.participants)
        stage.addActor(turnTable)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun selectAttack() {
        buttonTableAction.remove()
        setupAttackTable()
    }

    private fun attackIsSelected(attack: String) {
        selectedAttack = attack
        buttonTableAttack.remove()
        setupTargetTable()
    }

    private fun targetIsSelected(target: String) {
        selectedEnemy = target
        showConfirmAttackDialog()
    }

    private fun selectPotion() {
        buttonTableAction.remove()
        setupPotionTable()
    }

    private fun potionIsSelected(potion: BattlePotionItem) {
        selectedPotion = potion
        showConfirmPotionDialog()
    }

    private fun returnToAction() {
        selectedAttack = null
        selectedEnemy = null
        selectedPotion = null
        buttonTableAttack.remove()
        buttonTablePotion.remove()
        setupActionTable()
    }

    private fun returnToAttack() {
        selectedAttack = null
        selectedEnemy = null
        buttonTableTarget.remove()
        setupAttackTable()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setupActionTable() {
        buttonTableAction = BattleScreenBuilder.createButtonTableAction()
        stage.addActor(buttonTableAction)
        buttonTableAction.addListener(listenerAction)
        stage.keyboardFocus = buttonTableAction.children.last()
    }

    private fun setupAttackTable() {
        buttonTableAttack = BattleScreenBuilder.createButtonTableAttack(currentParticipant)
        stage.addActor(buttonTableAttack)
        buttonTableAttack.addListener(listenerAttack)
        stage.keyboardFocus = buttonTableAttack.children.last()
    }

    private fun setupTargetTable() {
        buttonTableTarget = BattleScreenBuilder.createButtonTableTarget(enemies.getAll())
        stage.addActor(buttonTableTarget)
        buttonTableTarget.addListener(listenerTarget)
        stage.keyboardFocus = buttonTableTarget.children.last()
    }

    private fun setupPotionTable() {
        val battlePotions = gameData.inventory.getAllSelfPotionsForBattle()
        buttonTablePotion = BattleScreenBuilder.createButtonTablePotion(battlePotions)
        stage.addActor(buttonTablePotion)
        buttonTablePotion.addListener(listenerPotion)
        stage.keyboardFocus = buttonTablePotion.children.last()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showConfirmAttackDialog() {
        val message = """
            Do you want to $selectedAttack $selectedEnemy ?

            Chance to hit:  ${getHit()}%   |   Damage:  ${getDamage()}""".trimIndent()
        val dialog = DialogQuestion({ attackConfirmed() }, message)
        dialog.show(stage, 0)
    }

    private fun getHit(): Int {
        return currentParticipant.character.getCalculatedTotalHit().coerceAtMost(100)
    }

    private fun getDamage(): Int {
        val target = enemies.getEnemy(selectedEnemy!!)
        val attack: Int = currentParticipant.character.getCalculatedTotalDamage()
        val protection: Int = target.getCalculatedTotalProtection()
        return (attack - protection).coerceAtLeast(1).coerceAtMost(target.currentHp)
    }

    private fun attackConfirmed() {
        val target: EnemyItem = enemies.getEnemy(selectedEnemy!!)
        val messages: ArrayDeque<String> = currentParticipant.attack(selectedAttack!!, target)
        selectedAttack = null
        selectedEnemy = null
        buttonTableTarget.remove()
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            showMessages(messages)
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
    }

    private fun showConfirmPotionDialog() {
        val message = """
            ${selectedPotion!!.description}

            Do you want to drink a ${selectedPotion!!.name} ?""".trimIndent()
        val dialog = DialogQuestion({ potionConfirmed() }, message)
        dialog.show(stage, 0)
    }

    private fun potionConfirmed() {
        buttonTablePotion.remove()
        val message: String = currentParticipant.drinkPotion(selectedPotion!!)
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            isDelayingTurn = false
        }
        selectedPotion = null
        buttonTablePotion.remove()
        isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_POTION)
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
        if (isDelayingTurn) return
        isDelayingTurn = true
        Utils.runWithDelay(1f) {
            val target: HeroItem = gameData.party.getAllHeroesAlive().random()
            // todo, weaponName is niet de bedoeling, dit moet een 'spell' worden. body slam, bite, etc.
            // de names van die weapons moeten dus ook niet in enemy.json staan.
            val weaponName: String = currentParticipant.character.getInventoryItem(InventoryGroup.WEAPON)!!.name
            val messages: ArrayDeque<String> = currentParticipant.attack(weaponName, target) // <- hier dus
            turnManager.setNextTurn()
            showMessages(messages)
            isDelayingTurn = false
        }
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

    private fun showRestDialog() {
        val message = """
            Resting will make the current character
            skip this turn and recover 1 HP.

            Do you want to rest for this turn?""".trimIndent()
        val dialog = DialogQuestion({ restConfirmed() }, message)
        dialog.show(stage, 0)
    }

    private fun restConfirmed() {
        buttonTableAction.remove()
        val message: String = currentParticipant.rest()
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

    private fun showFleeDialog() {
        if (!gameData.battles.isBattleEscapable(battleId)) {
            playSe(AudioEvent.SE_MENU_ERROR)
            return
        }

        val message = """
            Fleeing will return you to the location of
            your last save with all progress intact.

            Do you want to flee?""".trimIndent()
        val dialog = DialogQuestion({ battleFledExitScreen() }, message)
        dialog.show(stage, 0)
    }

    private fun battleFledExitScreen() {
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
                val message = """
                    Mozes took a fatal blow.

                    Game Over.""".trimIndent()
                val messageDialog = MessageDialog(message)
                messageDialog.setActionAfterHide { gameOverExitScreen() }
                messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
            }
        ))
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

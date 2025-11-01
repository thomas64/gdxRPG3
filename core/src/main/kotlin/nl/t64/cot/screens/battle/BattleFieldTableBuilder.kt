package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils
import nl.t64.cot.components.battle.BattleField
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.abilities.AbilityItemId
import nl.t64.cot.screens.FontProvider
import kotlin.math.abs


class BattleFieldTableBuilder {

    private val transparent: Drawable = Utils.createTransparency()
    private val border: Drawable = Utils.createFullBorderWhite()
    private val combined: Drawable = Utils.createCombinedDrawable(transparent, border)
    private lateinit var battleField: BattleField

    fun createBattleFieldTable(battleField: BattleField, currentParticipant: Participant): Table {
        this.battleField = battleField

        val enemyTable: Table = if (currentParticipant.isHero) {
            createEnemyRowWithTargetFields()
        } else {
            createFullWhiteRow(battleField.enemySpaces)
        }
        val heroTable: Table = if (currentParticipant.isHero) {
            createHeroRowWithWalkingFields(currentParticipant)
        } else {
            createFullWhiteRow(battleField.heroSpaces)
        }

        return Table().apply {
            setPosition(320f, 20f)
            add(enemyTable).padLeft(60f)
            row()
            add(heroTable).padTop(4f)
            padLeft(-5f)
            padRight(25f)
            padBottom(25f)
            padTop(25f)
            background = combined
            pack()
        }
    }

    private fun createFullWhiteRow(spaces: MutableList<Participant?>): Table {
        return Table().apply {
            defaults().width(60f).height(60f).center()
            spaces.forEach {
                when (it) {
                    null -> addWhiteCell()
                    else -> addWhiteParticipantCell(it)
                }
            }
        }
    }

    private fun createEnemyRowWithTargetFields(): Table {
        val ranges: List<Int> = battleField.getRangeOfActingHero()

        return Table().apply {
            defaults().width(60f).height(60f).center()
            battleField.enemySpaces.forEachIndexed { index, enemyAtSpace ->

                when {
                    enemyAtSpace == null && index in ranges -> addRedCell()
                    enemyAtSpace == null -> addWhiteCell()
                    index in ranges -> addRedParticipantCell(enemyAtSpace)
                    else -> addWhiteParticipantCell(enemyAtSpace)
                }
            }
        }
    }

    private fun createHeroRowWithWalkingFields(currentParticipant: Participant): Table {
        val startingSpace: Int = battleField.startingSpace
        val currentSpace: Int = battleField.getSpaceIndexOfCurrentParticipant()
        val penaltyAp: Int = battleField.getPenaltyApForHero()

        return Table().apply {
            defaults().width(60f).height(60f).center()
            battleField.heroSpaces.forEachIndexed { index, heroAtSpace ->
                addHeroFieldCell(index, heroAtSpace, currentParticipant, startingSpace, currentSpace, penaltyAp)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun Table.addHeroFieldCell(
        index: Int,
        heroAtSpace: Participant?,
        currentParticipant: Participant,
        startingSpace: Int,
        currentSpace: Int,
        penaltyAp: Int,
    ) {
        val currentAp: Int = currentParticipant.currentAP
        val modifiedAp: Int = currentAp - penaltyAp
        val amountOfSteps: Int = abs(index - startingSpace)
        val apCost: Int = getApCostFor(amountOfSteps, penaltyAp)

        val isInRange: Boolean = index in startingSpace - modifiedAp..startingSpace + modifiedAp
        val isStartingSpace: Boolean = index == startingSpace
        val isCurrentSpace: Boolean = index == currentSpace

        when {
            startingSpace == -1 && heroAtSpace == currentParticipant -> addGoldParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace == null -> addWhiteCell()

            isStartingSpace && !isCurrentSpace -> addGoldCell()
            isInRange && heroAtSpace == null -> addGreenCell(apCost, currentAp)
            isStartingSpace && isCurrentSpace -> addGoldParticipantCell(heroAtSpace!!)
            !isStartingSpace && isCurrentSpace -> addGreenParticipantCell(heroAtSpace!!, apCost, currentAp)
            heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            else -> addWhiteCell()
        }
    }

    private fun getApCostFor(amountOfSteps: Int, penalty: Int): Int {
        return when {
            amountOfSteps == 0 -> 0
            amountOfSteps == 1 -> 1 + penalty
            amountOfSteps > 1 -> 1 + penalty + (amountOfSteps - 1)
            else -> 0
        }
    }

    private fun Table.addWhiteParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()))
            add(Container(createImageOf(participant)))
            addPossibleCount(participant)
            addPossibleBattleLock(participant)
            addPossiblePerformance(participant)
        }).padRight(1f)
    }

    private fun Table.addGoldParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.ORANGE })
            add(Container(createImageOf(participant)))
            addPossibleBattleLock(participant)
            addPossiblePerformance(participant)
        }).padRight(1f)
    }

    private fun Table.addGreenParticipantCell(participant: Participant, apCost: Int, actionPoints: Int) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.CHARTREUSE })
            possibleAddApCosts(apCost, actionPoints)
            add(Container(createImageOf(participant)))
            addPossibleBattleLock(participant)
            addPossiblePerformance(participant)
        }).padRight(1f)
    }

    private fun Table.addRedParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED })
            add(Container(createImageOf(participant)))
            addPossibleCount(participant)
            addPossibleBattleLock(participant)
            addPossiblePerformance(participant)
        }).padRight(1f)
    }

    private fun Table.addWhiteCell() {
        add(Image(Utils.createFullBorderWhite())).padRight(1f)
    }

    private fun Table.addGoldCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.ORANGE }).padRight(1f)
    }

    private fun Table.addGreenCell(apCost: Int, actionPoints: Int) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.CHARTREUSE })
            possibleAddApCosts(apCost, actionPoints)
        }).padRight(1f)
    }

    private fun Table.addRedCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED }).padRight(1f)
    }

    private fun createImageOf(participant: Participant): Image {
        return Image(Utils.getCharImage(participant.character.id)[0][1])
            .apply {
                setScaling(Scaling.none)
                name = participant.character.name
            }
    }

    private fun Stack.addPossibleCount(participant: Participant) {
        val enemyCountMap: Map<String, Int> = battleField.createEnemyCountMap()
        if ((enemyCountMap[participant.character.id] ?: 0) > 1) {
            val style = LabelStyle(FontProvider.default, Color.WHITE)
            add(Container(Label(participant.character.name.last().toString(), style)).top().right().padRight(2f))
        }
    }

    private fun Stack.addPossibleBattleLock(participant: Participant) {
        if (battleField.isParticipantNextToOpponent(participant)) {
            val style = LabelStyle(FontProvider.default, Color.YELLOW)
            add(Container(Label("!", style)).top().left().padLeft(4f))
        }
    }

    private fun Stack.addPossiblePerformance(participant: Participant) {
        if (participant.isPerforming) {
            val style = LabelStyle(FontProvider.default, Color.VIOLET)
            when (participant.performingType) {
                AbilityItemId.PERFORM_BEAUTY -> add(Container(Label("+++", style)).top().right().padRight(4f))
                AbilityItemId.PERFORM_CHAOS -> add(Container(Label("---", style)).top().right().padRight(4f))
                else -> throw IllegalStateException("Unknown performing AbilityItemId: ${participant.performingType}")
            }
        }
    }

    private fun Stack.possibleAddApCosts(apCost: Int, actionPoints: Int) {
        if (apCost > 0 && apCost <= actionPoints) {
            val style = LabelStyle(FontProvider.default, Color.WHITE)
            add(Container(Label("$apCost AP", style)).bottom().padBottom(-20f))
        }
    }

}

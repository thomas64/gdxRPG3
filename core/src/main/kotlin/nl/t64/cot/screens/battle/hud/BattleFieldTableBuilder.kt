package nl.t64.cot.screens.battle.hud

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
import nl.t64.cot.screens.battle.BattlePhase
import kotlin.math.abs


class BattleFieldTableBuilder {

    private val transparent: Drawable = Utils.createTransparency()
    private val border: Drawable = Utils.createFullBorderWhite()
    private val combined: Drawable = Utils.createCombinedDrawable(transparent, border)
    private lateinit var battleField: BattleField

    fun createBattleFieldTable(battleField: BattleField, currentParticipant: Participant, phase: BattlePhase): Table {
        this.battleField = battleField

        val enemyTable: Table = if (currentParticipant.isHero) {
            createEnemyRowWithHeroRangeFields()
        } else {
            createEnemyRowWithActingEnemy(currentParticipant)
        }
        val heroTable: Table = if (currentParticipant.isHero) {
            createHeroRowWithWalkingFields(currentParticipant, phase)
        } else {
            createHeroRowWithEnemyRangeFields()
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

    private fun createEnemyRowWithActingEnemy(currentParticipant: Participant): Table {
        return Table().apply {
            defaults().width(60f).height(60f).center()
            battleField.enemySpaces.forEach {
                when (it) {
                    null -> addWhiteCell()
                    currentParticipant -> addGoldParticipantCell(it)
                    else -> addWhiteParticipantCell(it)
                }
            }
        }
    }

    private fun createEnemyRowWithHeroRangeFields(): Table {
        val ranges: List<Int> = battleField.getWeaponRangeOfActingHero()

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

    private fun createHeroRowWithEnemyRangeFields(): Table {
        val ranges: List<Int> = battleField.getWeaponRangeOfActingEnemy()

        return Table().apply {
            defaults().width(60f).height(60f).center()
            battleField.heroSpaces.forEachIndexed { index, heroAtSpace ->

                when {
                    heroAtSpace == null && index in ranges -> addRedCell()
                    heroAtSpace == null -> addWhiteCell()
                    index in ranges -> addRedParticipantCell(heroAtSpace)
                    else -> addWhiteParticipantCell(heroAtSpace)
                }
            }
        }
    }

    private fun createHeroRowWithWalkingFields(currentParticipant: Participant, phase: BattlePhase): Table {
        val walkingFields: WalkingFields = when (phase) {
            BattlePhase.STEALTH_MOVEMENT -> createStealthWalkingFields()
            else -> createBattleWalkingFields(currentParticipant)
        }

        return Table().apply {
            defaults().width(60f).height(60f).center()
            battleField.heroSpaces.forEachIndexed { index, heroAtSpace ->
                addHeroFieldCell(index, heroAtSpace, currentParticipant, walkingFields)
            }
        }
    }

    private fun createBattleWalkingFields(currentParticipant: Participant): WalkingFields {
        val penaltyAp: Int = battleField.getPenaltyApForHero()
        return WalkingFields(startingSpace = battleField.startingSpace,
                             currentSpace = battleField.getSpaceIndexOfCurrentParticipant(),
                             range = currentParticipant.currentAP - penaltyAp,
                             budget = currentParticipant.currentAP,
                             extraCost = penaltyAp,
                             costSuffix = " AP",
                             cellColor = Color.CHARTREUSE)
    }

    private fun createStealthWalkingFields(): WalkingFields {
        val stealthSteps: Int = battleField.getFreeStealthStepsForHero()
        return WalkingFields(startingSpace = battleField.startingSpace,
                             currentSpace = battleField.getSpaceIndexOfCurrentParticipant(),
                             range = stealthSteps,
                             budget = stealthSteps,
                             extraCost = 0,
                             costSuffix = "",
                             cellColor = Color.PURPLE)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun Table.addHeroFieldCell(
        index: Int,
        heroAtSpace: Participant?,
        currentParticipant: Participant,
        walkingFields: WalkingFields,
    ) {
        val startingSpace: Int = walkingFields.startingSpace
        val costLabel: String? = walkingFields.createCostLabelFor(index)

        val isInRange: Boolean = walkingFields.isInRange(index)
        val isStartingSpace: Boolean = index == startingSpace
        val isCurrentSpace: Boolean = index == walkingFields.currentSpace

        when {
            startingSpace == -1 && heroAtSpace == currentParticipant -> addGoldParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace == null -> addWhiteCell()

            isStartingSpace && !isCurrentSpace -> addGoldCell()
            isInRange && heroAtSpace == null -> addReachableCell(costLabel, walkingFields.cellColor)
            isStartingSpace && isCurrentSpace -> addGoldParticipantCell(heroAtSpace!!)
            !isStartingSpace && isCurrentSpace -> addReachableParticipantCell(heroAtSpace!!, costLabel, walkingFields.cellColor)
            heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            else -> addWhiteCell()
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
            addPossibleCount(participant)
            addPossibleBattleLock(participant)
            addPossiblePerformance(participant)
        }).padRight(1f)
    }

    private fun Table.addReachableParticipantCell(participant: Participant, costLabel: String?, cellColor: Color) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = cellColor })
            possibleAddCostLabel(costLabel)
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

    private fun Table.addReachableCell(costLabel: String?, cellColor: Color) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = cellColor })
            possibleAddCostLabel(costLabel)
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

    private fun Stack.possibleAddCostLabel(costLabel: String?) {
        costLabel?.let {
            val style = LabelStyle(FontProvider.default, Color.WHITE)
            add(Container(Label(it, style)).bottom().padBottom(-20f))
        }
    }

    private class WalkingFields(
        val startingSpace: Int,
        val currentSpace: Int,
        private val range: Int,
        private val budget: Int,
        private val extraCost: Int,
        private val costSuffix: String,
        val cellColor: Color
    ) {

        fun isInRange(index: Int): Boolean {
            return index in startingSpace - range..startingSpace + range
        }

        fun createCostLabelFor(index: Int): String? {
            val amountOfSteps: Int = abs(index - startingSpace)
            if (amountOfSteps == 0) return null
            val cost: Int = amountOfSteps + extraCost
            return if (cost <= budget) "$cost$costSuffix" else null
        }

    }

}

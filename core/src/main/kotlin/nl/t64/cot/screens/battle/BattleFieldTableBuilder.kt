package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils
import nl.t64.cot.components.battle.BattleField
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.screens.FontProvider


class BattleFieldTableBuilder {

    private val smallStyle = LabelStyle(FontProvider.default, Color.WHITE)
    private val transparent: Drawable = Utils.createTransparency()
    private val border: Drawable = Utils.createFullBorderWhite()
    private val combined: Drawable = Utils.createCombinedDrawable(transparent, border)
    private lateinit var battleField: BattleField

    fun createBattleFieldTable(battleField: BattleField, currentParticipant: Participant): Table {
        this.battleField = battleField

        val enemyTable: Table = if (currentParticipant.isHero) {
            createTargetFieldTable()
        } else {
            createParticipantTable(battleField.enemySpaces)
        }
        val heroTable: Table = if (currentParticipant.isHero) {
            createHeroFieldTable(currentParticipant)
        } else {
            createParticipantTable(battleField.heroSpaces)
        }

        return Table().apply {
            setPosition(320f, 20f)
            add(enemyTable).padLeft(60f)
            row()
            add(heroTable).padTop(4f)
            padLeft(-20f)
            padRight(10f)
            padBottom(10f)
            padTop(10f)
            background = combined
            pack()
        }
    }

    private fun createParticipantTable(spaces: MutableList<Participant?>): Table {
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

    private fun createTargetFieldTable(): Table {
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

    private fun createHeroFieldTable(currentParticipant: Participant): Table {
        val startingSpace: Int = battleField.startingSpace
        val currentSpace: Int = battleField.getSpaceIndexOfCurrentParticipant()
        val actionPoints: Int = battleField.getModifiedApForHero()

        return Table().apply {
            defaults().width(60f).height(60f).center()
            battleField.heroSpaces.forEachIndexed { index, heroAtSpace ->
                addHeroFieldCell(index, heroAtSpace, currentParticipant, startingSpace, currentSpace, actionPoints)
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
        actionPoints: Int,
    ) {
        val isInRange = index in startingSpace - actionPoints..startingSpace + actionPoints
        val isStartingSpace = index == startingSpace
        val isCurrentSpace = index == currentSpace

        when {
            startingSpace == -1 && heroAtSpace == currentParticipant -> addGoldParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace == null -> addWhiteCell()

            isStartingSpace && !isCurrentSpace -> addGoldCell()
            isInRange && heroAtSpace == null -> addGreenCell()
            isStartingSpace && isCurrentSpace -> addGoldParticipantCell(heroAtSpace!!)
            !isStartingSpace && isCurrentSpace -> addGreenParticipantCell(heroAtSpace!!)
            heroAtSpace != null -> addWhiteParticipantCell(heroAtSpace)
            else -> addWhiteCell()
        }
    }

    private fun Table.addWhiteParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()))
            add(Container(createImageOf(participant)))
            addPossibleCount(participant)
        }).padRight(1f)
    }

    private fun Table.addGoldParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.ORANGE })
            add(Container(createImageOf(participant)))
        }).padRight(1f)
    }

    private fun Table.addGreenParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.CHARTREUSE })
            add(Container(createImageOf(participant)))
        }).padRight(1f)
    }

    private fun Table.addRedParticipantCell(participant: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED })
            add(Container(createImageOf(participant)))
            addPossibleCount(participant)
        }).padRight(1f)
    }

    private fun Table.addWhiteCell() {
        add(Image(Utils.createFullBorderWhite())).padRight(1f)
    }

    private fun Table.addGoldCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.ORANGE }).padRight(1f)
    }

    private fun Table.addGreenCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.CHARTREUSE }).padRight(1f)
    }

    private fun Table.addRedCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED }).padRight(1f)
    }

    private fun createImageOf(participant: Participant): Container<Image> {
        return Container(
            Image(Utils.getCharImage(participant.character.id)[0][1])
                .apply {
                    setScaling(Scaling.none)
                    name = participant.character.name
                }
        ).left()
    }

    private fun Stack.addPossibleCount(participant: Participant) {
        val enemyCountMap: Map<String, Int> = battleField.createEnemyCountMap()
        if ((enemyCountMap[participant.character.id] ?: 0) > 1) {
            add(Container(Label(participant.character.name.last().toString(), smallStyle)).top().right().padRight(2f))
        }
    }

}

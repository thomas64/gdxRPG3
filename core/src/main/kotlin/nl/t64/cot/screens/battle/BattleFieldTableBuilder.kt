package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.battle.BattleField
import nl.t64.cot.components.battle.Participant


private const val TEXT_FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24

class BattleFieldTableBuilder {

    fun createBattleFieldTable(battleField: BattleField, currentParticipant: Participant): Table {
        val tableSkin = createSkin()
        val labelStyle = createLabelStyle()
        val enemyCountMap = createEnemyCountMap(battleField)
        val enemyNumberTable = createEnemyNumberTable(battleField, enemyCountMap, tableSkin, labelStyle)
        val enemyTable = createParticipantTable(battleField.enemySpaces, tableSkin)
        val heroTable = if (currentParticipant.isHero) {
            createHeroFieldTable(battleField, currentParticipant, tableSkin)
        } else {
            createParticipantTable(battleField.heroSpaces, tableSkin)
        }

        return Table(tableSkin).apply {
            bottom().left()
            setPosition(360f, 20f)
            add(enemyNumberTable).padLeft(60f)
            row()
            add(enemyTable).padLeft(60f)
            row()
            add(heroTable)
        }
    }

    private fun createEnemyCountMap(battleField: BattleField): MutableMap<String, Int> {
        return mutableMapOf<String, Int>().apply {
            battleField.enemySpaces.filterNotNull().forEach {
                this[it.character.id] = getOrDefault(it.character.id, 0) + 1
            }
        }
    }

    private fun createEnemyNumberTable(
        battleField: BattleField,
        enemyCountMap: MutableMap<String, Int>,
        skin: Skin,
        labelStyle: LabelStyle
    ): Table {
        return Table(skin).apply {
            defaults().width(60f).height(30f).center()
            battleField.enemySpaces.forEach {
                if (it != null && !it.character.isAlive) {
                    add()
                } else if (it != null && enemyCountMap[it.character.id]!! > 1) {
                    add(Container(Label(it.character.name.last().toString(), labelStyle)))
                } else {
                    add()
                }
            }
        }
    }

    private fun createParticipantTable(spaces: MutableList<Participant?>, skin: Skin): Table {
        return Table(skin).apply {
            defaults().width(60f).height(60f).center()
            spaces.forEachIndexed { index, participantAtSpace ->
                if (participantAtSpace == null) {
                    add(Image(Utils.createFullBorderWhite()))
                } else if (!participantAtSpace.character.isAlive) {
                    spaces[index] = null
                    add(Image(Utils.createFullBorderWhite()))
                } else {
                    add(Stack().apply {
                        add(Image(Utils.createFullBorderWhite()))
                        add(Container(createImageOf(participantAtSpace)))
                    })
                }
            }
        }
    }

    private fun createHeroFieldTable(battleField: BattleField, currentParticipant: Participant, skin: Skin): Table {
        val startingSpace = battleField.startingSpace
        val currentSpace = battleField.getCurrentSpace(currentParticipant)
        val actionPoints = currentParticipant.currentAP

        return Table(skin).apply {
            defaults().width(60f).height(60f).center()
            battleField.heroSpaces.forEachIndexed { index, heroAtSpace ->
                addFieldCell(index, heroAtSpace, startingSpace, currentSpace, actionPoints, battleField)
            }
        }
    }

    private fun Table.addFieldCell(
        index: Int,
        heroAtSpace: Participant?,
        startingSpace: Int,
        currentSpace: Int,
        actionPoints: Int,
        battleField: BattleField
    ) {
        val isInRange = index in startingSpace - actionPoints..startingSpace + actionPoints
        val isStartingSpace = index == startingSpace
        val isCurrentSpace = index == currentSpace
        val isHeroDead = heroAtSpace?.character?.isAlive == false
        if (isHeroDead) battleField.heroSpaces[index] = null

        when {
            startingSpace == -1 && heroAtSpace != null -> addWhiteHeroCell(heroAtSpace)
            startingSpace == -1 && heroAtSpace == null -> addWhiteCell()

            isStartingSpace && !isCurrentSpace -> addGoldCell()
            isInRange && heroAtSpace == null -> addGreenCell()
            isStartingSpace && isCurrentSpace -> addGoldHeroCell(heroAtSpace!!)
            !isStartingSpace && isCurrentSpace -> addGreenHeroCell(heroAtSpace!!)
            heroAtSpace != null -> addRedHeroCell(heroAtSpace)
            else -> addRedCell()
        }
    }

    private fun Table.addWhiteHeroCell(hero: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()))
            add(Container(createImageOf(hero)))
        })
    }

    private fun Table.addGoldHeroCell(hero: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.GOLD })
            add(Container(createImageOf(hero)))
        })
    }

    private fun Table.addGreenHeroCell(hero: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.GREEN })
            add(Container(createImageOf(hero)))
        })
    }

    private fun Table.addRedHeroCell(hero: Participant) {
        add(Stack().apply {
            add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED })
            add(Container(createImageOf(hero)))
        })
    }

    private fun Table.addWhiteCell() {
        add(Image(Utils.createFullBorderWhite()))
    }

    private fun Table.addGoldCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.GOLD })
    }

    private fun Table.addGreenCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.GREEN })
    }

    private fun Table.addRedCell() {
        add(Image(Utils.createFullBorderWhite()).apply { color = Color.RED })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createButtonTableMove(): Table {
        return createSelectionTable().apply {
            add("Move left or right and confirm.")
        }
    }

    private fun createImageOf(participant: Participant): Container<Image> {
        return Container(
            Image(Utils.getCharImage(participant.character.id)[0][1])
                .apply { setScaling(Scaling.none) }
        ).left()
    }

    private fun createSelectionTable(): Table {
        return Table(createSkin()).apply {
            columnDefaults(0).width(300f)
            top().left()
            setPosition(1050f, Gdx.graphics.height - 20f)
        }
    }

    private fun createLabelStyle(): LabelStyle {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return LabelStyle(font, Color.WHITE)
    }

    private fun createSkin(): Skin {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return Skin().apply { add("default", LabelStyle(font, Color.WHITE)) }
    }

}

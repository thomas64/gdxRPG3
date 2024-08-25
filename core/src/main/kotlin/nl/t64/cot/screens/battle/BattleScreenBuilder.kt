package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.EnemyItem
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.disposeAndClear
import nl.t64.cot.toDrawable
import nl.t64.cot.toTexture
import kotlin.collections.List
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


private const val TEXT_FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24
private const val TITLE_TEXT = "Battle...!"
private const val BAR_WIDTH = 100f
private const val BAR_HEIGHT = 18f

object BattleScreenBuilder {

    private val texturesToDispose: MutableSet<Texture> = mutableSetOf()

    fun createBattleTitle(): Label {
        return Label(TITLE_TEXT, createLabelStyle(Color.WHITE)).apply {
            setPosition((Gdx.graphics.width / 2f) - (width / 2f), (Gdx.graphics.height / 2f) - (height / 2f))
            isVisible = false
        }
    }

    fun createIntroTable(): Table {
        return Table(createSkin()).apply {
            top()
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height - 50f)
            add("This battle screen is a temporary placeholder for the real turn based").row()
            add("strategic battle gameplay that will replace this screen in the future.")
        }
    }

    fun disposeAndClearTextures() {
        texturesToDispose.disposeAndClear()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createHeroTable(heroes: List<HeroItem>): Table {
        return Table(createSkin()).apply {
            defaults().height(Constant.FACE_SIZE)
            columnDefaults(0).width(Constant.FACE_SIZE)
            columnDefaults(1).width(150f)
            top().left()
            setPosition(20f, Gdx.graphics.height - 20f)
            heroes.forEach { addHero(it) }
        }
    }

    private fun Table.addHero(hero: HeroItem) {
        add(Utils.getFaceImage(hero.id).apply { if (!hero.isAlive) color = Color.DARK_GRAY })
        add(Table(createSkin()).apply {
            left()
            defaults().left()
            add(hero.name).colspan(2).row()
            add("HP:").width(50f)
            add(createHpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).row()
            add("SP:").width(50f)
            add(createSpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).row()
        }).top().left().padLeft(20f).row()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createEnemyTable(enemies: List<EnemyItem>): Table {
        return Table(createSkin()).apply {
            defaults().height(Constant.FACE_SIZE)
            columnDefaults(0).width(150f)
            columnDefaults(1).width(Constant.FACE_SIZE)
            top().left()
            setPosition(Gdx.graphics.width - Constant.FACE_SIZE - 150f - 40f, Gdx.graphics.height - 20f)
            enemies.forEach { addEnemy(it) }
        }
    }

    private fun Table.addEnemy(enemy: EnemyItem) {
        add(Table(createSkin()).apply {
            left()
            defaults().left()
            add(enemy.name).colspan(2).row()
            add("HP:").width(50f)
            add(createHpBar(enemy)).width(BAR_WIDTH).height(BAR_HEIGHT)
        }).top().left()
        val faceImage = Utils.getFaceImage(enemy.id, isFlipped = false)
            .apply { if (!enemy.isAlive) color = Color.DARK_GRAY }
        add(faceImage).padLeft(20f).row()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createTurnTable(participants: List<Participant>): Table {
        return Table(createSkin()).apply {
            defaults().height(50f)
            columnDefaults(0).width(100f)
            columnDefaults(1).width(54f)
            columnDefaults(2).width(200f)
            columnDefaults(3).width(100f)
            top().left()
            setPosition(450f, Gdx.graphics.height - 180f)

            add("").padBottom(5f)
            add("Turn order").padBottom(5f)
            add("").padBottom(5f)
            add("Speed").padBottom(5f).row()

            add("Now:").padBottom(5f)
            add(createImageOf(participants[0])).padBottom(5f)
            add(Label(participants[0].character.name, createLabelStyle(Color.GOLD))).padBottom(5f)
            addSpeedCell(participants[0], Color.GOLD).padBottom(5f).row()

            add("Next:")
            add(createImageOf(participants[1]))
            add(participants[1].character.name)
            addSpeedCell(participants[1]).row()

            participants.drop(2).forEach {
                add("")
                add(createImageOf(it))
                add(it.character.name)
                addSpeedCell(it).row()
            }
        }
    }

    private fun createImageOf(participant: Participant): Container<Image> {
        return Container(
            Image(Utils.getCharImage(participant.character.id)[0][1])
                .apply { setScaling(Scaling.none) }
        ).left()
    }

    private fun Table.addSpeedCell(participant: Participant, color: Color = Color.WHITE): Cell<Label> {
        if (participant.isHero) {
            return add(Label(participant.character.getCalculatedTotalStatOf(StatItemId.SPEED).toString(),
                             createLabelStyle(color)))
        } else {
            return add("?")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createButtonTableAction(): Table {
        return createStyledEmptyList<String>().fillWithActions().toActionTable()
    }

    fun createButtonTableAttack(currentParticipant: Participant): Table {
        return createStyledEmptyList<String>().fillWithAttacksFor(currentParticipant).toAttackTable()
    }

    fun createButtonTableTarget(enemies: List<EnemyItem>): Table {
        return createStyledEmptyList<String>().fillWithTargets(enemies).toTargetTable()
    }

    fun createButtonTablePotion(potions: List<BattlePotionItem>): Table {
        return createStyledEmptyList<BattlePotionItem>().fillWithPotions(potions).toPotionTable()
    }

    private fun GdxList<String>.fillWithActions(): GdxList<String> {
        this.setItems(
            "Attack",
            "Potion",
            "Rest",
            "Flee"
        )
        this.selectedIndex = -1
        return this
    }

    private fun GdxList<String>.fillWithAttacksFor(current: Participant): GdxList<String> {
        this.setItems(
            "Strike",
            "Back"
        )
        this.selectedIndex = -1
        return this
    }

    private fun GdxList<String>.fillWithTargets(enemies: List<EnemyItem>): GdxList<String> {
        enemies
            .filter { it.isAlive }
            .forEach { items.add(it.name) }
        items.add("Back")
        this.selectedIndex = -1
        return this
    }

    private fun GdxList<BattlePotionItem>.fillWithPotions(potions: List<BattlePotionItem>): GdxList<BattlePotionItem> {
        this.setItems(*potions.toTypedArray())
        items.add(BattlePotionItem("Back"))
        this.selectedIndex = -1
        return this
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun GdxList<String>.toActionTable(): Table {
        val listWithActions = this
        return createSelectionTable().apply {
            add("Select Action:").row()
            add(listWithActions)
        }
    }

    private fun GdxList<String>.toAttackTable(): Table {
        val listWithAttacks = this
        return createSelectionTable().apply {
            add("Select Attack:").row()
            add(listWithAttacks)
        }
    }

    private fun GdxList<String>.toTargetTable(): Table {
        val listWithTargets = this
        return createSelectionTable().apply {
            add("Select Target:").row()
            add(listWithTargets)
        }
    }

    private fun GdxList<BattlePotionItem>.toPotionTable(): Table {
        val listWithPotions = this
        return createSelectionTable().apply {
            add("Select Potion:").row()
            add(listWithPotions)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun createHpBar(character: Character): Stack {
        return Stack().apply {
            add(createHpFill(character))
            if (character is HeroItem) add(createHpLabel(character))
            add(Image(Utils.createFullBorderWhite()))
        }
    }

    private fun createHpFill(character: Character): Image {
        return Utils.getHpColor(character.currentHp, character.maximumHp).toImage().apply {
            setScaling(Scaling.stretchY)
            align = Align.left
            drawable.minWidth = (BAR_WIDTH / character.maximumHp) * character.currentHp
        }
    }

    private fun createHpLabel(character: Character): Label {
        return Label("${character.currentHp} ", LabelStyle(BitmapFont(), Color.WHITE))
            .apply { setAlignment(Align.right) }
    }

    private fun createSpBar(hero: HeroItem): Stack {
        return Stack().apply {
            add(createSpFill(hero))
            add(createSpLabel(hero))
            add(Image(Utils.createFullBorderWhite()))
        }
    }

    private fun createSpFill(hero: HeroItem): Image {
        return Color.ROYAL.toImage().apply {
            setScaling(Scaling.stretchY)
            align = Align.left
            drawable.minWidth = (BAR_WIDTH / hero.maximumSp) * hero.currentSp
        }
    }

    private fun createSpLabel(hero: HeroItem): Label {
        return Label("${hero.currentSp} ", LabelStyle(BitmapFont(), Color.WHITE))
            .apply { setAlignment(Align.right) }
    }

    private fun Color.toImage(): Image {
        return Image(toTexture().also { texturesToDispose.add(it) })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun <T> createStyledEmptyList(bottomHeight: Float = 0f): GdxList<T> {
        return GdxList<T>(ListStyle().apply {
            font = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
            fontColorSelected = Color.GOLD
            fontColorUnselected = Color.WHITE
            background = Color.CLEAR.toDrawable()
            selection = Color.CLEAR.toDrawable()
            selection.bottomHeight = bottomHeight
        })
    }

    private fun createLabelStyle(color: Color): LabelStyle {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return LabelStyle(font, color)
    }

    private fun createSelectionTable(): Table {
        return Table(createSkin()).apply {
            columnDefaults(0).width(300f)
            top().left()
            setPosition(1050f, Gdx.graphics.height - 180f)
        }
    }

    private fun createSkin(): Skin {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return Skin().apply { add("default", LabelStyle(font, Color.WHITE)) }
    }

}

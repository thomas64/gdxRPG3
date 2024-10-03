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
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
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

class BattleScreenBuilder {

    private val colorTextureCache: MutableMap<Color, Texture> = mutableMapOf()
    private val barFontStyle = LabelStyle(BitmapFont(), Color.WHITE)
    private val transparent = Utils.createTransparency()

    var buttonTableActionIndex = 0

    fun createBattleTitle(): Label {
        return Label(TITLE_TEXT, createLabelStyle(Color.WHITE)).apply {
            setPosition((Gdx.graphics.width / 2f) - (width / 2f), (Gdx.graphics.height / 2f) - (height / 2f))
            isVisible = false
        }
    }

    fun dispose() {
        colorTextureCache.disposeAndClear()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createHeroTable(heroes: List<HeroItem>, participants: List<Participant>): Table {
        return Table(createSkin()).apply {
            defaults().height(Constant.FACE_SIZE)
            columnDefaults(0).width(Constant.FACE_SIZE)
            top().left()
            setPosition(20f, Gdx.graphics.height - 20f)
            heroes.forEach { addHero(it, participants) }
        }
    }

    private fun Table.addHero(hero: HeroItem, participants: List<Participant>) {
        val currentAp: Int = participants.firstOrNull { it.character == hero }?.currentAP ?: 0
        val maximumAP: Int = hero.getCalculatedActionPoints()

        add(Utils.getFaceImage(hero.id).apply { if (!hero.isAlive) color = Color.DARK_GRAY })
        add(Table(createSkin()).apply {
            defaults().left().height(30f)
            add(hero.name).width(150f).colspan(2).padLeft(10f).padRight(10f).row()
            add("HP:").width(50f).padLeft(10f)
            add(createHpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            add("SP:").width(50f).padLeft(10f)
            add(createSpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            add("AP:").width(50f).padLeft(10f)
            add("$currentAp/ $maximumAP").width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            background = transparent
        }).row()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createEnemyTable(enemies: List<EnemyItem>): Table {
        return Table(createSkin()).apply {
            defaults().height(Constant.FACE_SIZE)
            columnDefaults(1).width(Constant.FACE_SIZE)
            top().left()
            setPosition(Gdx.graphics.width - Constant.FACE_SIZE - 150f - 40f, Gdx.graphics.height - 20f)
            enemies.forEach { addEnemy(it) }
        }
    }

    private fun Table.addEnemy(enemy: EnemyItem) {
        add(Table(createSkin()).apply {
            defaults().left()
            add(enemy.name).width(150f).colspan(2).padLeft(10f).padRight(10f).row()
            add("HP:").width(50f).padLeft(10f)
            add(createHpBar(enemy)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            background = transparent
        })
        val faceImage = Utils.getFaceImage(enemy.id, isFlipped = false)
            .apply { if (!enemy.isAlive) color = Color.DARK_GRAY }
        add(faceImage).row()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createTurnTable(participants: List<Participant>): Table {
        return Table(createSkin()).apply {
            defaults().height(50f)
            columnDefaults(0).width(80f).padLeft(10f)
            columnDefaults(1).width(54f)
            columnDefaults(2).width(200f)
            columnDefaults(3).width(70f)

            add("").padBottom(5f)
            add("Turn order").padBottom(5f)
            add("").padBottom(5f)
            add("Speed").padBottom(5f).row()

            add(Label("Now:", createLabelStyle(Color.GOLD))).padBottom(5f)
            add(createImageOf(participants[0])).padBottom(5f)
            add(Label(participants[0].character.name, createLabelStyle(Color.GOLD))).padBottom(5f)
            addSpeedCell(participants[0], Color.GOLD).padBottom(5f).row()
            possibleAddPadBottom(participants[0], participants)

            add("Next:")
            if (participants.size > 1) {
                add(createImageOf(participants[1]))
                add(participants[1].character.name)
                addSpeedCell(participants[1]).row()
                possibleAddPadBottom(participants[1], participants)

                participants.drop(2).forEach {
                    add("")
                    add(createImageOf(it))
                    add(it.character.name)
                    addSpeedCell(it).row()
                    possibleAddPadBottom(it, participants)
                }
            }
            background = transparent
            pack()
            setPosition(450f, Gdx.graphics.height - height - 20f)
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

    private fun Table.possibleAddPadBottom(participant: Participant, participants: List<Participant>) {
        if (participant == participants.last()) {
            padBottom(10f)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createButtonTableAction(): Table {
        return createStyledEmptyList<String>().fillWithActions().toActionTable()
    }

    fun createButtonTableAttack(currentParticipant: Participant): Table {
        return createStyledEmptyList<String>().fillWithAttacksFor(currentParticipant).toAttackTable()
    }

    fun createButtonTableMove(): Table {
        return createSelectionTable().apply {
            add("Move left or right and confirm.")
            background = transparent
            pack()
            y = Gdx.graphics.height - height - 20f
        }
    }

    fun createButtonTableTarget(enemies: List<Participant>): Table {
        return createStyledEmptyList<String>().fillWithTargets(enemies).toTargetTable()
    }

    fun createButtonTablePotion(potions: List<BattlePotionItem>): Table {
        return createStyledEmptyList<BattlePotionItem>().fillWithPotions(potions).toPotionTable()
    }

    fun createButtonTableWeapon(weapons: List<BattleWeaponItem>, currentWeapon: InventoryItem?): Table {
        return createStyledEmptyList<BattleWeaponItem>().fillWithWeapons(weapons).toWeaponTable(currentWeapon)
    }

    private fun GdxList<String>.fillWithActions(): GdxList<String> {
        this.setItems(
            "Attack (? AP)",
            "Move (1+ AP)",
            "Potion (3 AP)",
            "Switch weapon (3 AP)",
            "Rest (2 AP)",
            "Preview hit and damage",
            "Inventory",
            "Flee battle",
            "End turn"
        )
        this.selectedIndex = buttonTableActionIndex
        return this
    }

    private fun GdxList<String>.fillWithAttacksFor(current: Participant): GdxList<String> {
        val weaponRange: List<Int>? = current.character.getInventoryItem(InventoryGroup.WEAPON)?.getWeaponRange()
        val rangeText: String? = weaponRange?.let { if (it.size == 1) "Range: 1" else "Range: ${it.first()} - ${it.last()}" }
        val attackText: String = rangeText?.let { "Strike ($it) (3 AP)" } ?: "No weapon equipped"
        val attacks: List<String> = listOf(attackText, "Back")

        this.setItems(*attacks.toTypedArray())
        this.selectedIndex = 0
        return this
    }

    private fun GdxList<String>.fillWithTargets(enemies: List<Participant>): GdxList<String> {
        enemies
            .filter { it.character.isAlive }
            .forEach { items.add(it.character.name) }
        items.add("Back")
        this.selectedIndex = 0
        return this
    }

    private fun GdxList<BattlePotionItem>.fillWithPotions(potions: List<BattlePotionItem>): GdxList<BattlePotionItem> {
        this.setItems(*potions.toTypedArray())
        items.add(BattlePotionItem("Back"))
        this.selectedIndex = 0
        return this
    }

    private fun GdxList<BattleWeaponItem>.fillWithWeapons(weapons: List<BattleWeaponItem>): GdxList<BattleWeaponItem> {
        this.setItems(*weapons.toTypedArray())
        items.add(BattleWeaponItem("Back"))
        this.selectedIndex = 0
        return this
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun GdxList<String>.toActionTable(): Table {
        val listWithActions = this
        return createSelectionTable().apply {
            add("Select Action:").padBottom(10f).row()
            finish(listWithActions)
        }
    }

    private fun GdxList<String>.toAttackTable(): Table {
        val listWithAttacks = this
        return createSelectionTable().apply {
            add("Select Attack:").padBottom(10f).row()
            finish(listWithAttacks)
        }
    }

    private fun GdxList<String>.toTargetTable(): Table {
        val listWithTargets = this
        return createSelectionTable().apply {
            add("Select Target:").padBottom(10f).row()
            finish(listWithTargets)
        }
    }

    private fun GdxList<BattlePotionItem>.toPotionTable(): Table {
        val listWithPotions = this
        return createSelectionTable().apply {
            add("Select Potion (Amount):").padBottom(10f).row()
            finish(listWithPotions)
        }
    }

    private fun GdxList<BattleWeaponItem>.toWeaponTable(currentWeapon: InventoryItem?): Table {
        val listWithWeapons = this
        return createSelectionTable().apply {
            add("Select Weapon (Uses):").padBottom(10f).row()
            currentWeapon?.let { add("Current: ${it.name} (${it.durability})").row() }
            finish(listWithWeapons)
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
        return Label("${character.currentHp} ", barFontStyle).apply { setAlignment(Align.right) }
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
        return Label("${hero.currentSp} ", barFontStyle).apply { setAlignment(Align.right) }
    }

    private fun Color.toImage(): Image {
        val texture = colorTextureCache.getOrPut(this) { this.toTexture() }
        return Image(texture)
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
            defaults().top().left()
            padLeft(10f)
            padRight(10f)
            x = 1050f
        }
    }

    private fun <T> Table.finish(listWithActions: GdxList<T>) {
        add(listWithActions)
        background = transparent
        pack()
        y = Gdx.graphics.height - height - 20f
    }

    private fun createSkin(): Skin {
        val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, FONT_SIZE)
        return Skin().apply { add("default", LabelStyle(font, Color.WHITE)) }
    }

}

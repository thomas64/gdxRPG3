package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import ktx.assets.disposeSafely
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.EnemyItem
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.disposeAndClear
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.toDrawable
import nl.t64.cot.toTexture
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


private const val TITLE_TEXT = "Battle...!"
private const val BAR_WIDTH = 100f
private const val BAR_HEIGHT = 18f

class BattleScreenBuilder {

    private val colorTextureCache: MutableMap<Color, Texture> = mutableMapOf()
    private val tableSkin: Skin = createSkin()
    private val barFontStyle = LabelStyle(FontProvider.default, Color.WHITE)
    private val transparent: Drawable = Utils.createTransparency()
    private val border: Drawable = Utils.createFullBorderWhite()
    private val combined: Drawable = Utils.createCombinedDrawable(transparent, border)

    var buttonTableSelectHeroIndex = 0
    var buttonTableMainMenuIndex = 0
    var buttonTableSelectAttackIndex = 0

    fun createBattleTitle(): Label {
        val style = LabelStyle(FontProvider.spectralRegular24, Color.WHITE)
        return Label(TITLE_TEXT, style).apply {
            setPosition((Gdx.graphics.width / 2f) - (width / 2f), (Gdx.graphics.height / 2f) - (height / 2f))
            isVisible = false
        }
    }

    fun dispose() {
        colorTextureCache.disposeAndClear()
        tableSkin.disposeSafely()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createHeroTable(heroes: List<HeroItem>,
                        getCurrentAp: (Character) -> Int,
                        currentParticipantName: String
    ): Table {
        return Table(tableSkin).apply {
            defaults().height(Constant.FACE_SIZE).spaceBottom(4f)
            columnDefaults(0).width(Constant.FACE_SIZE)
            top().left()
            setPosition(20f, Gdx.graphics.height - 20f)
            heroes.forEach { addHero(it, getCurrentAp, currentParticipantName) }
        }
    }

    private fun Table.addHero(hero: HeroItem,
                              getCurrentAp: (Character) -> Int,
                              currentParticipantName: String
    ) {
        val currentAp: Int = getCurrentAp.invoke(hero)
        val maximumAP: Int = hero.getCalculatedActionPoints()

        val heroTable = Table(tableSkin).apply {
            defaults().left().height(30f)
            add(hero.name).width(150f).colspan(2).padLeft(10f).padRight(10f).row()
            add("HP:").width(50f).padLeft(10f)
            add(createHpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            add("SP:").width(50f).padLeft(10f)
            add(createSpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            add("AP:").width(50f).padLeft(10f)
            add("$currentAp/$maximumAP").width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            background = transparent
        }

        val statsStack = Stack()
        statsStack.add(heroTable)

        hero.getInventoryItem(InventoryGroup.WEAPON)?.let {
            val textureRegion = resourceManager.getAtlasTexture(it.id)
            val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
            val container = Container(image).top().right().size(35f).pad(5f)
            val table = Table(tableSkin).apply { add(container).width(170f).height(Constant.FACE_SIZE) }
            statsStack.add(table)
        }

        hero.getInventoryItem(InventoryGroup.SHIELD)?.let {
            val textureRegion = resourceManager.getAtlasTexture(it.id)
            val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
            val container = Container(image).bottom().right().size(35f).pad(5f)
            val table = Table(tableSkin).apply { add(container).width(170f).height(Constant.FACE_SIZE) }
            statsStack.add(table)
        }

        statsStack.add(Image(Utils.createFullBorderWhite()))

        add(createFaceImage(hero, currentParticipantName, isFlipped = true))
        add(statsStack).row()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createEnemyTable(enemies: List<EnemyItem>, currentParticipantName: String): Table {
        return Table(tableSkin).apply {
            defaults().height(Constant.FACE_SIZE).spaceBottom(4f)
            columnDefaults(1).width(Constant.FACE_SIZE)
            top().left()
            setPosition(Gdx.graphics.width - Constant.FACE_SIZE - 170f - 40f, Gdx.graphics.height - 20f)
            enemies.forEach { addEnemy(it, currentParticipantName) }
        }
    }

    private fun Table.addEnemy(enemy: EnemyItem, currentParticipantName: String) {
        val enemyTable = Table(tableSkin).apply {
            defaults().left()
            add(enemy.name).width(170f).height(28f).colspan(2).padLeft(10f).padRight(10f).row()
            add("HP:").width(50f).height(28f).padLeft(10f)
            add(createHpBar(enemy)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            background = transparent
        }

        val statsStack = Stack()
        statsStack.add(enemyTable)

        // todo, dit is een enorme tijdelijke regel, op deze manier aan de verwijderde bite skill komen, is niet heel flexibel.
        // idee om het op te lossen: attackName: String toevoegen aan EnemyItem, en alleen onderstaande in de json invullen.
        // neem attackName als deze gevuld is met bite of body_slam oid, en als deze leeg is pak het wapen.
        val skillName: String? = when {
            enemy.id.endsWith("_bat") -> "bite"
            enemy.id.endsWith("_slime") || enemy.id.endsWith("_slime_medicine") -> "body_slam"
            else -> enemy.getInventoryItem(InventoryGroup.WEAPON)?.id
        }

        skillName?.let {
            val textureRegion = resourceManager.getAtlasTexture(it)
            val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
            val container = Container(image).top().left().size(35f).pad(5f)
            val table = Table(tableSkin).apply { add(container).width(190f).height(Constant.FACE_SIZE) }
            statsStack.add(table)
        }

        enemy.getInventoryItem(InventoryGroup.SHIELD)?.let {
            val textureRegion = resourceManager.getAtlasTexture(it.id)
            val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
            val container = Container(image).bottom().left().size(35f).pad(5f)
            val table = Table(tableSkin).apply { add(container).width(190f).height(Constant.FACE_SIZE) }
            statsStack.add(table)
        }

        statsStack.add(Image(Utils.createFullBorderWhite()))

        add(statsStack)
        add(createFaceImage(enemy, currentParticipantName, isFlipped = false)).row()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createTurnTable(participants: List<Participant>): Table {
        return Table(tableSkin).apply {
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
            background = combined
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

    fun createButtonTablePreBattle(): Table {
        return createStyledEmptyList<String>().fillWithPreBattleActions().toPreBattleTable()
    }

    fun createButtonTableHero(heroes: List<Participant>): Table {
        return createStyledEmptyList<String>().fillWithHeroes(heroes).toHeroTable()
    }

    fun createButtonTableAction(currentParticipant: Participant, areEnemiesInRange: Boolean): Table {
        return createStyledEmptyList<String>().fillWithActions(currentParticipant, areEnemiesInRange).toActionTable()
    }

    fun createButtonTablePreviewAttack(currentParticipant: Participant): Table {
        return createStyledEmptyList<BattleAbilityItem>().fillWithPreviewAttacks(currentParticipant).toAttackTable()
    }

    fun createButtonTableAttack(currentParticipant: Participant): Table {
        return createStyledEmptyList<BattleAbilityItem>().fillWithAttacks(currentParticipant).toAttackTable()
    }

    fun createButtonTableSpecial(currentParticipant: Participant): Table {
        return createStyledEmptyList<BattleAbilityItem>().fillWithSpecials(currentParticipant).toSpecialTable()
    }

    fun createButtonTableMove(): Table {
        return createSelectionTable().apply {
            add("Move left or right and confirm.")
            background = combined
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

    fun createButtonTableWeapon(equipment: List<BattleWeaponItem>,
                                currentWeapon: InventoryItem?,
                                currentShield: InventoryItem?
    ): Table {
        return createStyledEmptyList<BattleWeaponItem>()
            .fillWithWeapons(equipment, currentWeapon, currentShield)
            .toWeaponTable(currentWeapon, currentShield)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun GdxList<String>.fillWithPreBattleActions(): GdxList<String> {
        items.add("Party preparation")
        items.add("Select equipment")
        items.add("Preview attacks")
        items.add("Start battle")
        this.selectedIndex = buttonTableMainMenuIndex
        return this
    }

    private fun GdxList<String>.fillWithHeroes(heroes: List<Participant>): GdxList<String> {
        heroes
            .filter { it.character.isAlive }
            .forEach { items.add(it.character.name) }
        items.add("Back")
        this.selectedIndex = buttonTableSelectHeroIndex
        return this
    }

    private fun GdxList<String>.fillWithActions(currentParticipant: Participant,
                                                areEnemiesInRange: Boolean): GdxList<String> {
        val attackAp: Int = if (areEnemiesInRange) 2 else 99
        val curAp: Int = currentParticipant.currentAP
        val maxAp: Int = currentParticipant.maximumAP
        val actions: List<Pair<String, Int>> = listOf(
            // @formatter:off
            String.format("%-12s%6s", "Attack",     "? AP")         to attackAp,
            String.format("%-12s%6s", "Special",    "4 AP")         to 4,
            String.format("%-12s%6s", "Move",       if (curAp <= 1) "1 AP" else "1-$curAp AP") to 1,
            String.format("%-12s%6s", "Equipment",  "3 AP")         to 3,
            String.format("%-12s%6s", "Preview",    "")             to 0,
            String.format("%-12s%6s", "Potion",     "3 AP")         to 3,
            String.format("%-12s%6s", "Party",      "")             to 0,
            String.format("%-12s%6s", "Flee",       "$maxAp AP")    to maxAp,
            String.format("%-12s%6s", "Delay turn", "1 AP")         to 1,
            String.format("%-12s%6s", "Rest",       "$curAp AP")    to 1,
            String.format("%-12s%6s", "End turn",   "")             to 0
            // @formatter:on
        )
        val actionStrings: List<String> = actions.map { (action, ap) ->
            "${currentParticipant.getColorBasedOn(ap, action)}$action"
        }
        this.setItems(*actionStrings.toTypedArray())

        if (!areEnemiesInRange && buttonTableMainMenuIndex == 0) {
            buttonTableMainMenuIndex = 2
        }
        if (currentParticipant.currentAP <= 1
            || this.items[buttonTableMainMenuIndex].startsWith("[GRAY]")
        ) {
            buttonTableMainMenuIndex = 10
        }
        this.selectedIndex = buttonTableMainMenuIndex
        return this
    }

    private fun Participant.getColorBasedOn(requestedAp: Int, action: String): String {
        if (action.contains("Special") && this.getBattleAbilities().none { it.abilityItem.isSpecial }) return "[GRAY]"
        return if (this.currentAP < requestedAp) "[GRAY]" else ""
    }

    private fun GdxList<BattleAbilityItem>.fillWithPreviewAttacks(currentParticipant: Participant): GdxList<BattleAbilityItem> {
        val abilities: List<BattleAbilityItem> = currentParticipant.getBattleAbilities()
            .filterNot { it.abilityItem.isSpecial }
            .map { it.createCopyForPreview() }
        val allItems: List<BattleAbilityItem> = abilities + createBackButton(currentParticipant)
        this.setItems(*allItems.toTypedArray())
        this.setSelectedIndex()
        return this
    }

    private fun GdxList<BattleAbilityItem>.fillWithAttacks(currentParticipant: Participant): GdxList<BattleAbilityItem> {
        val abilities: List<BattleAbilityItem> = currentParticipant.getBattleAbilities()
            .filterNot { it.abilityItem.isSpecial }
        val allItems: List<BattleAbilityItem> = abilities + createBackButton(currentParticipant)
        this.setItems(*allItems.toTypedArray())
        this.setSelectedIndex()
        return this
    }

    private fun GdxList<BattleAbilityItem>.fillWithSpecials(currentParticipant: Participant): GdxList<BattleAbilityItem> {
        val abilities: List<BattleAbilityItem> = currentParticipant.getBattleAbilities()
            .filter { it.abilityItem.isSpecial }
        val allItems: List<BattleAbilityItem> = abilities + createBackButton(currentParticipant)
        this.setItems(*allItems.toTypedArray())
        this.setSelectedIndex()
        return this
    }

    private fun GdxList<BattleAbilityItem>.setSelectedIndex() {
        if (buttonTableSelectAttackIndex > this.items.size - 1
            || this.items[buttonTableSelectAttackIndex].toString().startsWith("[GRAY]")
        ) {
            buttonTableSelectAttackIndex = this.items.size - 1
        }
        this.selectedIndex = buttonTableSelectAttackIndex
    }

    private fun createBackButton(currentParticipant: Participant): BattleAbilityItem {
        return object : BattleAbilityItem(AbilityItem(name = "Back"), currentParticipant) {
            override fun createCopyForPreview(): BattleAbilityItem = this
            override fun createPreviewMessage(): String = ""
            override fun handleSuccess(attackData: AttackData) {}
        }
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
        val allItems: List<BattlePotionItem> = potions + BattlePotionItem("Back")
        this.setItems(*allItems.toTypedArray())
        this.selectedIndex = 0
        return this
    }

    private fun GdxList<BattleWeaponItem>.fillWithWeapons(weapons: List<BattleWeaponItem>,
                                                          currentWeapon: InventoryItem?,
                                                          currentShield: InventoryItem?
    ): GdxList<BattleWeaponItem> {
        val allItems: List<BattleWeaponItem> = buildList {
            addAll(weapons)
            currentWeapon?.let { add(BattleWeaponItem(InventoryItem(name = "Unequip current weapon", group = InventoryGroup.WEAPON))) }
            currentShield?.let { add(BattleWeaponItem(InventoryItem(name = "Unequip current shield", group = InventoryGroup.SHIELD))) }
            add(BattleWeaponItem(InventoryItem(name = "Back")))
        }
        this.setItems(*allItems.toTypedArray())
        this.selectedIndex = 0
        return this
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun GdxList<String>.toPreBattleTable(): Table {
        val listWithActions = this
        return createSelectionTable().apply {
            add("Prepare for Battle:").padBottom(10f).row()
            finish(listWithActions)
        }
    }

    private fun GdxList<String>.toHeroTable(): Table {
        val listWithHeroes = this
        return createSelectionTable().apply {
            add("Select Hero:").padBottom(10f).row()
            finish(listWithHeroes)
        }
    }

    private fun GdxList<String>.toActionTable(): Table {
        val listWithActions = this
        return createSelectionTable().apply {
            add("Select Action:").padBottom(10f).row()
            finish(listWithActions)
        }
    }

    private fun GdxList<BattleAbilityItem>.toAttackTable(): Table {
        val listWithAttacks = this
        return createSelectionTable().apply {
            add("Select Attack:").padBottom(10f).row()
            finish(listWithAttacks)
        }
    }

    private fun GdxList<BattleAbilityItem>.toSpecialTable(): Table {
        val listWithSpecials = this
        return createSelectionTable().apply {
            add("Select Spell:").padBottom(10f).row()
            finish(listWithSpecials)
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

    private fun GdxList<BattleWeaponItem>.toWeaponTable(currentWeapon: InventoryItem?,
                                                        currentShield: InventoryItem?): Table {
        val listWithEquipment = this
        val weapon: String = currentWeapon?.let { "${it.name} (${it.durability})" } ?: "None"
        val shield: String = currentShield?.let { "${it.name} (${it.durability})" } ?: "None"

        return createSelectionTable().apply {
            add("Current Weapon: $weapon").row()
            add("Current Shield: $shield").row()
            add("______________________________").padTop(-10f).padBottom(5f).row()
            add("Select Weapon or Shield:").row()
            add("______________________________").padTop(-10f).padBottom(10f).row()
            finish(listWithEquipment)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun createFaceImage(character: Character,
                                currentParticipantName: String,
                                isFlipped: Boolean
    ): Actor {
        val faceImage = Utils.getFaceImage(character.id, isFlipped)
            .apply { if (character.isDead) color = Color.DARK_GRAY }

        if (character.name == currentParticipantName) {
            val stack = Stack()
            stack.add(Constant.GRAY.toImage())
            stack.add(faceImage)
            stack.add(Image(Utils.createFullBorderWhite()).apply { color = Color.GOLD })
            return stack
        } else {
            return faceImage
        }
    }

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

    private fun <T> createStyledEmptyList(): GdxList<T> {
        return GdxList<T>(ListStyle().apply {
            font = FontProvider.inconsolata24
            fontColorSelected = Color.GOLD
            fontColorUnselected = Color.WHITE
            background = Color.CLEAR.toDrawable()
            selection = Color.CLEAR.toDrawable()
            selection.topHeight = 3f
            selection.bottomHeight = 3f
        })
    }

    private fun createSelectionTable(): Table {
        return Table(tableSkin).apply {
            defaults().top().left()
            padTop(5f)
            padLeft(10f)
            padRight(10f)
            x = 1050f
        }
    }

    private fun <T> Table.finish(listWithActions: GdxList<T>) {
        add(listWithActions)
        background = combined
        pack()
        y = Gdx.graphics.height - height - 20f
    }

    private fun createSkin(): Skin {
        val style = createLabelStyle(Color.WHITE)
        return Skin().apply { add("default", style) }
    }

    private fun createLabelStyle(color: Color): LabelStyle {
        return LabelStyle(FontProvider.inconsolata24, color)
    }

}

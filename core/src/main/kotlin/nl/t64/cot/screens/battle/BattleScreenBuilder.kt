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
import nl.t64.cot.Utils.gameData
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
private const val BAR_WIDTH = 145f
private const val BAR_HEIGHT = 18f
private const val AP_UNAVAILABLE = 99

class BattleScreenBuilder {

    private val colorTextureCache: MutableMap<Color, Texture> = mutableMapOf()
    private val tableSkin: Skin = createSkin()
    private val smallFontStyle = LabelStyle(FontProvider.default, Color.WHITE)
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
        val statsStack = Stack()
        statsStack.add(createStatsTableFor(hero, getCurrentAp))
        statsStack.add(createWeaponSlotFor(hero))
        statsStack.add(createShieldSlotFor(hero))
        statsStack.add(Image(Utils.createFullBorderWhite()))

        add(createFaceImage(hero, currentParticipantName, isFlipped = true))
        add(statsStack).row()
    }

    private fun createStatsTableFor(hero: HeroItem, getCurrentAp: (Character) -> Int): Table {
        val currentAp: Int = getCurrentAp.invoke(hero)
        val maximumAP: Int = hero.getCalculatedActionPoints()

        return Table(tableSkin).apply {
            defaults().left().height(30f)
            add(hero.name).width(185f).colspan(2).padLeft(10f).padRight(10f).row()
            add("HP:").width(40f).padLeft(10f)
            add(createHpBar(hero, true)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            add("SP:").width(40f).padLeft(10f)
            add(createSpBar(hero)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            add("AP:").width(40f).padLeft(10f)
            add(createApDots(currentAp, maximumAP)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            background = transparent
        }
    }

    private fun createWeaponSlotFor(hero: HeroItem): Table {
        val weaponItem: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val weaponStack = Stack().apply {
            if (weaponItem != null) {
                val textureRegion = resourceManager.getAtlasTexture(weaponItem.id)
                val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
                val container = Container(image).top().right().size(35f).padTop(5f).padRight(45f)
                add(container)
            }
            val image = Image(Utils.createFullBorderWhite())
            val container = Container(image).top().right().size(35f).padTop(5f).padRight(45f)
            add(container)
        }
        return Table(tableSkin).apply {
            add(weaponStack).width(205f).height(Constant.FACE_SIZE)
        }
    }

    private fun createShieldSlotFor(hero: HeroItem): Table {
        val shieldItem: InventoryItem? = hero.getInventoryItem(InventoryGroup.SHIELD)
        val shieldStack = Stack().apply {
            if (shieldItem != null) {
                val textureRegion = resourceManager.getAtlasTexture(shieldItem.id)
                val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
                val container = Container(image).top().right().size(35f).padTop(5f).padRight(5f)
                add(container)
            }
            val image = Image(Utils.createFullBorderWhite())
            val container = Container(image).top().right().size(35f).padTop(5f).padRight(5f)
            add(container)
        }
        return Table(tableSkin).apply {
            add(shieldStack).width(205f).height(Constant.FACE_SIZE)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createEnemyTable(enemies: List<EnemyItem>,
                         getCurrentAp: (Character) -> Int,
                         currentParticipantName: String
    ): Table {
        return Table(tableSkin).apply {
            defaults().height(Constant.FACE_SIZE).spaceBottom(4f)
            columnDefaults(1).width(Constant.FACE_SIZE)
            top().left()
            setPosition(Gdx.graphics.width - Constant.FACE_SIZE - 185f - 40f, Gdx.graphics.height - 20f)
            enemies.forEach { addEnemy(it, getCurrentAp, currentParticipantName) }
        }
    }

    private fun Table.addEnemy(enemy: EnemyItem,
                               getCurrentAp: (Character) -> Int,
                               currentParticipantName: String
    ) {
        val statsStack = Stack()
        statsStack.add(createStatsTableFor(enemy, getCurrentAp))
        statsStack.add(createWeaponSlotFor(enemy))
        statsStack.add(createShieldSlotFor(enemy))
        statsStack.add(Image(Utils.createFullBorderWhite()))

        add(statsStack)
        add(createFaceImage(enemy, currentParticipantName, isFlipped = false)).row()
    }

    private fun createStatsTableFor(enemy: EnemyItem, getCurrentAp: (Character) -> Int): Table {
        val isEnemyKnown: Boolean = gameData.inventory.containsBook(enemy.id)
        val currentAp: Int = getCurrentAp.invoke(enemy)
        val maximumAP: Int = enemy.getCalculatedActionPoints()

        return Table(tableSkin).apply {
            defaults().left().height(30f)
            top().padTop(42f)
            add(enemy.name).width(185f).colspan(2).padLeft(10f).padRight(10f).row()
            add("HP:").width(40f).padLeft(10f)
            add(createHpBar(enemy, isEnemyKnown)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            if (isEnemyKnown) {
                add("AP:").width(40f).padLeft(10f)
                add(createApDots(currentAp, maximumAP)).width(BAR_WIDTH).height(BAR_HEIGHT).padRight(10f).row()
            }
            background = transparent
        }
    }

    private fun createWeaponSlotFor(enemy: EnemyItem): Table {
        // todo, dit is een enorme tijdelijke regel, op deze manier aan de verwijderde bite skill komen, is niet heel flexibel.
        // idee om het op te lossen: attackName: String toevoegen aan EnemyItem, en alleen onderstaande in de json invullen.
        // neem attackName als deze gevuld is met bite of body_slam oid, en als deze leeg is pak het wapen.
        val skillName: String? = when {
            enemy.id.endsWith("_bat") -> "bite"
            enemy.id.endsWith("_slime") || enemy.id.endsWith("_slime_medicine") -> "body_slam"
            enemy.id.endsWith("_imp") -> "scratch"
            else -> enemy.getInventoryItem(InventoryGroup.WEAPON)?.id
        }

        val weaponStack = Stack().apply {
            if (skillName != null) {
                val textureRegion = resourceManager.getAtlasTexture(skillName)
                val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
                val container = Container(image).top().left().size(35f).padTop(5f).padLeft(5f)
                add(container)
            }
            val image = Image(Utils.createFullBorderWhite())
            val container = Container(image).top().left().size(35f).padTop(5f).padLeft(5f)
            add(container)
        }
        return Table(tableSkin).apply {
            add(weaponStack).width(205f).height(Constant.FACE_SIZE)
        }
    }

    private fun createShieldSlotFor(enemy: EnemyItem): Table {
        val shieldItem = enemy.getInventoryItem(InventoryGroup.SHIELD)
        val shieldStack = Stack().apply {
            if (shieldItem != null) {
                val textureRegion = resourceManager.getAtlasTexture(shieldItem.id)
                val image = Image(textureRegion).apply { setScaling(Scaling.fit) }
                val container = Container(image).top().left().size(35f).padTop(5f).padLeft(45f)
                add(container)
            }
            val image = Image(Utils.createFullBorderWhite())
            val container = Container(image).top().left().size(35f).padTop(5f).padLeft(45f)
            add(container)
        }
        return Table(tableSkin).apply {
            add(shieldStack).width(205f).height(Constant.FACE_SIZE)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createTurnTable(participants: List<Participant>, enemyCountMap: Map<String, Int>): Table {
        return Table(tableSkin).apply {
            defaults().size(60f).padRight(1f)
            pad(8f)
            participants.forEachIndexed { index, participant ->
                add(createForecastCell(participant, isCurrent = index == 0, enemyCountMap))
            }
            background = combined
            pack()
            setPosition((Gdx.graphics.width - width) / 2f, Gdx.graphics.height - height - 20f)
        }
    }

    private fun createForecastCell(participant: Participant, isCurrent: Boolean, enemyCountMap: Map<String, Int>): Stack {
        return Stack().apply {
            if (isCurrent) {
                add(Constant.GRAY.toImage())
            }
            add(createImageOf(participant))
            add(Image(Utils.createFullBorderWhite()).apply { if (isCurrent) color = Color.GOLD })
            addPossibleForecastCount(participant, enemyCountMap)
            addSpeedOverlay(participant)
        }
    }

    private fun Stack.addSpeedOverlay(participant: Participant) {
        val speedText: String = if (participant.isHero || gameData.inventory.containsBook(participant.character.id)) {
            participant.character.getCalculatedTotalStatOf(StatItemId.SPEED).toString()
        } else {
            "?"
        }
        add(Container(Label(speedText, smallFontStyle)).bottom().right().padRight(2f))
    }

    private fun createImageOf(participant: Participant): Container<Image> {
        return Container(
            Image(Utils.getCharImage(participant.character.id)[0][1])
                .apply { setScaling(Scaling.none) }
        )
    }

    private fun Stack.addPossibleForecastCount(participant: Participant, enemyCountMap: Map<String, Int>) {
        if ((enemyCountMap[participant.character.id] ?: 0) > 1) {
            add(Container(Label(participant.character.name.last().toString(), smallFontStyle)).top().right().padRight(2f))
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createButtonTablePreBattle(): Table {
        return createStyledEmptyList<String>().fillWithPreBattleActions().toPreBattleTable()
    }

    fun createButtonTableHero(heroes: List<Participant>): Table {
        return createStyledEmptyList<String>().fillWithHeroes(heroes).toHeroTable()
    }

    fun createButtonTableAction(currentParticipant: Participant,
                                areEnemiesInRange: Boolean,
                                isAbleToMove: Boolean): Table {
        return createStyledEmptyList<String>()
            .fillWithActions(currentParticipant, areEnemiesInRange, isAbleToMove)
            .toActionTable()
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
            x = (Gdx.graphics.width - width) / 2f
            y = Gdx.graphics.height * 0.85f - height
        }
    }

    fun createButtonTableTarget(enemies: List<String>): Table {
        return createStyledEmptyList<String>().fillWithTargets(enemies).toTargetTable()
    }

    fun createButtonTablePotion(potions: List<InventoryItem>): Table {
        return createStyledEmptyList<BattlePotionItem>().fillWithPotions(potions).toPotionTable()
    }

    fun createButtonTableWeapon(equipment: List<InventoryItem>,
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
                                                areEnemiesInRange: Boolean,
                                                isAbleToMove: Boolean): GdxList<String> {
        val attackAp: Int = currentParticipant.getCheapestUsableAttackApAnd(areEnemiesInRange)
        val moveApInt: Int = if (isAbleToMove) 1 else AP_UNAVAILABLE
        val curAp: Int = maxOf(1, currentParticipant.currentAP)
        val maxAp: Int = currentParticipant.maximumAP
        val moveApString: String = buildMoveApStringFrom(curAp)
        val fleeAp: String = buildFleeApStringFrom(curAp, maxAp)

        val actions: List<Pair<String, Int>> = listOf(
            // @formatter:off
            String.format("%-11s%7s", "Attack",     "? AP")             to attackAp,
            String.format("%-11s%7s", "Special",    "? AP")             to 3,
            String.format("%-11s%7s", "Move",       "$moveApString AP") to moveApInt,
            String.format("%-11s%7s", "Equipment",  "3 AP")             to 3,
            String.format("%-11s%7s", "Potion",     "3 AP")             to 3,
            String.format("%-11s%7s", "Preview",    "")                 to 0,
            String.format("%-11s%7s", "Party",      "")                 to 0,
            String.format("%-11s%7s", "Flee party", "$fleeAp AP")       to fleeAp.toInt(),
            String.format("%-11s%7s", "Delay turn", "1 AP")             to 1,
            String.format("%-11s%7s", "Rest",       "$curAp AP")        to 1,
            String.format("%-11s%7s", "End turn",   "")                 to 0
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

    private fun Participant.getCheapestUsableAttackApAnd(areEnemiesInRange: Boolean): Int {
        if (!areEnemiesInRange) return AP_UNAVAILABLE

        return getBattleAbilities()
            .filterNot { it.abilityItem.isSpecial }
            .filter { it.isWeaponAllowed() && it.hasEnoughApSp() }
            .minOfOrNull { it.ap }
            ?: AP_UNAVAILABLE
    }

    private fun buildMoveApStringFrom(curAp: Int): String {
        return if (curAp <= 1) "1" else "1-$curAp"
    }

    private fun buildFleeApStringFrom(curAp: Int, maxAp: Int): String {
        return if (curAp >= maxAp) "$curAp" else "$maxAp"
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
            val firstAvailableIndex: Int = this.items.indexOfFirst { !it.toString().startsWith("[GRAY]") }
            buttonTableSelectAttackIndex = if (firstAvailableIndex == -1) this.items.size - 1 else firstAvailableIndex
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

    private fun GdxList<String>.fillWithTargets(enemies: List<String>): GdxList<String> {
        enemies.forEach { items.add(it) }
        items.add("Back")
        this.selectedIndex = 0
        return this
    }

    private fun GdxList<BattlePotionItem>.fillWithPotions(potions: List<InventoryItem>): GdxList<BattlePotionItem> {
        val allItems: List<BattlePotionItem> = buildList {
            addAll(potions.map { BattlePotionItem(it) })
            add(BattlePotionItem("Back"))
        }
        this.setItems(*allItems.toTypedArray())
        this.selectedIndex = 0
        return this
    }

    private fun GdxList<BattleWeaponItem>.fillWithWeapons(weapons: List<InventoryItem>,
                                                          currentWeapon: InventoryItem?,
                                                          currentShield: InventoryItem?
    ): GdxList<BattleWeaponItem> {
        val allItems: List<BattleWeaponItem> = buildList {
            addAll(weapons.map { BattleWeaponItem(it) })
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
        val weaponLine: String = createItemLine("Current Weapon:", currentWeapon)
        val shieldLine: String = createItemLine("Current Shield:", currentShield)
        val line: String = createLine(weaponLine, shieldLine)

        return createSelectionTable().apply {
            add(weaponLine).row()
            add(shieldLine).row()
            add(line).padTop(-10f).padBottom(5f).row()
            add("Select Weapon or Shield:").row()
            add(line).padTop(-10f).padBottom(10f).row()
            finish(listWithEquipment)
        }
    }

    private fun createItemLine(prefix: String, currentItem: InventoryItem?): String {
        return currentItem?.let { "$prefix ${it.name} (${it.durability}/${it.maxDurability})" } ?: "$prefix None"
    }

    private fun createLine(weapon: String, shield: String): String {
        val minLineLength = 32
        val maxLength = maxOf(weapon.length, shield.length, minLineLength)
        return "_".repeat(maxLength)
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

    private fun createHpBar(character: Character, showLabel: Boolean): Stack {
        return Stack().apply {
            add(createHpFill(character))
            if (showLabel) add(createHpLabel(character))
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
        return Label("${character.currentHp} ", smallFontStyle).apply { setAlignment(Align.right) }
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
        return Label("${hero.currentSp} ", smallFontStyle).apply { setAlignment(Align.right) }
    }

    private fun createApDots(currentAp: Int, maximumAP: Int): Table {
        val apTable = Table().apply { left() }

        repeat(currentAp.coerceAtMost(maximumAP)) {
            val filledDot = Color.LIME.toImage()
            apTable.add(filledDot).size(10f, 12f).padRight(5f)
        }

        when {
            currentAp < maximumAP -> {
                repeat(maximumAP - currentAp) {
                    val emptyDot = Color.GRAY.toImage()
                    apTable.add(emptyDot).size(10f, 12f).padRight(5f)
                }
            }
            currentAp > maximumAP -> {
                repeat(currentAp - maximumAP) {
                    val bonusDot = Color.CYAN.toImage()
                    apTable.add(bonusDot).size(10f, 12f).padRight(5f)
                }
            }
        }

        return apTable
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
        }
    }

    private fun <T> Table.finish(listWithActions: GdxList<T>) {
        add(listWithActions)
        background = combined
        pack()
        x = (Gdx.graphics.width - width) / 2f
        y = Gdx.graphics.height * 0.85f - height
    }

    private fun createSkin(): Skin {
        val style = createLabelStyle(Color.WHITE)
        return Skin().apply { add("default", style) }
    }

    private fun createLabelStyle(color: Color): LabelStyle {
        return LabelStyle(FontProvider.inconsolata24, color)
    }

}

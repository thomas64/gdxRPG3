package nl.t64.cot.screens.battle.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.assets.disposeSafely
import nl.t64.cot.Utils
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.toDrawable
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


class BattleMenuBuilder {

    private val tableSkin: Skin = createSkin()
    private val combined: Drawable = Utils.createCombinedDrawable(Utils.createTransparency(),
                                                                  Utils.createFullBorderWhite())

    fun dispose() {
        tableSkin.disposeSafely()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createButtonTablePreBattle(options: List<BattleMenuOption>, selectedIndex: Int): Table {
        return createStyledEmptyList<BattleMenuOption>()
            .fillWith(options.toTypedArray(), selectedIndex)
            .toPreBattleTable()
    }

    fun createButtonTableHero(heroes: List<Participant>, selectedIndex: Int): Table {
        return createStyledEmptyList<String>().fillWithHeroes(heroes, selectedIndex).toHeroTable()
    }

    fun createButtonTableAction(options: List<BattleMenuOption>, selectedIndex: Int): Table {
        return createStyledEmptyList<BattleMenuOption>()
            .fillWith(options.toTypedArray(), selectedIndex)
            .toActionTable()
    }

    fun createButtonTableAttack(abilities: List<BattleAbilityItem>, selectedIndex: Int): Table {
        return createStyledEmptyList<BattleAbilityItem>()
            .fillWith(abilities.toTypedArray(), selectedIndex)
            .toAttackTable()
    }

    fun createButtonTableSpecial(abilities: List<BattleAbilityItem>, selectedIndex: Int): Table {
        return createStyledEmptyList<BattleAbilityItem>()
            .fillWith(abilities.toTypedArray(), selectedIndex)
            .toSpecialTable()
    }

    fun createButtonTableMove(): Table {
        return createSelectionTable().apply {
            add("Move left or right and confirm.")
            placeAboveBattleField()
        }
    }

    fun createButtonTableStealthMovement(heroName: String, stealthSteps: Int): Table {
        return createSelectionTable().apply {
            add("Take position: $heroName - Stealth $stealthSteps").padBottom(5f).row()
            add("Move left or right and confirm.")
            placeAboveBattleField()
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

    private fun Table.placeAboveBattleField() {
        background = combined
        pack()
        x = (Gdx.graphics.width - width) / 2f
        y = Gdx.graphics.height * 0.85f - height
    }

    private fun GdxList<String>.fillWithHeroes(heroes: List<Participant>,
                                               selectedIndex: Int): GdxList<String> {
        heroes
            .filter { it.character.isAlive }
            .forEach { items.add(it.character.name) }
        items.add("Back")
        this.selectedIndex = selectedIndex
        return this
    }

    private fun <T> GdxList<T>.fillWith(items: Array<T>, selectedIndex: Int): GdxList<T> {
        this.setItems(*items)
        this.selectedIndex = selectedIndex
        return this
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

    private fun GdxList<BattleMenuOption>.toPreBattleTable(): Table {
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

    private fun GdxList<BattleMenuOption>.toActionTable(): Table {
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
            padBottom(5f)
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
        val style = LabelStyle(FontProvider.inconsolata24, Color.WHITE)
        return Skin().apply { add("default", style) }
    }

}

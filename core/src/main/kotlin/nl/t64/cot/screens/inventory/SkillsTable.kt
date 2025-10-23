package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.alchemist.AlchemistScreen
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip
import nl.t64.cot.screens.mechanic.MechanicScreen


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 121f
private const val THIRD_COLUMN_WIDTH = 15f
private const val FOURTH_COLUMN_WIDTH = 40f
private const val FIFTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f
private const val TABLE_PAD_TOP = -3f
private const val SUBTITLE_PAD_TOP = 10f

internal class SkillsTable(
    tooltip: PersonalityTooltip,
    loadedFromSkillScreen: SkillItemId? = null
) : BaseTable(tooltip) {

    private val allSkills: List<SkillItem> get() = selectedHero.getAllSkillsAboveZero()
    private val verticalKeyListener = ListenerKeyVertical { updateIndex(it, allSkills.size) }
    private var deltaIndex = 0

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.columnDefaults(4).width(FIFTH_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)
        table.padTop(TABLE_PAD_TOP)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(verticalKeyListener)

        super.update()

        if (loadedFromSkillScreen != null) {
            selectedIndex = allSkills.indexOfFirst { it.id == loadedFromSkillScreen }
        }
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= allSkills.size) {
            selectedIndex = allSkills.size - 1
        }
    }

    override fun updateIndex(deltaIndex: Int, size: Int) {
        this.deltaIndex = deltaIndex
        super.updateIndex(deltaIndex, size)
    }

    override fun fillRows() {
        val communicationSkills = allSkills.filter { it.id.isCommunicationSkill() }
        val civilSkills = allSkills.filter { it.id.isCivilSkill() }
        val combatSkills = allSkills.filter { it.id.isCombatSkill() }
        val weaponSkills = allSkills.filter { it.id.isWeaponSkill() }

        if (communicationSkills.isNotEmpty()) {
            table.add("Communication Skills:").padTop(SUBTITLE_PAD_TOP).row()
            communicationSkills.forEach { fillRow(it) }
        }
        if (civilSkills.isNotEmpty()) {
            table.add("Civil Skills:").padTop(SUBTITLE_PAD_TOP).row()
            civilSkills.forEach { fillRow(it) }
        }
        if (combatSkills.isNotEmpty()) {
            table.add("Combat Skills:").padTop(SUBTITLE_PAD_TOP).row()
            combatSkills.forEach { fillRow(it) }
        }
        if (weaponSkills.isNotEmpty()) {
            table.add("Weapon Skills:").padTop(SUBTITLE_PAD_TOP).row()
            weaponSkills.forEach { fillRow(it) }
        }

        allSkills.forEachIndexed { index, skillItem ->
            table.findActor<Label>(skillItem.id.name)
                ?.let { super.possibleSetSelected(index, it, skillItem) }
        }
        if (deltaIndex != 0) {
            scrollScrollPane()
            deltaIndex = 0
        }
    }

    override fun doAction() {
        if (selectedHero.isDead) return

        val selectedSkill: SkillItem = allSkills[selectedIndex]
        val screenshot = table.stage.actors[0] as Image
        val parchment = table.stage.actors[1] as Image

        if (selectedSkill.id == SkillItemId.MECHANIC) {
            hideTooltip()
            MechanicScreen.load(selectedHero.id, screenshot, parchment)
        } else if (selectedSkill.id == SkillItemId.ALCHEMIST) {
            hideTooltip()
            AlchemistScreen.load(selectedHero.id, screenshot, parchment)
        }
    }

    fun stopScrolling() {
        verticalKeyListener.cleanup()
    }

    private fun fillRow(skillItem: SkillItem) {
        table.add(createImageOf(skillItem.id.name))
        val skillName = Label(skillItem.name, LabelStyle(font, Color.BLACK)).apply { name = skillItem.id.name }
        table.add(skillName).padLeft(SECOND_COLUMN_PAD_LEFT)
        val upgrade = Label("^", LabelStyle(font, Color.PURPLE))
        if (skillItem.canBeUpgraded()) table.add(upgrade) else table.add("")
        table.add(skillItem.rank.toString())
        val totalExtra = selectedHero.getExtraSkillForVisualOf(skillItem)
        addExtraToTable(totalExtra)
    }

    private fun SkillItem.canBeUpgraded(): Boolean {
        if (selectedHero.isDead) return false
        val xpCost: Int = this.getXpCostForNextRank()
        return xpCost > 0 && selectedHero.xpPoints >= xpCost
    }

    private fun scrollScrollPane() {
        if (!table.hasKeyboardFocus()) return
        val y = getSelected().y
        val rowScroll = if (y < table.height / 2f) {
            -1f * ROW_HEIGHT
        } else {
            5f * ROW_HEIGHT
        }
        val scrollDifference = table.height - scrollPane.height
        val scrollY = scrollPane.height - ((y + rowScroll) - scrollDifference)
        scrollPane.scrollTo(0f, scrollY, 0f, 0f)
    }

}

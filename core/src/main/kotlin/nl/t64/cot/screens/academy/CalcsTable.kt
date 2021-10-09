package nl.t64.cot.screens.academy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 230f
private const val SECOND_COLUMN_WIDTH = 60f
private const val THIRD_COLUMN_WIDTH = 35f

internal class CalcsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)

        container.add(table)
        container.background = Utils.createTopBorder()
    }

    override fun fillRows() {
        table.add(Label("XP to Invest", createLabelStyle()))
        table.add(Label(selectedHero.xpToInvest.toString(), createLabelStyle())).row()
        table.add(Label("Amount of Gold", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("gold").toString(), createLabelStyle())).row()
        table.add("").row()

        table.add(Label("Currently equipped", createLabelStyle())).row()
        table.add(Label("weapon skill type:", createLabelStyle()))
        table.add(Label(selectedHero.getWeaponSkillForVisual(), createLabelStyle())).row()
        table.add("").row()

        table.add(Label("Modified Hit-chance", createLabelStyle()))
        table.add(selectedHero.getCalculatedTotalHit().toString() + "%")
        addPossibleAcademyUpgradeToTable { selectedHero.getCalculatedTotalHit() }

        table.add(Label("Modified " + CalcAttributeId.DAMAGE.title, createLabelStyle()))
        table.add(selectedHero.getCalculatedTotalDamage().toString())
        addPossibleAcademyUpgradeToTable { selectedHero.getCalculatedTotalDamage() }

        table.add(Label("Modified " + CalcAttributeId.DEFENSE.title, createLabelStyle()))
        table.add(selectedHero.getCalculatedTotalDefense().toString() + "%")
        addPossibleAcademyUpgradeToTable { selectedHero.getCalculatedTotalDefense() }
    }

    private fun addPossibleAcademyUpgradeToTable(getHeroCalculatedValue: () -> Int) {
        val trainerSkill: SkillItem? = AcademyUtils.trainerSkill
        val heroSkill: SkillItem? = AcademyUtils.heroSkill
        if (trainerSkill == null ||
            heroSkill == null ||
            heroSkill.rank == heroSkill.maximum ||
            heroSkill.rank >= trainerSkill.rank
        ) {
            table.add("").row()
        } else {
            val current = getHeroCalculatedValue.invoke()
            selectedHero.doTempUpgrade(selectedHero.getSkillById(heroSkill.id))
            val new = getHeroCalculatedValue.invoke()
            selectedHero.doTempDowngrade(selectedHero.getSkillById(heroSkill.id))
            addExtraToTable(new - current)
        }
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(font, Color.BLACK)
    }

}

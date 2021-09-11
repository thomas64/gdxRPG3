package nl.t64.cot.components.party.skills

import java.beans.ConstructorProperties


private const val NUMBER_OF_SKILL_SLOTS = 19

class SkillContainer() {

    private val skills: MutableMap<String, SkillItem> = HashMap(NUMBER_OF_SKILL_SLOTS)

    @ConstructorProperties(
        "alchemist", "diplomat", "healer", "mechanic", "merchant", "ranger",
        "scholar", "stealth", "thief", "troubadour", "warrior", "wizard",
        "hafted", "missile", "pole", "shield", "sword", "thrown",
        "bite")
    constructor(alc: Int, dip: Int, hlr: Int, mec: Int, mer: Int, ran: Int,
                sch: Int, stl: Int, thf: Int, trb: Int, war: Int, wiz: Int,
                haf: Int, mis: Int, pol: Int, shd: Int, swd: Int, thr: Int,
                bit: Int
    ) : this() {
        this.skills[SkillItemId.ALCHEMIST.name] = Alchemist(alc)
        this.skills[SkillItemId.DIPLOMAT.name] = Diplomat(dip)
        this.skills[SkillItemId.HEALER.name] = Healer(hlr)
        this.skills[SkillItemId.MECHANIC.name] = Mechanic(mec)
        this.skills[SkillItemId.MERCHANT.name] = Merchant(mer)
        this.skills[SkillItemId.RANGER.name] = Ranger(ran)
        this.skills[SkillItemId.SCHOLAR.name] = Scholar(sch)
        this.skills[SkillItemId.STEALTH.name] = Stealth(stl)
        this.skills[SkillItemId.THIEF.name] = Thief(thf)
        this.skills[SkillItemId.TROUBADOUR.name] = Troubadour(trb)
        this.skills[SkillItemId.WARRIOR.name] = Warrior(war)
        this.skills[SkillItemId.WIZARD.name] = Wizard(wiz)
        this.skills[SkillItemId.HAFTED.name] = Hafted(haf)
        this.skills[SkillItemId.MISSILE.name] = Missile(mis)
        this.skills[SkillItemId.POLE.name] = Pole(pol)
        this.skills[SkillItemId.SHIELD.name] = Shield(shd)
        this.skills[SkillItemId.SWORD.name] = Sword(swd)
        this.skills[SkillItemId.THROWN.name] = Thrown(thr)
        this.skills[SkillItemId.BITE.name] = Bite(bit)
    }


    fun getById(skillItemId: SkillItemId): SkillItem {
        return skills[skillItemId.name]!!
    }

    fun getAllAboveZero(): List<SkillItem> {
        return SkillItemId.values()
            .map { skills[it.name]!! }
            .filter { hasPositiveQuantity(it) }
    }

    private fun hasPositiveQuantity(skillItem: SkillItem): Boolean {
        return skillItem.rank > 0
    }

}

package nl.t64.cot.components.party

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.skills.SkillDatabase
import nl.t64.cot.components.party.skills.SkillItem


object SkillsRewarder {

    fun receivePossibleSkills(lootId: String) {
        val reward: Loot = gameData.loot.getLoot(lootId)
        if (!reward.isTaken()) {
            receiveSkills(reward)
        }
    }

    private fun receiveSkills(reward: Loot) {
        // todo, it's always Mozes now who gets the skills.
        val mozes: HeroItem = gameData.party.getPlayer()

        val skillsToLearn: List<SkillItem> = reward.content.map { SkillDatabase.createSkillItem(it.key, it.value) }
        skillsToLearn.forEach { mozes.learnSkill(it) }
        showMessageTooltipRewardSkills(skillsToLearn)
        reward.clearContent()
    }

    private fun HeroItem.learnSkill(skillToLearn: SkillItem) {
        val knownSkill = this.getSkillById(skillToLearn.id)
        while (knownSkill.rank < skillToLearn.rank) {
            this.doUpgrade(knownSkill, 0)
        }
    }

    private fun showMessageTooltipRewardSkills(skillItems: List<SkillItem>) {
        stopAllSe()
        playSe(AudioEvent.SE_REWARD)
        val builder = StringBuilder()
        // todo, it only shows which skills and not how much ranks.
        skillItems.forEach { builder.appendLine("+ ${it.name}") }
        builder.deleteAt(builder.lastIndex)
        worldScreen.showMessageTooltip(builder.toString())
    }

}

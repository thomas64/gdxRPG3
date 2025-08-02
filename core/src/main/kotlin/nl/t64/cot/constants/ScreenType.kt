package nl.t64.cot.constants

import com.badlogic.gdx.Screen
import nl.t64.cot.screens.LoadScreen
import nl.t64.cot.screens.academy.AcademyScreen
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.cutscene.*
import nl.t64.cot.screens.help.HelpScreen
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.loot.*
import nl.t64.cot.screens.mechanic.MechanicScreen
import nl.t64.cot.screens.menu.*
import nl.t64.cot.screens.questlog.QuestLogScreen
import nl.t64.cot.screens.school.SchoolScreen
import nl.t64.cot.screens.shop.ShopScreen
import nl.t64.cot.screens.storage.StorageScreen
import nl.t64.cot.screens.warp.WarpScreen
import nl.t64.cot.screens.world.WorldScreen


enum class ScreenType(val screenClass: Class<out Screen>) {

    MENU_MAIN(MenuMain::class.java),
    MENU_NEW(MenuNew::class.java),
    MENU_LOAD_MAIN(MenuLoadMain::class.java),
    MENU_LOAD_PAUSE(MenuLoadPause::class.java),
    MENU_SETTINGS_MAIN(MenuSettingsMain::class.java),
    MENU_SETTINGS_PAUSE(MenuSettingsPause::class.java),
    MENU_CONTROLS_MAIN(MenuControlsMain::class.java),
    MENU_CONTROLS_PAUSE(MenuControlsPause::class.java),
    MENU_CREDITS(MenuCredits::class.java),
    MENU_PAUSE(MenuPause::class.java),
    WORLD(WorldScreen::class.java),
    BATTLE(BattleScreen::class.java),
    LOAD_SCREEN(LoadScreen::class.java),
    WARP(WarpScreen::class.java),
    INVENTORY(InventoryScreen::class.java),
    QUEST_LOG(QuestLogScreen::class.java),
    HELP(HelpScreen::class.java),
    STORAGE(StorageScreen::class.java),
    SHOP(ShopScreen::class.java),
    ACADEMY(AcademyScreen::class.java),
    SCHOOL(SchoolScreen::class.java),
    MECHANIC(MechanicScreen::class.java),
    FIND(FindScreen::class.java),
    REWARD(RewardScreen::class.java),
    RECEIVE(ReceiveScreen::class.java),
    RECEIVE_CUTSCENE(ReceiveCutsceneScreen::class.java),
    TRADE(TradeScreen::class.java),
    SPOILS(SpoilsScreen::class.java),
    SPOILS_CUTSCENE(SpoilsCutsceneScreen::class.java),

    SCENE_INTRO(SceneIntro::class.java),
    SCENE_ARDOR_FIRST_TIME(SceneArdorFirstTime::class.java),
    SCENE_ARDOR_FIRST_TIME_AFTER_WIN_FROM_GENERALS(SceneArdorFirstTimeAfterWinFromGenerals::class.java),
    SCENE_ARDOR_KILLING_GRACE(SceneArdorKillingGrace::class.java),
    SCENE_CYCLE_1_IN_HEAVEN(SceneCycle1InHeaven::class.java),
    SCENE_CYCLE_2_IN_HEAVEN(SceneCycle2InHeaven::class.java),
    SCENE_CYCLE_3_IN_HEAVEN(SceneCycle3InHeaven::class.java),
    SCENE_USE_CRYSTAL_OF_TIME(SceneUseCrystalOfTime::class.java),
    SCENE_CYCLE_4_BEGINS(SceneCycle4Begins::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_1(SceneHoneywoodFarmAttack1::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_2(SceneHoneywoodFarmAttack2::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_3(SceneHoneywoodFarmAttack3::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_AFTER_WIN(SceneHoneywoodFarmAttackAfterWin::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_1_ALT(SceneHoneywoodFarmAttack1Alt::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_2_ALT(SceneHoneywoodFarmAttack2Alt::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_3_ALT(SceneHoneywoodFarmAttack3Alt::class.java),
    SCENE_HONEYWOOD_FARM_ATTACK_AFTER_WIN_ALT(SceneHoneywoodFarmAttackAfterWinAlt::class.java),
    SCENE_HONEYWOOD_FARM_TOO_LATE(SceneHoneywoodFarmTooLate::class.java),
    SCENE_SANTINO_MURDERED(SceneSantinoMurdered::class.java),
    SCENE_ARDOR_LATER_TIME(SceneArdorLaterTime::class.java),
    SCENE_ARDOR_LATER_TIME_AFTER_WIN_FROM_GENERALS(SceneArdorLaterTimeAfterWinFromGenerals::class.java),
    SCENE_GAME_ENDING(SceneGameEnding::class.java);

    fun hasSmallParchment(): Boolean {
        return this in listOf(FIND, REWARD, RECEIVE, RECEIVE_CUTSCENE, TRADE, SPOILS, SPOILS_CUTSCENE)
    }

}

package nl.t64.cot.constants

import com.badlogic.gdx.Screen
import nl.t64.cot.screens.LoadScreen
import nl.t64.cot.screens.academy.AcademyScreen
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.cutscene.*
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.loot.*
import nl.t64.cot.screens.menu.*
import nl.t64.cot.screens.questlog.QuestLogScreen
import nl.t64.cot.screens.school.SchoolScreen
import nl.t64.cot.screens.shop.ShopScreen
import nl.t64.cot.screens.storage.StorageScreen
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
    INVENTORY(InventoryScreen::class.java),
    QUEST_LOG(QuestLogScreen::class.java),
    STORAGE(StorageScreen::class.java),
    SHOP(ShopScreen::class.java),
    ACADEMY(AcademyScreen::class.java),
    SCHOOL(SchoolScreen::class.java),
    FIND(FindScreen::class.java),
    REWARD(RewardScreen::class.java),
    RECEIVE(ReceiveScreen::class.java),
    TRADE(TradeScreen::class.java),
    SPOILS(SpoilsScreen::class.java),
    SPOILS_CUTSCENE(SpoilsCutsceneScreen::class.java),

    SCENE_INTRO(SceneIntro::class.java),
    SCENE_DEATH(SceneDeath::class.java),
    SCENE_ARDOR_1(SceneArdor1::class.java),
    SCENE_ARDOR_1_WIN(SceneArdor1Win::class.java),
    SCENE_ARDOR_1_LOSE(SceneArdor1Lose::class.java),
    SCENE_GAME_ENDING(SceneGameEnding::class.java),
    SCENE_END_OF_CYCLE_2(SceneEndOfCycle2::class.java);

}

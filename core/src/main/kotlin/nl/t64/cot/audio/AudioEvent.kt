package nl.t64.cot.audio

import com.badlogic.gdx.math.MathUtils


enum class AudioEvent(val filePath: String, val volume: Float = 1f) {

    BGM_TITLE("audio/bgm/brave.mp3"),
    BGM_FOREST("audio/bgm/journey.ogg"),
    BGM_TOWN("audio/bgm/town.ogg"),
    BGM_INN("audio/bgm/store.ogg"),
    BGM_HOUSE("audio/bgm/wake_up.ogg"),
    BGM_CELLAR("audio/bgm/quiet_town.ogg"),
    BGM_SECRET_CHAMBER("audio/bgm/woods.ogg"),
    BGM_MYSTERIOUS_TUNNEL("audio/bgm/cave.ogg"),
    BGM_TENSION("audio/bgm/volcano.ogg"),
    BGM_CAVE("audio/bgm/den.ogg"),
    BGM_GREAT_TREE("audio/bgm/wayfarer.mp3"),
    BGM_ARDOR("audio/bgm/remains.ogg"),

    BGS_BIRDS("audio/bgs/birds.ogg"),
    BGS_CREEK("audio/bgs/creek.ogg"),
    BGS_FIREPLACE("audio/bgs/fireplace.ogg"),
    BGS_SPRING_FLOW("audio/bgs/spring_flow.ogg"),
    BGS_CROWS("audio/bgs/crows.ogg"),
    BGS_RIVER("audio/bgs/mv_river.ogg"),
    BGS_QUAKE("audio/bgs/mv_quake.ogg"),

    SE_MENU_CURSOR("audio/se/botw_menu_cursor.wav"),
    SE_MENU_CONFIRM("audio/se/botw_menu_confirm.wav"),
    SE_MENU_BACK("audio/se/botw_menu_back.wav"),
    SE_MENU_ERROR("audio/se/botw_menu_error.wav"),
    SE_MENU_TYPING("audio/se/botw_menu_typing.wav"),

    SE_CONVERSATION_START("audio/se/botw_conversation_start.ogg", 0.8f),
    SE_CONVERSATION_NEXT("audio/se/botw_conversation_next.wav", 0.4f),
    SE_CONVERSATION_END("audio/se/botw_conversation_end.wav", 0.8f),
    SE_CONVERSATION_CURSOR("audio/se/botw_conversation_cursor.wav", 0.2f),
    SE_MINIMAP("audio/se/botw_minimap.wav"),

    SE_REWARD("audio/se/virix_reward.wav", 0.4f),
    SE_QUEST_FAIL("audio/se/virix_quest_fail.wav", 0.5f),
    SE_JOIN("audio/se/virix_join.wav", 0.6f),
    SE_SAVE_GAME("audio/se/virix_save.wav", 0.3f),

    SE_CHEST("audio/se/kenney_door_open2.ogg", 0.1f),
    SE_EQUIP("audio/se/kenney_cloth3.ogg"),
    SE_TAKE("audio/se/kenney_handle_small_leather2.ogg"),
    SE_COINS_BUY("audio/se/kenney_handle_coins.ogg", 0.5f),
    SE_COINS_SELL("audio/se/kenney_handle_coins2.ogg", 0.5f),

    SE_WARP("audio/se/whooshes_impacts_impact_01.ogg", 0.4f),
    SE_BANG("audio/se/whooshes_impacts2_whooshes_032.ogg", 0.4f),
    SE_LEVELUP("audio/se/copyc4t_levelup.ogg", 0.3f),
    SE_DEATH_SCREAM("audio/se/death_7_ian.ogg"),

    SE_FIGHT_ON("audio/se/fight_on.ogg", 0.2f),
    SE_RESTORE("audio/se/healing.ogg", 0.2f),
    SE_UPGRADE("audio/se/upgrade.ogg", 0.5f),

    SE_SCROLL("audio/se/mv_book2.ogg", 0.2f),
    SE_SPARKLE("audio/se/mv_key.ogg", 0.2f),
    SE_SMALL_DOOR("audio/se/mv_open1.ogg", 0.2f),
    SE_METAL_GATE("audio/se/mv_open3.ogg", 0.2f),
    SE_LARGE_DOOR("audio/se/mv_open5.ogg", 0.2f),
    SE_WOODEN_GATE("audio/se/mv_earth4.ogg", 0.2f),

    SE_STEP_CARPET1("audio/se/footsteps/oot_step_carpet1.ogg"),
    SE_STEP_CARPET2("audio/se/footsteps/oot_step_carpet2.ogg"),
    SE_STEP_CARPET3("audio/se/footsteps/oot_step_carpet3.ogg"),
    SE_STEP_CARPET4("audio/se/footsteps/oot_step_carpet4.ogg"),
    SE_STEP_GRASS1("audio/se/footsteps/oot_step_grass1.ogg"),
    SE_STEP_GRASS2("audio/se/footsteps/oot_step_grass2.ogg"),
    SE_STEP_GRASS3("audio/se/footsteps/oot_step_grass3.ogg"),
    SE_STEP_GRASS4("audio/se/footsteps/oot_step_grass4.ogg"),
    SE_STEP_SAND1("audio/se/footsteps/oot_step_sand1.ogg"),
    SE_STEP_SAND2("audio/se/footsteps/oot_step_sand2.ogg"),
    SE_STEP_SAND3("audio/se/footsteps/oot_step_sand3.ogg"),
    SE_STEP_SAND4("audio/se/footsteps/oot_step_sand4.ogg"),
    SE_STEP_STONE1("audio/se/footsteps/oot_step_stone1.ogg"),
    SE_STEP_STONE2("audio/se/footsteps/oot_step_stone2.ogg"),
    SE_STEP_STONE3("audio/se/footsteps/oot_step_stone3.ogg"),
    SE_STEP_STONE4("audio/se/footsteps/oot_step_stone4.ogg"),
    SE_STEP_WOOD1("audio/se/footsteps/oot_step_wood1.ogg"),
    SE_STEP_WOOD2("audio/se/footsteps/oot_step_wood2.ogg"),
    SE_STEP_WOOD3("audio/se/footsteps/oot_step_wood3.ogg"),
    SE_STEP_WOOD4("audio/se/footsteps/oot_step_wood4.ogg"),

    NONE("");

}

fun String.toAudioEvent(): AudioEvent {
    return AudioEvent.valueOf("SE_STEP_" + this.uppercase() + MathUtils.random(1, 4))
}

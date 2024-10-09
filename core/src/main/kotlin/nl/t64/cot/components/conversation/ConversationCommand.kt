package nl.t64.cot.components.conversation


enum class ConversationCommand {
    EXIT,
    HERO_JOIN,
    HERO_DISMISS,
    LOAD_SHOP,
    LOAD_ACADEMY,
    LOAD_SCHOOL,
    AUTO_SAVE,
    SAVE_GAME,
    HEAL_LIFE_01,
    HEAL_LIFE_09,
    HEAL_LIFE_12,
    HEAL_LIFE_15,
    HEAL_LIFE_18,
    RECEIVE_XP,
    RECEIVE_SPELLS,
    RECEIVE_ITEM,
    START_BATTLE,
    RELOAD_NPCS,
    FADE_TO_BLACK_17,

    KNOW_QUEST,
    ACCEPT_QUEST,
    TRADE_QUEST_ITEMS,
    SHOW_QUEST_ITEM,
    WEAR_QUEST_ITEM,
    PROVIDE_QUEST_ITEM,
    SAY_QUEST_THING,
    RECEIVE_ITEM_FOR_QUEST,
    DELIVER_QUEST_ITEM,
    DELIVER_QUEST_ITEM_ALTERNATE,
    DELIVER_QUEST_MESSAGE,

    REWARD_QUEST,

    NONE,


    BONUS_REWARD_QUEST,
    FAIL_QUEST,

}

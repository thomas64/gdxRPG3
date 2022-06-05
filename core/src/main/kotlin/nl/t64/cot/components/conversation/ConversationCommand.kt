package nl.t64.cot.components.conversation


enum class ConversationCommand {
    EXIT,
    HERO_JOIN,
    HERO_DISMISS,
    LOAD_SHOP,
    LOAD_ACADEMY,
    LOAD_SCHOOL,
    SAVE_GAME,
    HEAL_LIFE,
    RECEIVE_XP,
    RECEIVE_SPELLS,
    START_BATTLE,
    RELOAD_NPCS,

    KNOW_QUEST,
    ACCEPT_QUEST,
    TRADE_QUEST_ITEMS,
    RE_TRADE_QUEST_ITEMS,
    SHOW_QUEST_ITEM,
    WEAR_QUEST_ITEM,
    GIVE_QUEST_ITEM,
    SAY_QUEST_THING,

    REWARD_QUEST,

    NONE,


    TOLERATE_QUEST,
    RECEIVE_ITEM,
    CHECK_IF_LINKED_QUEST_KNOWN,
    CHECK_IF_QUEST_ACCEPTED,
    CHECK_IF_IN_INVENTORY,
    COMPLETE_QUEST_TASK,
    RETURN_QUEST,
    ACCEPT_OR_RETURN_QUEST,
    BONUS_REWARD_QUEST,
    FAIL_QUEST,

}

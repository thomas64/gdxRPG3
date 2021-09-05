package nl.t64.cot.components.conversation


enum class ConversationCommand {
    EXIT_CONVERSATION,
    HERO_JOIN,
    HERO_DISMISS,
    LOAD_SHOP,
    SAVE_GAME,
    HEAL_LIFE,
    RECEIVE_XP,
    START_BATTLE,

    ACCEPT_QUEST,
    SHOW_QUEST_ITEM,
    WEAR_QUEST_ITEM,
    SAY_QUEST_THING,

    NONE,


    KNOW_QUEST,
    TOLERATE_QUEST,
    RECEIVE_ITEM,
    CHECK_IF_LINKED_QUEST_KNOWN,
    CHECK_IF_QUEST_ACCEPTED,
    CHECK_IF_IN_INVENTORY,
    COMPLETE_QUEST_TASK,
    RETURN_QUEST,
    ACCEPT_OR_RETURN_QUEST,
    REWARD_QUEST,
    BONUS_REWARD_QUEST,
    FAIL_QUEST,

}

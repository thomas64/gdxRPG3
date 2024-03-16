package nl.t64.cot.components.quest

enum class QuestTaskType {
    DISCOVER,
    CHECK,
    CHECK_WITH_ITEM, // a check quest in which you'll receive items which are indicated in 'target' (the same with DELIVER_ITEM).
    FIND_ITEM,
    SHOW_ITEM,
    WEAR_ITEM,
    PROVIDE_ITEM,
    TRADE_ITEMS,
    DELIVER_ITEM,
    DELIVER_MESSAGE,
    SAY_THE_RIGHT_THING,
    KILL,

    RETURN,

    FREE,
    NONE,


    FETCH_ITEM,
    GAIN_KNOWLEDGE,
    MESSAGE_DELIVERY,   // legacy
    ITEM_DELIVERY,      // legacy
    GUARD,
    ESCORT,

}

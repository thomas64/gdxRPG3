package nl.t64.cot.components.portal


enum class Portal(
    val title: String,
    val isActivatedAtStart: Boolean
) {

    HONEYWOOD_HOUSE_MOZES("Mozes' House", true),
    HONEYWOOD_GREAT_TREE("Honeywood Forest", false),
    HONEYWOOD_HOUSE_ELDER_B2("Honeywood Elder", false),
    LASTDENN("Lastdenn", false);

    override fun toString(): String {
        return title
    }

}

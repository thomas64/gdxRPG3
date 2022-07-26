package nl.t64.cot.components.portal


enum class Portal(val title: String) {

    HONEYWOOD_GREAT_TREE("Honeywood Forest"),
    HONEYWOOD_HOUSE_ELDER_B2("Honeywood Village"),
    LASTDENN("Lastdenn");

    override fun toString(): String {
        return title
    }

}

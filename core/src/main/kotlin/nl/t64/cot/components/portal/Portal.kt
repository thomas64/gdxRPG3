package nl.t64.cot.components.portal


enum class Portal(val title: String) {

    HONEYWOOD_HOUSE_MOZES("Mozes' House"),
    HONEYWOOD_GREAT_TREE("Honeywood Forest Fairies"),
    HONEYWOOD_HOUSE_ELDER_B2("Honeywood Village Elder"),
    LASTDENN("Lastdenn");

    override fun toString(): String {
        return title
    }

}

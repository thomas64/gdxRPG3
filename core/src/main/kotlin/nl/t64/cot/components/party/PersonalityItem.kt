package nl.t64.cot.components.party


abstract class PersonalityItem {

    abstract val id: SuperEnum
    abstract val name: String

    abstract fun getTotalDescription(): String

}

package nl.t64.cot.screens.world.entity


abstract class InputComponent : Component {

    lateinit var direction: Direction

    abstract fun update(entity: Entity, dt: Float)

    override fun dispose() {
        // empty
    }

    open fun reset() {
        // empty
    }

}

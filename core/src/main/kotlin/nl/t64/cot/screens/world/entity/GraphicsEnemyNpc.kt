package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import nl.t64.cot.components.battle.ThreatLevel
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent


class GraphicsEnemyNpc(spriteId: String) : GraphicsNpc(spriteId) {

    private lateinit var threatMarker: ThreatMarker

    override fun receive(event: Event) {
        super.receive(event)
        if (event is LoadEntityEvent) {
            threatMarker = ThreatMarker(ThreatLevel.forBattle(event.conversationOrBattleId!!))
        }
    }

    override fun update(dt: Float) {
        super.update(dt)
        threatMarker.update(dt)
    }

    override fun render(batch: Batch) {
        super.render(batch)
        if (state != EntityState.INVISIBLE) {
            threatMarker.render(batch, position)
        }
    }

}

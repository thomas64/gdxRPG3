package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch


class GraphicsRecruitableHero(spriteId: String) : GraphicsNpc(spriteId) {

    private val recruitMarker = RecruitMarker()

    override fun update(dt: Float) {
        super.update(dt)
        recruitMarker.update(dt)
    }

    override fun render(batch: Batch) {
        super.render(batch)
        if (state != EntityState.INVISIBLE) {
            recruitMarker.render(batch, position)
        }
    }

}

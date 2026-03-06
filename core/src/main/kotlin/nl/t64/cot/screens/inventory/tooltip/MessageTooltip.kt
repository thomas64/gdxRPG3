package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.FontProvider


private const val PAD = 50f
private const val DELAY = 1f
private const val SHOW_DURATION = 5f

class MessageTooltip : BaseTooltip() {

    private val label: Label
    private var isPersistent = false
    private var isPersistentRequested = false
    private var persistentRequestId = 0 // Only the latest persistent show request is allowed to materialize.

    init {
        super.window.padLeft(PAD)
        super.window.padRight(PAD)
        val labelStyle = LabelStyle(FontProvider.calibriLight28, Color.WHITE)
        label = Label(null, labelStyle)
        label.setAlignment(Align.center)
        super.window.add(label)
    }

    fun show(message: String, stage: Stage) {
        if (!label.textEquals(message)) {
            stage.addActor(window)
            showWindow(message)
        }
    }

    fun showPersistent(message: String, stage: Stage) {
        isPersistentRequested = true
        // Capture the current request version for this queued action.
        val requestId = ++persistentRequestId
        stage.addActor(window)
        enqueuePersistentShow(message, requestId)
    }

    fun hidePersistent() {
        // Invalidate queued persistent shows that have not started yet.
        isPersistentRequested = false
        persistentRequestId++
        if (isPersistent) {
            hidePersistentWindow()
        }
    }

    private fun enqueuePersistentShow(message: String, requestId: Int) {
        window.addAction(Actions.after(Actions.run {
            // Skip stale queued actions after a newer show/hide invalidated this request.
            if (isPersistentRequested && requestId == persistentRequestId) {
                showPersistentWindow(message)
            }
        }))
    }

    private fun showWindow(message: String) {
        window.addAction(Actions.after(Actions.sequence(Actions.run { isPersistent = false },
                                                        Actions.run { setupWindow(message) },
                                                        Actions.alpha(0f),
                                                        Actions.visible(true),
                                                        Actions.delay(DELAY),
                                                        Actions.fadeIn(Constant.FADE_DURATION),
                                                        Actions.delay(SHOW_DURATION),
                                                        Actions.fadeOut(Constant.FADE_DURATION),
                                                        Actions.visible(false),
                                                        Actions.run { label.setText("") })))
    }

    private fun showPersistentWindow(message: String) {
        window.addAction(Actions.sequence(Actions.run { isPersistent = true },
                                          Actions.run { setupWindow(message) },
                                          Actions.alpha(0f),
                                          Actions.visible(true),
                                          Actions.fadeIn(Constant.FADE_DURATION)))
    }

    private fun hidePersistentWindow() {
        window.addAction(Actions.after(Actions.sequence(Actions.fadeOut(Constant.FADE_DURATION),
                                                        Actions.visible(false),
                                                        Actions.run { isPersistent = false },
                                                        Actions.run { label.setText("") })))
    }

    private fun setupWindow(message: String) {
        label.setText(message)
        window.pack()
        window.setPosition(Gdx.graphics.width - window.width - PAD,
                           Gdx.graphics.height - window.height - PAD)
    }

}

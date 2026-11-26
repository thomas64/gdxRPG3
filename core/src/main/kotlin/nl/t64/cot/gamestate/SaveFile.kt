package nl.t64.cot.gamestate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


private const val PROFILE_ID_KEY = "id"
private const val SAVE_DATE_KEY = "saveDate"
private const val SAVE_TIME_KEY = "saveTime"
private const val SAVE_STATE_KEY = "saveState"
private const val FOG_OF_WAR_KEY = "fogOfWar"
private const val DATE_PATTERN = "yyyy-MM-dd HH:mm"

class SaveFile(
    saveFileName: String,
    fogOfWarFileName: String,
    val isAutosave: Boolean
) {

    private val saveFileContent: Preferences = Gdx.app.getPreferences(saveFileName)
    private val fogOfWarFileContent: Preferences = Gdx.app.getPreferences(fogOfWarFileName)

    val profileId: String get() = saveFileContent.getString(PROFILE_ID_KEY)
    val saveDate: String get() = saveFileContent.getString(SAVE_DATE_KEY)
    val saveStateJson: String get() = saveFileContent.getString(SAVE_STATE_KEY)
    val fogOfWarJson: String get() = fogOfWarFileContent.getString(FOG_OF_WAR_KEY)

    // the save date only has minutes, which is too coarse to tell two quick autosaves apart.
    // a file without this key gets 0 and therefore counts as the oldest.
    val saveTime: Long get() = saveFileContent.getLong(SAVE_TIME_KEY)

    fun exists(): Boolean {
        return saveFileContent.contains(SAVE_STATE_KEY)
    }

    fun write(profileId: String, saveStateJson: String, fogOfWarJson: String) {
        val now: LocalDateTime = LocalDateTime.now()
        saveFileContent.putString(PROFILE_ID_KEY, profileId)
        saveFileContent.putString(SAVE_DATE_KEY, DateTimeFormatter.ofPattern(DATE_PATTERN).format(now))
        saveFileContent.putLong(SAVE_TIME_KEY, now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        saveFileContent.putString(SAVE_STATE_KEY, saveStateJson)
        fogOfWarFileContent.putString(FOG_OF_WAR_KEY, fogOfWarJson)
        saveFileContent.flush()
        fogOfWarFileContent.flush()
    }

    fun remove() {
        saveFileContent.clear()
        saveFileContent.flush()
        fogOfWarFileContent.clear()
        fogOfWarFileContent.flush()
    }

}

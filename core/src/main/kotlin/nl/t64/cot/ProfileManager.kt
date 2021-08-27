package nl.t64.cot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set
import ktx.json.fromJson
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.constants.Constant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val SAVE_FILES = "save1.dat,save2.dat,save3.dat,save4.dat,save5.dat,save6.dat,save7.dat"
private const val LOADING = ",,Loading...,,,,"
private const val SAVE_STATE_KEY = "saveState"
private const val PROFILE_ID = "id"
private const val PROFILE_SAVE_DATE = "saveDate"
private const val DATE_PATTERN = "yyyy-MM-dd HH:mm"
private const val DEFAULT_EMPTY_PROFILE_VIEW = " [...]"

class ProfileManager {

    private val json = Json()
    private var saveStateProperties = GdxMap<String, Any>()
    private lateinit var currentSaveFileName: String
    var selectedIndex = -1

    fun doesProfileExist(profileIndex: Int): Boolean {
        val saveFile = getSaveFileBy(profileIndex)
        return saveFile.contains(SAVE_STATE_KEY)
    }

    fun createNewProfile(profileId: String) {
        currentSaveFileName = getSaveFileNames()[selectedIndex]
        saveStateProperties.clear()
        brokerManager.profileObservers.notifyCreateProfile(this)
        writeProfileToDisk(profileId.ifEmpty { getDefaultId() })
    }

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified T> getProperty(key: String): T {
        return saveStateProperties[key] as T
    }

    fun setProperty(key: String, any: Any) {
        saveStateProperties[key] = any
    }

    fun saveProfile() {
        brokerManager.profileObservers.notifySaveProfile(this)
        writeProfileToDisk(null)
    }

    fun loadProfile(profileIndex: Int) {
        currentSaveFileName = getSaveFileNames()[profileIndex]
        val saveFile = Gdx.app.getPreferences(currentSaveFileName)
        saveStateProperties = getSaveStateProperties(saveFile)
        brokerManager.profileObservers.notifyLoadProfile(this)
    }

    fun removeProfile(profileIndex: Int) {
        val saveFile = getSaveFileBy(profileIndex)
        saveFile.clear()
        saveFile.flush()
    }

    fun getVisualLoadingArray(): GdxArray<String> =
        GdxArray(LOADING.split(",").toTypedArray())

    fun getVisualProfileArray(): GdxArray<String> =
        GdxArray(getSaveFileNames().indices.map { getVisualOf(it) }.toTypedArray())

    fun getLastSaveLocation(): String {
        return saveStateProperties["mapTitle"] as String
    }

    private fun writeProfileToDisk(profileId: String?) {
        val saveDate: String = DateTimeFormatter.ofPattern(DATE_PATTERN).format(LocalDateTime.now())
        val saveStateJsonString = json.prettyPrint(json.toJson(saveStateProperties))
        val saveFile = Gdx.app.getPreferences(currentSaveFileName)
        if (profileId != null) saveFile.putString(PROFILE_ID, profileId)
        saveFile.putString(PROFILE_SAVE_DATE, saveDate)
        saveFile.putString(SAVE_STATE_KEY, saveStateJsonString)
        saveFile.flush()
    }

    private fun getVisualOf(profileIndex: Int): String {
        val saveFile = getSaveFileBy(profileIndex)
        return if (saveFile.contains(SAVE_STATE_KEY)) {
            "${saveFile.getString(PROFILE_ID)} [${saveFile.getString(PROFILE_SAVE_DATE)}]"
        } else {
            "${profileIndex + 1} $DEFAULT_EMPTY_PROFILE_VIEW"
        }
    }

    private fun getSaveFileBy(profileIndex: Int): Preferences {
        val saveFileName = getSaveFileNames()[profileIndex]
        return Gdx.app.getPreferences(saveFileName)
    }

    private fun getSaveStateProperties(saveFile: Preferences): GdxMap<String, Any> {
        val saveStateJsonString = saveFile.getString(SAVE_STATE_KEY)
        return json.fromJson(saveStateJsonString)
    }

    private fun getDefaultId(): String {
        return Constant.PLAYER_ID.replaceFirstChar { it.uppercase() }
    }

    private fun getSaveFileNames(): List<String> = SAVE_FILES.split(",")

}

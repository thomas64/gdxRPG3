package nl.t64.cot.gamestate

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.SerializationException
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set
import ktx.json.fromJson
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.map.FogOfWar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val SAVE_FILES = "save1.dat,save2.dat,save3.dat,save4.dat,save5.dat,autosave.dat"
private const val FOW_FILES = "fow1.dat,fow2.dat,fow3.dat,fow4.dat,fow5.dat,autofow.dat"
private const val LOADING = ",,Loading...,,,"
private const val PROFILE_ID_KEY = "id"
private const val PROFILE_INDEX_KEY = "index"
private const val PROFILE_SAVE_DATE_KEY = "saveDate"
private const val PROFILE_SAVE_STATE_KEY = "saveState"
private const val PROFILE_FOG_OF_WAR_KEY = "fogOfWar"
private const val DATE_PATTERN = "yyyy-MM-dd HH:mm"
const val AUTOSAVE_INDEX = 5
const val DEFAULT_EMPTY_PROFILE_VIEW = " [...]"
const val INVALID_PROFILE_VIEW = " [Invalid]"

class ProfileManager {

    private val json = Json()
    private var saveState = GdxMap<String, Any>()
    private var currentProfileId: String = ""
    private var currentIndex = -1

    fun doesProfileExist(profileIndex: Int): Boolean {
        val (saveFileContent: Preferences, _) = getSaveFilesContentBy(profileIndex)
        return saveFileContent.contains(PROFILE_SAVE_STATE_KEY)
    }

    fun selectNewProfile(profileIndex: Int) {
        currentIndex = profileIndex
    }

    fun createNewProfile(profileId: String) {
        currentProfileId = profileId.ifEmpty { getDefaultId() }
        saveState.clear()
        brokerManager.profileObservers.notifyCreateProfile(this)
        writeProfileToDisk(currentIndex)
    }

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified T> getProperty(key: String): T {
        return saveState[key] as T
    }

    fun setProperty(key: String, any: Any) {
        saveState[key] = any
    }

    fun autoSave() {
        brokerManager.profileObservers.notifySaveProfile(this)
        writeProfileToDisk(AUTOSAVE_INDEX)
        worldScreen.showMessageTooltip("Game saved.")
    }

    fun saveProfile() {
        removeProfile(AUTOSAVE_INDEX)
        brokerManager.profileObservers.notifySaveProfile(this)
        writeProfileToDisk(currentIndex)
        worldScreen.showMessageTooltip("Game saved.")
    }

    fun loadProfileFromMain(profileIndex: Int) {
        val (saveFileContent: Preferences, fogOfWarFileContent: Preferences) = getSaveFilesContentBy(profileIndex)
        currentProfileId = saveFileContent.getString(PROFILE_ID_KEY)
        currentIndex = saveFileContent.getInteger(PROFILE_INDEX_KEY)
        loadProfile(saveFileContent, fogOfWarFileContent)
    }

    fun loadProfileFromPause(profileIndex: Int) {
        val modIndex: Int = if (profileIndex == 0) currentIndex else AUTOSAVE_INDEX
        val (saveFileContent: Preferences, fogOfWarFileContent: Preferences) = getSaveFilesContentBy(modIndex)
        loadProfile(saveFileContent, fogOfWarFileContent)
    }

    private fun loadProfile(saveFileContent: Preferences, fogOfWarFileContent: Preferences) {
        saveState = createSaveStateFrom(saveFileContent, fogOfWarFileContent)
        brokerManager.profileObservers.notifyLoadProfile(this)
    }

    fun removeProfile(profileIndex: Int) {
        val (saveFileContent: Preferences, fogOfWarContent: Preferences) = getSaveFilesContentBy(profileIndex)
        saveFileContent.clear()
        saveFileContent.flush()
        fogOfWarContent.clear()
        fogOfWarContent.flush()
    }

    fun getVisualLoadingArray(): GdxArray<String> {
        return GdxArray(LOADING.split(",").toTypedArray())
    }

    fun getVisualLoadingArrayForLoadedProfile(): GdxArray<String> {
        return GdxArray(arrayOf("Loading...", ""))
    }

    fun getVisualProfileArray(): GdxArray<String> {
        return GdxArray(getSaveFileNames().indices.map { getVisualOf(it) }.toTypedArray())
    }

    fun getVisualProfileArrayForLoadedProfile(): GdxArray<String> {
        return GdxArray(getSaveFileNames().indices
                            .map { index -> index to getSaveFilesContentBy(index) }
                            // 0 is the default default value, which can actually be a valid value. So -9 is a never valid value.
                            .filter { (_, saveFileContent) -> saveFileContent.first.getInteger(PROFILE_INDEX_KEY, -9) == currentIndex }
                            .map { (index, saveFileContent) -> getVisualOf(index, saveFileContent.first) }
                            .toTypedArray())
    }

    fun getLastSaveLocation(): String {
        return saveState["mapTitle"] as String
    }

    private fun writeProfileToDisk(profileIndex: Int) {
        val fogOfWar = saveState["fogOfWar"]
        saveState.remove("fogOfWar")

        val (saveFileContent: Preferences, fogOfWarFileContent: Preferences) = getSaveFilesContentBy(profileIndex)

        saveFileContent.putString(PROFILE_ID_KEY, currentProfileId)
        saveFileContent.putInteger(PROFILE_INDEX_KEY, currentIndex)
        saveFileContent.putString(PROFILE_SAVE_DATE_KEY,
                                  DateTimeFormatter.ofPattern(DATE_PATTERN).format(LocalDateTime.now()))
        saveFileContent.putString(PROFILE_SAVE_STATE_KEY,
                                  json.prettyPrint(json.toJson(saveState)))
        fogOfWarFileContent.putString(PROFILE_FOG_OF_WAR_KEY,
                                      json.prettyPrint(json.toJson(fogOfWar)))

        saveFileContent.flush()
        fogOfWarFileContent.flush()

        saveState["fogOfWar"] = fogOfWar
    }

    private fun getVisualOf(profileIndex: Int): String {
        val (saveFileContent: Preferences, _) = getSaveFilesContentBy(profileIndex)
        return getVisualOf(profileIndex, saveFileContent)
    }

    private fun getVisualOf(profileIndex: Int, saveFileContent: Preferences): String {
        if (saveFileContent.contains(PROFILE_SAVE_STATE_KEY)) {
            try {
                if (!preferenceManager.isInDebugMode) {
                    createSaveStateFrom(saveFileContent, null)
                }
                return getFilledProfileView(profileIndex, saveFileContent)
            } catch (e: SerializationException) {
                return getInvalidProfileView(profileIndex)
            }
        } else {
            return getEmptyProfileView(profileIndex)
        }
    }

    private fun getFilledProfileView(profileIndex: Int, saveFileContent: Preferences): String {
        val saveDate: String = saveFileContent.getString(PROFILE_SAVE_DATE_KEY)
        if (profileIndex == AUTOSAVE_INDEX) {
            return "Autosave [$saveDate]"
        } else {
            val profileId: String = saveFileContent.getString(PROFILE_ID_KEY)
            return "$profileId [$saveDate]"
        }
    }

    private fun getInvalidProfileView(profileIndex: Int): String {
        if (profileIndex == AUTOSAVE_INDEX) {
            return "Autosave $INVALID_PROFILE_VIEW"
        } else {
            return "${profileIndex + 1} $INVALID_PROFILE_VIEW"
        }
    }

    private fun getEmptyProfileView(profileIndex: Int): String {
        if (profileIndex == AUTOSAVE_INDEX) {
            return "Autosave $DEFAULT_EMPTY_PROFILE_VIEW"
        } else {
            return "${profileIndex + 1} $DEFAULT_EMPTY_PROFILE_VIEW"
        }
    }

    private fun getSaveFilesContentBy(profileIndex: Int): Pair<Preferences, Preferences> {
        val saveFileName: String = getSaveFileNames()[profileIndex]
        val fogOfWarFileName: String = getFogOfWarFileNames()[profileIndex]
        return Gdx.app.getPreferences(saveFileName) to Gdx.app.getPreferences(fogOfWarFileName)
    }

    private fun createSaveStateFrom(
        saveFileContent: Preferences,
        fogOfWarFileContent: Preferences?
    ): GdxMap<String, Any> {
        val saveStateJsonString = saveFileContent.getString(PROFILE_SAVE_STATE_KEY)
        val loadedSaveState: GdxMap<String, Any> = json.fromJson(saveStateJsonString)
        fogOfWarFileContent
            ?.getString(PROFILE_FOG_OF_WAR_KEY)
            ?.let { loadedSaveState["fogOfWar"] = json.fromJson<FogOfWar>(it) }
        return loadedSaveState
    }

    private fun getDefaultId(): String {
        return Constant.PLAYER_ID.replaceFirstChar { it.uppercase() }
    }

    private fun getSaveFileNames(): List<String> = SAVE_FILES.split(",")
    private fun getFogOfWarFileNames(): List<String> = FOW_FILES.split(",")

}

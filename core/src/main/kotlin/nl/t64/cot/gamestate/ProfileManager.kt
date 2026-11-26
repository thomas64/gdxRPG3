package nl.t64.cot.gamestate

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.SerializationException
import ktx.collections.*
import ktx.json.fromJson
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.map.FogOfWar


private const val NUMBER_OF_PROFILES = 7
private const val AUTOSAVE_SUFFIXES = "a,b,c"
private const val LOADING_PROFILES = ",,,Loading...,,,"
private const val LOADING_SAVES = "Loading..."
private const val AUTOSAVE_NAME = "Autosave"
const val DEFAULT_EMPTY_PROFILE_VIEW = " [...]"
const val INVALID_PROFILE_VIEW = " [Invalid]"

class ProfileManager {

    // an unknown field is a field that was removed from the game after the save file was written.
    private val json = Json().apply { ignoreUnknownFields = true }
    private var saveState = GdxMap<String, Any?>()
    private var currentProfileId: String = ""
    private var selectedProfileIndex = -1

    fun doesProfileExist(profileIndex: Int): Boolean {
        return getSaveFilesOf(profileIndex).any { it.exists() }
    }

    fun selectProfile(profileIndex: Int) {
        selectedProfileIndex = profileIndex
    }

    fun getSaveFilesOfSelectedProfile(): List<SaveFile> {
        return getExistingSaveFilesNewestFirstOf(selectedProfileIndex)
    }

    fun createNewProfile(profileId: String) {
        currentProfileId = profileId.ifEmpty { getDefaultId() }
        saveState.clear()
        brokerManager.profileObservers.notifyCreateProfile(this)
        writeProfileTo(getManualSaveFileOf(selectedProfileIndex))
    }

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline fun <reified T> getProperty(key: String): T {
        return saveState[key] as T
    }

    fun setProperty(key: String, any: Any?) {
        saveState[key] = any
    }

    fun autoSaveAfterBattle(playerPosition: Vector2) {
        brokerManager.profileObservers.notifySaveProfileAfterBattle(this, playerPosition)
        writeProfileTo(getOldestAutosaveFileOf(selectedProfileIndex))
        worldScreen.showMessageTooltip("Game saved.")
    }

    fun autoSave() {
        brokerManager.profileObservers.notifySaveProfile(this)
        writeProfileTo(getOldestAutosaveFileOf(selectedProfileIndex))
        worldScreen.showMessageTooltip("Game saved.")
    }

    fun saveProfile() {
        getAutosaveFilesOf(selectedProfileIndex).forEach { it.remove() }
        brokerManager.profileObservers.notifySaveProfile(this)
        writeProfileTo(getManualSaveFileOf(selectedProfileIndex))
        worldScreen.showMessageTooltip("Game saved.")
    }

    fun loadProfile(saveFile: SaveFile) {
        currentProfileId = saveFile.profileId
        saveState = createSaveStateFrom(saveFile)
        saveState["fogOfWar"] = json.fromJson<FogOfWar>(saveFile.fogOfWarJson)
        brokerManager.profileObservers.notifyLoadProfile(this)
    }

    fun removeProfile(profileIndex: Int) {
        getSaveFilesOf(profileIndex).forEach { it.remove() }
    }

    fun getVisualLoadingArrayForProfiles(): GdxArray<String> {
        return LOADING_PROFILES.split(",").toGdxArray()
    }

    fun getVisualLoadingArrayForSaves(): GdxArray<String> {
        return gdxArrayOf(LOADING_SAVES)
    }

    fun getVisualProfileArray(): GdxArray<String> {
        return (0 until NUMBER_OF_PROFILES).map { getVisualOfProfile(it) }.toGdxArray()
    }

    fun getVisualSaveArray(saveFiles: List<SaveFile>): GdxArray<String> {
        return saveFiles.map { getVisualOf(it, getNameOf(it)) }.toGdxArray()
    }

    fun getLastSaveLocation(): String {
        val lastSaveState: GdxMap<String, Any?> = createSaveStateFrom(getManualSaveFileOf(selectedProfileIndex))
        return lastSaveState["mapTitle"] as String
    }

    private fun getExistingSaveFilesNewestFirstOf(profileIndex: Int): List<SaveFile> {
        return getSaveFilesOf(profileIndex)
            .filter { it.exists() }
            .sortedByDescending { it.saveTime }
    }

    private fun getOldestAutosaveFileOf(profileIndex: Int): SaveFile {
        // an empty autosave file has saveTime 0, so it is always picked before an existing one.
        return getAutosaveFilesOf(profileIndex).minBy { it.saveTime }
    }

    private fun getSaveFilesOf(profileIndex: Int): List<SaveFile> {
        return listOf(getManualSaveFileOf(profileIndex)) + getAutosaveFilesOf(profileIndex)
    }

    private fun getManualSaveFileOf(profileIndex: Int): SaveFile {
        val profileNumber: Int = profileIndex + 1
        return SaveFile("save$profileNumber.dat", "fow$profileNumber.dat", isAutosave = false)
    }

    private fun getAutosaveFilesOf(profileIndex: Int): List<SaveFile> {
        val profileNumber: Int = profileIndex + 1
        return AUTOSAVE_SUFFIXES.split(",").map {
            SaveFile("autosave$profileNumber$it.dat", "autofow$profileNumber$it.dat", isAutosave = true)
        }
    }

    private fun writeProfileTo(saveFile: SaveFile) {
        val fogOfWar: Any? = saveState.remove("fogOfWar")
        saveFile.write(currentProfileId,
                       json.prettyPrint(json.toJson(saveState)),
                       json.prettyPrint(json.toJson(fogOfWar)))
        saveState["fogOfWar"] = fogOfWar
    }

    private fun getVisualOfProfile(profileIndex: Int): String {
        val newestSaveFile: SaveFile? = getExistingSaveFilesNewestFirstOf(profileIndex).firstOrNull()
        return newestSaveFile
            ?.let { getVisualOf(it, it.profileId) }
            ?: "${profileIndex + 1} $DEFAULT_EMPTY_PROFILE_VIEW"
    }

    private fun getNameOf(saveFile: SaveFile): String {
        return if (saveFile.isAutosave) AUTOSAVE_NAME else saveFile.profileId
    }

    private fun getVisualOf(saveFile: SaveFile, name: String): String {
        if (isValid(saveFile)) {
            return "$name [${saveFile.saveDate}]"
        } else {
            return "$name $INVALID_PROFILE_VIEW"
        }
    }

    private fun isValid(saveFile: SaveFile): Boolean {
        if (preferenceManager.isDebugModeOn) return true
        try {
            createSaveStateFrom(saveFile)
            return true
        } catch (e: SerializationException) {
            return false
        }
    }

    private fun createSaveStateFrom(saveFile: SaveFile): GdxMap<String, Any?> {
        return json.fromJson(saveFile.saveStateJson)
    }

    private fun getDefaultId(): String {
        return Constant.PLAYER_ID.replaceFirstChar { it.uppercase() }
    }

}

package nl.t64.cot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.reflect.ClassReflection
import com.badlogic.gdx.utils.reflect.ReflectionException
import nl.t64.cot.CrystalOfTime
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.menu.MenuScreen
import nl.t64.cot.screens.world.WorldScreen
import java.util.*


class ScreenManager {

    private val screenCache: EnumMap<ScreenType, Screen> = EnumMap(ScreenType::class.java)
    private val currentScreen: Screen get() = (Gdx.app.applicationListener as CrystalOfTime).screen

    fun openParchmentLoadScreen(screenTypeToLoad: ScreenType) {
        val loadScreen = getScreen(ScreenType.LOAD_SCREEN) as LoadScreen
        loadScreen.screenTypeToLoad = screenTypeToLoad
        setScreen(ScreenType.LOAD_SCREEN)
    }

    fun getWorldScreen(): WorldScreen {
        return getScreen(ScreenType.WORLD) as WorldScreen
    }

    fun getMenuScreen(screenType: ScreenType): MenuScreen {
        return getScreen(screenType) as MenuScreen
    }

    fun getScreen(screenType: ScreenType): Screen {
        ifNotInScreenCacheAdd(screenType)
        return screenCache[screenType]!!
    }

    fun getCurrentParchmentScreen(): ParchmentScreen = currentScreen as ParchmentScreen

    fun setScreen(screenType: ScreenType) {
        ifNotInScreenCacheAdd(screenType)
        setScreen(screenCache[screenType]!!)
    }

    private fun setScreen(screen: Screen) {
        (Gdx.app.applicationListener as CrystalOfTime).screen = screen
    }

    private fun ifNotInScreenCacheAdd(screenType: ScreenType) {
        if (!screenCache.containsKey(screenType)) {
            try {
                val newScreen = ClassReflection.getConstructor(screenType.screenClass).newInstance() as Screen
                screenCache[screenType] = newScreen
            } catch (e: ReflectionException) {
                throw GdxRuntimeException("Screen $screenType could not be created.", e)
            }
        }
    }

    fun dispose() {
        screenCache.values.forEach { it.dispose() }
        screenCache.clear()
    }

}

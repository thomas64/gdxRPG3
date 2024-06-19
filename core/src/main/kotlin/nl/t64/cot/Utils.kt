package nl.t64.cot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.Timer
import ktx.assets.dispose
import nl.t64.cot.audio.AudioManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.gamestate.GameData
import nl.t64.cot.gamestate.PreferenceManager
import nl.t64.cot.gamestate.ProfileManager
import nl.t64.cot.gamestate.Scenario
import nl.t64.cot.resources.ResourceManager
import nl.t64.cot.resources.SpriteConfig
import nl.t64.cot.screens.ScreenManager
import nl.t64.cot.screens.world.WorldScreen
import nl.t64.cot.screens.world.map.FogOfWarManager
import nl.t64.cot.screens.world.map.MapManager


private const val TITLE_PADDING = 50f
private const val TITLE_FONT = "fonts/spectral_extra_bold_28.ttf"
private const val TITLE_SIZE = 28
private const val SPRITE_BORDER = "sprites/border.png"
private const val SPRITE_BORDER_TOP = "sprites/border_top.png"
private const val SPRITE_BORDER_BOTTOM = "sprites/border_bottom.png"
private const val SPRITE_BORDER_RIGHT = "sprites/border_right.png"
private const val SPRITE_TOOLTIP = "sprites/tooltip.png"
private const val SPRITE_TOOLTIP_RIGHT = "sprites/tooltip_right.png"
private const val CHAR_PATH = "sprites/characters/%s.png"
private const val FACE_PATH = "sprites/faces/%s.png"
private const val DOOR_PATH = "sprites/objects/%s.png"
private const val CHEST_PATH = "sprites/objects/chest.png"
private const val SPRITE_PARCHMENT = "sprites/parchment.png"
private const val SMALL_PARCHMENT_WIDTH = 400f
private const val SMALL_PARCHMENT_HEIGHT = 280f
private const val MEDIUM_PARCHMENT_WIDTH = 400f
private const val MEDIUM_PARCHMENT_HEIGHT = 600f
private const val LIGHTMAP_PATH = "sprites/lightmaps/%s.png"


object Utils {

    private val crystalOfTime: CrystalOfTime get() = Gdx.app.applicationListener as CrystalOfTime
    val resourceManager: ResourceManager get() = crystalOfTime.resourceManager
    val preferenceManager: PreferenceManager get() = crystalOfTime.preferenceManager
    val profileManager: ProfileManager get() = crystalOfTime.profileManager
    val gameData: GameData get() = crystalOfTime.gameData
    val scenario: Scenario get() = crystalOfTime.scenario
    val screenManager: ScreenManager get() = crystalOfTime.screenManager
    val audioManager: AudioManager get() = crystalOfTime.audioManager
    val fogOfWarManager: FogOfWarManager get() = crystalOfTime.fogOfWarManager
    val mapManager: MapManager get() = crystalOfTime.mapManager
    val brokerManager: BrokerManager get() = crystalOfTime.brokerManager

    val worldScreen: WorldScreen get() = screenManager.getWorldScreen()

    private val screenShots: MutableSet<Texture> = mutableSetOf()

    fun setGamepadInputProcessor(inputProcessor: InputProcessor?) {
        crystalOfTime.gamepadMapping.setInputProcessor(inputProcessor)
    }

    fun isGamepadConnected(): Boolean {
        return Controllers.getCurrent()?.isConnected ?: false
    }

    fun createDefaultWindow(title: String, table: Table, titleAlignment: Int = Align.left): Window {
        val font = resourceManager.getTrueTypeAsset(TITLE_FONT, TITLE_SIZE)
        val windowStyle = WindowStyle(font, Color.BLACK, createFullBorder())
        return Window(title, windowStyle).apply {
            add(table)
            padTop(TITLE_PADDING)
            titleLabel.setAlignment(titleAlignment)
            isMovable = false
            pack()
        }
    }

    fun createParchmentDialog(font: BitmapFont): Dialog {
        val parchment = Sprite(resourceManager.getTextureAsset(SPRITE_PARCHMENT))
        val windowStyle = WindowStyle(font, Color.BLACK, SpriteDrawable(parchment))
        return Dialog("", windowStyle).apply {
            titleLabel.setAlignment(Align.center)
            setKeepWithinStage(true)
            isModal = true
            isMovable = false
            isResizable = false
        }
    }

    fun createFullBorder(): Drawable {
        val texture = resourceManager.getTextureAsset(SPRITE_BORDER)
        val ninepatch = NinePatch(texture, 1, 1, 1, 1)
        return NinePatchDrawable(ninepatch)
    }

    fun createTopBorder(sprite: String = SPRITE_BORDER_TOP): Drawable {
        val texture = resourceManager.getTextureAsset(sprite)
        val ninepatch = NinePatch(texture, 0, 0, 1, 0)
        return NinePatchDrawable(ninepatch)
    }

    fun createBottomBorder(): Drawable {
        val texture = resourceManager.getTextureAsset(SPRITE_BORDER_BOTTOM)
        val ninepatch = NinePatch(texture, 0, 0, 0, 1)
        return NinePatchDrawable(ninepatch)
    }

    fun createRightBorder(): Drawable {
        val texture = resourceManager.getTextureAsset(SPRITE_BORDER_RIGHT)
        val ninepatch = NinePatch(texture, 0, 1, 0, 0)
        return NinePatchDrawable(ninepatch)
    }

    fun createTooltipRightBorder(): Drawable {
        val texture = resourceManager.getTextureAsset(SPRITE_TOOLTIP_RIGHT)
        val ninepatch = NinePatch(texture, 0, 1, 0, 0)
        return NinePatchDrawable(ninepatch)
    }

    fun createTooltipWindowStyle(): WindowStyle {
        val texture = resourceManager.getTextureAsset(SPRITE_TOOLTIP)
        val ninepatch = NinePatch(texture, 1, 1, 1, 1)
        val drawable = NinePatchDrawable(ninepatch)
        return WindowStyle(BitmapFont(), Color.GREEN, drawable)
    }

    fun createLightmap(lightmapId: String): Texture {
        return resourceManager.getTextureAsset(String.format(LIGHTMAP_PATH, lightmapId))
    }

    fun createImage(path: String, x: Int, y: Int, width: Int, height: Int): Image {
        val texture = resourceManager.getTextureAsset(path)
        val region = TextureRegion(texture, x, y, width, height)
        return Image(region)
    }

    fun getCharImage(spriteId: String): Array<Array<TextureRegion>> {
        val charConfig = getSpriteConfigFrom(spriteId)
        val path = String.format(CHAR_PATH, charConfig.source)
        val row = charConfig.row - 1
        val col = charConfig.col - 1
        val splitOfEight = getSplitTexture(path, Constant.SPRITE_GROUP_WIDTH, Constant.SPRITE_GROUP_HEIGHT)
        val personSprite = splitOfEight[row][col]
        return personSprite.split(Constant.TILE_SIZE.toInt(), Constant.TILE_SIZE.toInt())
    }

    fun getFaceImage(spriteId: String, isFlipped: Boolean = true): Image {
        if (spriteId.isEmpty()) return Image()
        val faceConfig = getSpriteConfigFrom(spriteId)
        val path = String.format(FACE_PATH, faceConfig.source)
        val row = faceConfig.row - 1
        val col = faceConfig.col - 1
        val splitOfEight = getSplitTexture(path, Constant.FACE_SIZE.toInt())
        val characterFace = splitOfEight[row][col]
        if (isFlipped) characterFace.flip(true, false)
        return Image(characterFace)
    }

    private fun getSpriteConfigFrom(spriteId: String): SpriteConfig {
        return resourceManager.getSpriteConfig(spriteId)
            ?: resourceManager.getSpriteConfig(spriteId.substringBeforeLast("_"))!!
    }

    fun getDoorImage(spriteId: String, width: Int, isShadow: Boolean): Array<Array<TextureRegion>> {
        val doorConfig = resourceManager.getSpriteConfig(spriteId)!!
        val path = String.format(DOOR_PATH, doorConfig.source)
        val row = doorConfig.row - 1
        val col = doorConfig.col - (if (isShadow) 0 else 1)
        val allDoors = getSplitTexture(path, width, Constant.SPRITE_GROUP_HEIGHT * 2)
        val doorSprite = allDoors[row][col]
        return doorSprite.split(width, Constant.TILE_SIZE.toInt() * 2)
    }

    fun getChestImage(): List<TextureRegion> {
        val splitOfEight = getSplitTexture(CHEST_PATH, Constant.SPRITE_GROUP_WIDTH, Constant.SPRITE_GROUP_HEIGHT)
        val firstRedChest = splitOfEight[0][0]
        val twelveRedChestTextures = firstRedChest.split(Constant.TILE_SIZE.toInt(), Constant.TILE_SIZE.toInt())
        return listOf(twelveRedChestTextures[0][0], twelveRedChestTextures[3][0])
    }

    fun getSplitTexture(texturePath: String, size: Int): Array<Array<TextureRegion>> {
        return getSplitTexture(texturePath, size, size)
    }

    fun getSplitTexture(texturePath: String, width: Int, height: Int): Array<Array<TextureRegion>> {
        val completeTexture = resourceManager.getTextureAsset(texturePath)
        return TextureRegion.split(completeTexture, width, height)
    }

    fun getHpColor(currentHp: Int, maximumHp: Int): Color {
        val percentage = currentHp.toFloat() / maximumHp.toFloat()
        return when {
            percentage > 0.875f -> interpolateColor(Color.FOREST, Color.FOREST, (percentage - 0.875f) * 8f)
            percentage > 0.625f -> interpolateColor(Color.OLIVE, Color.FOREST, (percentage - 0.625f) * 4f)
            percentage > 0.500f -> interpolateColor(Color.GOLD, Color.OLIVE, (percentage - 0.500f) * 8f)
            percentage > 0.375f -> interpolateColor(Color.ORANGE, Color.GOLD, (percentage - 0.375f) * 8f)
            percentage > 0.250f -> interpolateColor(Color.RED, Color.ORANGE, (percentage - 0.250f) * 8f)
            percentage > 0.125f -> interpolateColor(Color.FIREBRICK, Color.RED, (percentage - 0.125f) * 8f)
            else -> interpolateColor(Color.FIREBRICK, Color.FIREBRICK, percentage * 8f)
        }
    }

    private fun interpolateColor(color1: Color, color2: Color, percentage: Float): Color {
        val r = color1.r + percentage * (color2.r - color1.r)
        val g = color1.g + percentage * (color2.g - color1.g)
        val b = color1.b + percentage * (color2.b - color1.b)
        return Color(r, g, b, 1f)
    }

    fun createScreenshot(withBlur: Boolean): Image {
        val region = ScreenUtils.getFrameBufferTexture()
        screenShots.add(region.texture)
        val screenshot = Image(region)
        if (withBlur) {
            screenshot.color = Color.DARK_GRAY
        }
        return screenshot
    }

    fun disposeScreenshots() {
        screenShots.disposeAndClear()
    }

    fun createTransparency(): Drawable {
        return Constant.TRANSPARENT.toDrawable()
    }

    fun createLargeParchment(): Image {
        return Image(resourceManager.getTextureAsset(SPRITE_PARCHMENT)).apply {
            setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
    }

    fun createMediumParchment(): Image {
        return Image(resourceManager.getTextureAsset(SPRITE_PARCHMENT)).apply {
            setSize(MEDIUM_PARCHMENT_WIDTH, MEDIUM_PARCHMENT_HEIGHT)
            setPosition(
                Gdx.graphics.width / 2f - MEDIUM_PARCHMENT_WIDTH / 2f,
                Gdx.graphics.height / 2f - MEDIUM_PARCHMENT_HEIGHT / 2f
            )
        }
    }

    fun createSmallParchment(): Image {
        return Image(resourceManager.getTextureAsset(SPRITE_PARCHMENT)).apply {
            setSize(SMALL_PARCHMENT_WIDTH, SMALL_PARCHMENT_HEIGHT)
            setPosition(
                Gdx.graphics.width / 2f - SMALL_PARCHMENT_WIDTH / 2f,
                Gdx.graphics.height / 2f - SMALL_PARCHMENT_HEIGHT / 2f
            )
        }
    }

    fun runWithDelay(delayInSeconds: Float, action: () -> Unit) {
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                action.invoke()
            }
        }, delayInSeconds)
    }

}

fun MutableSet<Texture>.disposeAndClear() {
    dispose()
    clear()
}

fun Color.toDrawable(): Drawable {
    return Image(toTexture()).drawable
}

fun Color.toTexture(): Texture {
    val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(this)
    pixmap.fill()
    return Texture(pixmap)
        .also { pixmap.dispose() }
}

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
import nl.t64.cot.audio.AudioManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.ScreenManager
import nl.t64.cot.screens.world.MapManager


private const val TITLE_PADDING = 50f
private const val TITLE_FONT = "fonts/fff_tusj_bold_30.ttf"
private const val TITLE_SIZE = 30
private const val SPRITE_BORDER = "sprites/border.png"
private const val SPRITE_BORDER_TOP = "sprites/border_top.png"
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
private const val LIGHTMAP_PATH = "sprites/lightmaps/%s.png"

private val TRANSPARENT = Color(0f, 0f, 0f, 0.5f)

object Utils {

    private val gdxRpg3: GdxRpg3 get() = Gdx.app.applicationListener as GdxRpg3
    val resourceManager: ResourceManager get() = gdxRpg3.resourceManager
    val preferenceManager: PreferenceManager get() = gdxRpg3.preferenceManager
    val profileManager: ProfileManager get() = gdxRpg3.profileManager
    val gameData: GameData get() = gdxRpg3.gameData
    val screenManager: ScreenManager get() = gdxRpg3.screenManager
    val audioManager: AudioManager get() = gdxRpg3.audioManager
    val mapManager: MapManager get() = gdxRpg3.mapManager
    val brokerManager: BrokerManager get() = gdxRpg3.brokerManager

    fun setGamepadInputProcessor(inputProcessor: InputProcessor?) {
        gdxRpg3.gamepadMapping.setInputProcessor(inputProcessor)
    }

    fun isGamepadConnected(): Boolean {
        return Controllers.getCurrent()?.isConnected ?: false
    }

    fun createDefaultWindow(title: String, table: Table): Window {
        val font = resourceManager.getTrueTypeAsset(TITLE_FONT, TITLE_SIZE)
        val windowStyle = WindowStyle(font, Color.BLACK, createFullBorder())
        return Window(title, windowStyle).apply {
            add(table)
            padTop(TITLE_PADDING)
            titleLabel.setAlignment(Align.left)
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

    fun createTopBorder(): Drawable {
        val texture = resourceManager.getTextureAsset(SPRITE_BORDER_TOP)
        val ninepatch = NinePatch(texture, 0, 0, 1, 0)
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
        val charConfig = resourceManager.getSpriteConfig(spriteId)
        val path = String.format(CHAR_PATH, charConfig.source)
        val row = charConfig.row - 1
        val col = charConfig.col - 1
        val splitOfEight = getSplitTexture(path, Constant.SPRITE_GROUP_WIDTH, Constant.SPRITE_GROUP_HEIGHT)
        val personSprite = splitOfEight[row][col]
        return personSprite.split(Constant.TILE_SIZE.toInt(), Constant.TILE_SIZE.toInt())
    }

    fun getFaceImage(spriteId: String): Image {
        val faceConfig = resourceManager.getSpriteConfig(spriteId)
        val path = String.format(FACE_PATH, faceConfig.source)
        val row = faceConfig.row - 1
        val col = faceConfig.col - 1
        val splitOfEight = getSplitTexture(path, Constant.FACE_SIZE.toInt())
        val characterFace = splitOfEight[row][col]
        return Image(characterFace)
    }

    fun getDoorImage(spriteId: String, width: Int, isShadow: Boolean): Array<Array<TextureRegion>> {
        val doorConfig = resourceManager.getSpriteConfig(spriteId)
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

    fun getHpColor(hpStats: Map<String, Int>): Color {
        var color = Color.ROYAL
        if (hpStats["lvlVari"]!! < hpStats["lvlRank"]!!) {
            color = Color.LIME
        }
        if (hpStats["staVari"]!! < hpStats["staRank"]!!) {
            color = Color.GOLD
        }
        if (hpStats["conVari"]!! < hpStats["conRank"]!!) {
            color = Color.FIREBRICK
            if (hpStats["staVari"]!! > 0) {
                color = Color.ORANGE
            }
        }
        return color
    }

    fun createScreenshot(withBlur: Boolean): Image {
        val screenshot = Image(ScreenUtils.getFrameBufferTexture())
        if (withBlur) {
            screenshot.color = Color.DARK_GRAY
        }
        return screenshot
    }

    fun createTransparency(): Drawable {
        val pixmap = Pixmap(1, 1, Pixmap.Format.Alpha)
        pixmap.setColor(TRANSPARENT)
        pixmap.fill()
        val drawable = Image(Texture(pixmap)).drawable
        pixmap.dispose()
        return drawable
    }

    fun createLargeParchment(): Image {
        return Image(resourceManager.getTextureAsset(SPRITE_PARCHMENT)).apply {
            setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
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

}

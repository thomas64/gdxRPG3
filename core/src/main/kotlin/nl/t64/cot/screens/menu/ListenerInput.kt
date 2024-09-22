package nl.t64.cot.screens.menu

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


private const val NO_MENU_INDEX = 0
private val NO_MENU_TO_UPDATE = { _: Int -> }

internal class ListenerInput {

    private val updateIndexFunction: (Int) -> Unit
    private var numberOfItems = 0

    private lateinit var selectItemFunction: () -> Unit
    private var definedIndex = 0

    private var selectedIndex = 0

    constructor(updateIndexFunction: (Int) -> Unit, numberOfItems: Int) {
        this.updateIndexFunction = updateIndexFunction
        this.numberOfItems = numberOfItems
    }

    constructor(selectItemFunction: () -> Unit) {
        this.updateIndexFunction = NO_MENU_TO_UPDATE
        this.selectItemFunction = selectItemFunction
        this.definedIndex = NO_MENU_INDEX
    }

    constructor(updateIndexFunction: (Int) -> Unit, selectItemFunction: () -> Unit, definedIndex: Int) {
        this.updateIndexFunction = updateIndexFunction
        this.selectItemFunction = selectItemFunction
        this.definedIndex = definedIndex
    }

    fun updateSelectedIndex(newSelectedIndex: Int) {
        selectedIndex = newSelectedIndex
    }

    fun inputPrev() {
        if (numberOfItems < 2) return

        if (selectedIndex <= 0) {
            playSe(AudioEvent.SE_MENU_ERROR)
            selectedIndex = 0
        } else {
            playSe(AudioEvent.SE_MENU_CURSOR)
            selectedIndex -= 1
        }
        updateIndexFunction.invoke(selectedIndex)
    }

    fun inputNext() {
        if (numberOfItems < 2) return

        if (selectedIndex >= numberOfItems - 1) {
            playSe(AudioEvent.SE_MENU_ERROR)
            selectedIndex = numberOfItems - 1
        } else {
            playSe(AudioEvent.SE_MENU_CURSOR)
            selectedIndex += 1
        }
        updateIndexFunction.invoke(selectedIndex)
    }

    fun inputConfirm() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        selectItemFunction.invoke()
    }

    fun inputCancel() {
        playSe(AudioEvent.SE_MENU_BACK)
        updateIndexFunction.invoke(definedIndex)
        selectItemFunction.invoke()
    }

    fun inputConfirmDefinedIndex() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        updateIndexFunction.invoke(definedIndex)
        selectItemFunction.invoke()
    }

}

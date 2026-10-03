package com.shanova.keyboard.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.shanova.keyboard.keyboard.KeyboardController

/**
 * SHAN-X-NOVA Keyboard — InputMethodService entry point.
 *
 * All typing happens locally through the standard InputConnection API.
 * No typed text is stored, logged or transmitted.
 */
class ShanovaImeService : InputMethodService() {

    private lateinit var controller: KeyboardController

    override fun onCreate() {
        super.onCreate()
        controller = KeyboardController(this)
    }

    override fun onCreateInputView(): View = controller.createRootView()

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        controller.inputConnection = currentInputConnection
        controller.editorInfo = attribute
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        controller.inputConnection = currentInputConnection
        controller.editorInfo = info
        controller.refresh()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        controller.inputConnection = null
        controller.editorInfo = null
    }
}

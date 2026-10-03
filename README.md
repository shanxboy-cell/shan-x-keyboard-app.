# SHAN-X-NOVA Keyboard

**Type Beyond Ordinary.**

By **SHAN** — a real Android keyboard built with Kotlin and `InputMethodService`.

Phase 1 delivers a working, installable keyboard with three languages,
a full emoji panel and a premium dark SHAN-X-NOVA design.

---

## Features (Phase 1)

- Real Android `InputMethodService` — no web views, no fake UI
- 🇱🇰 **Sinhala**, 🇬🇧 **English**, 🇮🇳 **Tamil** layouts
- 🌐 Globe key cycles languages instantly
- Number and symbol layers
- Shift, backspace (with key-repeat), enter, space
- Emoji panel: recently used, 9 categories, live search, direct insertion
- Settings app with a guided 2-step enable/switch flow
- Keyboard height adjustment (85%–135%)
- Key sound ON/OFF, haptic feedback ON/OFF
- 3 premium themes: Emerald Night, Neon Carbon, Forest Mist
- Black background, green accent, rounded keys, press animations

## Privacy

The app requests **no internet permission**. Typed text is never stored,
logged or transmitted. All preferences stay in on-device SharedPreferences.

## Project structure

```
app/src/main/java/com/shanova/keyboard/
├── ime/ShanovaImeService.kt        # InputMethodService entry point
├── keyboard/
│   ├── KeyboardController.kt       # state machine: language/layer/shift
│   ├── KeyboardView.kt             # renders layouts, animations, feedback
│   ├── model/KeyModels.kt          # Key / KeyRow / KeyboardLayout model
│   └── languages/
│       ├── Language.kt             # interface + registry + shared layers
│       ├── EnglishLanguage.kt
│       ├── SinhalaLanguage.kt
│       └── TamilLanguage.kt
├── emoji/
│   ├── EmojiData.kt                # categorized emoji catalogue + search
│   └── EmojiPanel.kt               # recents / categories / search / insert
├── settings/
│   ├── SettingsActivity.kt         # setup flow + preferences UI
│   └── Prefs.kt                    # local-only preference store
└── theme/KeyboardTheme.kt          # SHAN-X-NOVA themes
```

## Adding a new language

1. Create `MyLanguage.kt` implementing `Language` (extend `BaseLanguage`
   to reuse the shared number/symbol layers).
2. Register it in `LanguageRegistry.languages` — one line.

No other part of the keyboard changes.

## Build

Requires Android SDK (platform 34, build-tools 34) and JDK 17.

```bash
gradle assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Enable on a device

1. Install the APK.
2. Open **SHAN-X-NOVA Keyboard** → tap **Open Keyboard Settings** → enable it.
3. Tap **Choose SHAN-X-NOVA Keyboard** → select it as the active input method.
4. Switch languages any time with the 🌐 key.

## Roadmap (post Phase 1)

Additional languages, custom themes, clipboard, suggestions,
voice typing, swipe typing.

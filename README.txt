LIBAMB TEST APP — SOURCE PROJECT

What it does:
- Opens Android's file picker so you can select libAMB.so.
- Copies the selected file into this app's private storage.
- Calls System.load() on that file and displays the result/error.

Important:
- This is an isolated diagnostic app. It does NOT inject into Animal Company,
  install a mod menu, or provide a game loader.
- Loading a native library executes its native code. Only test files you trust.
- A successful load only means Android accepted the library in this app.
  It does not establish compatibility with Animal Company.
- A failure may be caused by missing native dependencies or game-specific
  symbols/classes that this standalone app does not provide.

Build:
1. Open this folder in Android Studio.
2. Let Gradle sync.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Install the APK on an ARM64 Android/Quest test device.
5. Open "libAMB Loader Test", choose libAMB.so, then tap Attempt to load.

Requirements:
- Android Studio with Android SDK Platform 35.
- Android Gradle Plugin 8.9.1.
- Device should be ARM64 for this ARM64 library.

No copy of libAMB.so is bundled in this project; choose the file from storage
when running the app.

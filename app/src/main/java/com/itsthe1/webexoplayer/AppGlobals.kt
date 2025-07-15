package com.itsthe1.webexoplayer

object AppGlobals {
    // server ip address
    var webViewURL: String = ""

    // api key for muslimsalat
    var muslimsalatAPIKey: String = "f9019c4279b6ff8b5d174d2fd9291422"

    // resolution
    var resolution: String = "720p"

    var appId: Int = 10

    var resolutionValue: Int = 22

    fun initialize(context: android.content.Context) {
        webViewURL = DeviceManager.getServerUrl(context)
        webViewURL = webViewURL.replaceFirst(Regex("^https?://"), "")
        webViewURL = webViewURL.trimEnd('/')
        if (webViewURL.isBlank()) {
            throw IllegalArgumentException("Server URL is invalid or empty!")
        }
        resolution = DeviceManager.getServerResolution(context)
        appId = DeviceManager.getServerAppId(context)
        resolutionValue = DeviceManager.getServerResolutionValue(context)
    }
} 
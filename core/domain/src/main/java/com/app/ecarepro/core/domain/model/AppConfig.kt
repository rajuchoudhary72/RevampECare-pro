package com.app.ecarepro.core.domain.model

import javax.inject.Singleton

/** * A data class holding essential, project-level configurations.
 * This object is provided as a singleton by Hilt and can be injected anywhere in the
 * application to access static or environmental data consistently.
 */
@Singleton
data class AppConfig(
    /** The base URL for all network API calls. */
    val baseUrl: String,

    /** A unique identifier for the physical device, which persists across app installs. */
    val deviceId: String,

    /** The public-facing version name of the application (e.g., "1.0.1"). */
    val appVersion: String,

    /** The internal version code of the application (e.g., 2). Used for programmatic checks. */
    val appVersionCode: Int,

    /** The type of the device, typically used to differentiate between form factors (e.g., "phone" or "tablet"). */
    val deviceType: DeviceType,

    /** The specific model name of the hardware device (e.g., "Pixel 8 Pro"). */
    val deviceModel: String,

    /** The unique application ID, as defined in the build configuration (e.g., "com.app.ecarepro"). */
    val applicationId: String,

    /** A boolean flag indicating if the current build is a debug version. Useful for enabling debug-only features. */
    val isDebug: Boolean,

    val osVersion: String
)


enum class DeviceType(val id: Int) {
    ANDROID(1),
    IOS(2)
}

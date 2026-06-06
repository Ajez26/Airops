package com.airops

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point — required by Hilt for code generation.
 * Referenced in AndroidManifest.xml as android:name=".AiropsApplication".
 */
@HiltAndroidApp
class AiropsApplication : Application()

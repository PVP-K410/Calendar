package com.pvp.app.service

import com.pvp.app.BuildConfig
import com.pvp.app.api.Configuration
import javax.inject.Inject

class ConfigurationImpl @Inject constructor() : Configuration {

    override val googleOAuthClientId: String = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
}
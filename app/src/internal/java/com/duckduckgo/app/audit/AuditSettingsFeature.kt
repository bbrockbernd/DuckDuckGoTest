/*
 * Copyright (c) 2021 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.audit

import android.content.Context
import com.duckduckgo.anvil.annotations.PriorityKey
import com.duckduckgo.app.browser.R
import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.internal.features.api.InternalFeaturePlugin
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
@PriorityKey(InternalFeaturePlugin.AUDIT_SETTINGS_PRIO_KEY)
class AuditSettingsFeature @Inject constructor(private val context: Context) : InternalFeaturePlugin {
    override fun internalFeatureTitle(): String {
        return context.getString(R.string.auditSettingsTitle)
    }

    override fun internalFeatureSubtitle(): String {
        return context.getString(R.string.auditSettingsSubtitle)
    }

    override fun onInternalFeatureClicked(activityContext: Context) {
        activityContext.startActivity(AuditSettingsActivity.intent(activityContext))
    }
}

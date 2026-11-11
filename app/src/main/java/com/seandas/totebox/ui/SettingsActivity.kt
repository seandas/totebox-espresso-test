package com.seandas.totebox.ui

import android.os.Bundle
import com.seandas.totebox.R
import com.seandas.totebox.data.Session
import com.seandas.totebox.databinding.ActivitySettingsBinding
import com.seandas.totebox.util.FakeNetwork

/** Account, simulated network speed and about. Settings last for the app session only. */
class SettingsActivity : ShopActivity() {
    override val menuItemForThisScreen = R.id.action_settings

    private val speeds = mapOf(R.id.speed_normal to 1f, R.id.speed_fast to 0f, R.id.speed_slow to 3f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireSession()) return
        val b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setUpToolbar(b.toolbar, getString(R.string.title_settings), showBack = true)

        b.settingsAccount.text = Session.email?.let { getString(R.string.settings_signed_in_as, it) }
            ?: getString(R.string.settings_guest)

        // A scale set from a test (not one of the three choices) leaves no button checked
        speeds.entries.firstOrNull { it.value == FakeNetwork.delayScale }?.let { b.networkSpeedGroup.check(it.key) }
        b.networkSpeedGroup.setOnCheckedChangeListener { _, checkedId ->
            speeds[checkedId]?.let { FakeNetwork.delayScale = it }
        }

        val version = packageManager.getPackageInfo(packageName, 0).versionName ?: ""
        b.settingsVersion.text = getString(R.string.settings_version, version)
    }
}

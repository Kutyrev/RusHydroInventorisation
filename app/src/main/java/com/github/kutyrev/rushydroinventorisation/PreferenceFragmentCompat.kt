package com.github.kutyrev.rushydroinventorisation

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val etpPassword : EditTextPreference? = preferenceManager.findPreference("service_pass")

        etpPassword?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            it.transformationMethod = PasswordTransformationMethod.getInstance()
            it.selectAll()
        }

        etpPassword?.setOnPreferenceChangeListener {
                preference, newValue ->
            if (newValue != "") etpPassword.summary = "***"
            else etpPassword.summary = ""

            true
        }

       if (!etpPassword?.text.isNullOrEmpty())etpPassword?.summary = "***"

    }

}
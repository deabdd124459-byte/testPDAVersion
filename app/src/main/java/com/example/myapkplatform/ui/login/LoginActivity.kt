package com.example.myapkplatform.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.pm.PackageInfoCompat
import com.example.myapkplatform.R
import com.example.myapkplatform.databinding.ActivityLoginBinding
import com.example.myapkplatform.model.LoginResponse
import com.example.myapkplatform.model.VersionInfo
import com.example.myapkplatform.ui.MainActivity
import com.example.myapkplatform.ui.base.ApiStatus
import com.example.myapkplatform.ui.base.BaseActivity
import com.example.myapkplatform.util.LocaleManager

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private val TAG = "LoginActivityUpdate"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar, getString(R.string.about_pda_client))
        setupLanguageSpinner()
        setupButtonClickListeners()
        observeViewModel()

        // Trigger the update check from the ViewModel
        viewModel.checkForUpdate()
    }

    private fun observeViewModel() {
        // Observer for login result
        viewModel.loginResult.observe(this) { apiResponse ->
            if (apiResponse.isSuccess && apiResponse.data != null) {
                handleLoginSuccess(apiResponse.data)
            } else {
                Toast.makeText(this, apiResponse.message ?: getString(R.string.login_failed), Toast.LENGTH_LONG).show()
            }
        }

        // Observer for API status (e.g., show loading indicator)
        viewModel.apiStatus.observe(this) { status ->
            val isLoading = status == ApiStatus.LOADING
            binding.buttonConfirm.isEnabled = !isLoading
        }

        // Observer for the update check result
        viewModel.updateResult.observe(this) { versionInfo ->
            if (versionInfo != null && versionInfo.versionCode > getCurrentVersionCode()) {
                showUpdateDialog(versionInfo)
            }
        }
    }

    private fun handleLoginSuccess(loginResponse: LoginResponse) {
        Toast.makeText(this, loginResponse.message ?: getString(R.string.login_success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("LOGIN_CREDENTIAL", loginResponse.data)
        }
        startActivity(intent)
        finish()
    }

    private fun setupLanguageSpinner() {
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        val currentLanguageCode = LocaleManager.getLanguage(this)
        val defaultLanguage = if (currentLanguageCode.startsWith("zh")) "繁體中文" else "English"
        binding.spinnerLanguage.setSelection(adapter.getPosition(defaultLanguage))

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                val languageCode = if (selectedLanguage == "繁體中文") "zh-TW" else "en"
                if (LocaleManager.getLanguage(this@LoginActivity) != languageCode) {
                    LocaleManager.setLocale(this@LoginActivity, languageCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupButtonClickListeners() {
        binding.buttonConfirm.setOnClickListener {
            val account = binding.editTextAccount.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (account.isBlank() || password.isBlank()) {
                Toast.makeText(this, getString(R.string.login_error_empty_credentials), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Shortcut for testing
            if (account == "test" && password == "test") {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                viewModel.login(account, password)
            }
        }

        binding.buttonCancel.setOnClickListener {
            finishAffinity() // Close the entire task
        }
    }

    // --- 自動更新相關 UI 邏輯 ---

    private fun getCurrentVersionCode(): Long {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            PackageInfoCompat.getLongVersionCode(pInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current version code", e)
            -1L
        }
    }

    private fun showUpdateDialog(versionInfo: VersionInfo) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.update_available_title))
            .setMessage(getString(R.string.update_available_message, versionInfo.versionName, versionInfo.releaseNotes))
            .setPositiveButton(getString(R.string.update_now)) { _, _ ->
                Log.d(TAG, "Starting download for URL: ${versionInfo.apkUrl}")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(versionInfo.apkUrl)
                }
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.update_later), null)
            .setCancelable(false) // Make user choose
            .show()
    }
}

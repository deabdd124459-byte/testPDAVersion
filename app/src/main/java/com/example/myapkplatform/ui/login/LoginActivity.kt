package com.example.myapkplatform.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.example.myapkplatform.ui.MainActivity
import com.example.myapkplatform.R
import com.example.myapkplatform.data.auth.LoginResponse
import com.example.myapkplatform.databinding.ActivityLoginBinding
import com.example.myapkplatform.ui.base.ApiStatus
import com.example.myapkplatform.ui.base.BaseActivity
import com.example.myapkplatform.util.LocaleManager

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Use the correct title for the login screen
        setupToolbar(binding.toolbar, getString(R.string.about_pda_client))

        setupLanguageSpinner()
        setupButtonClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { apiResponse ->
            // The logic is now driven by the server's response
            if (apiResponse.isSuccess && apiResponse.data?.data != null) {
                // Login successful, call the handler
                handleLoginSuccess(apiResponse.data)
            } else {
                // Login failed, show the detailed error message from the server
                Toast.makeText(this, apiResponse.message ?: getString(R.string.login_failed), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.apiStatus.observe(this) { status ->
            val isLoading = status == ApiStatus.LOADING
            binding.buttonConfirm.isEnabled = !isLoading
        }
    }

    private fun handleLoginSuccess(loginResponse: LoginResponse) {
        Toast.makeText(this, loginResponse.message ?: getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java).apply {
            // Pass the user data (LoginData) to MainActivity
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

            viewModel.login(account, password)
        }

        binding.buttonCancel.setOnClickListener {
            finishAffinity() // Close the entire task
        }
    }
}

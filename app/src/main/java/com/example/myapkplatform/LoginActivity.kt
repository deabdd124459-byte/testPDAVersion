package com.example.myapkplatform

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.myapkplatform.databinding.ActivityLoginBinding
import com.example.myapkplatform.ui.base.BaseActivity
import com.example.myapkplatform.util.LocaleManager

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 使用我們在 BaseActivity 中建立的輔助方法來設定 Toolbar
        setupToolbar(binding.toolbar, getString(R.string.title_login))

        setupLanguageSpinner()
        setupButtonClickListeners()
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
            val account = binding.editTextAccount.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (account.isNotEmpty() && password.isNotEmpty()) {
                // For now, any non-empty username and password will be considered valid
                // and will navigate to the MainActivity.
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.login_error_empty_credentials), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }
}

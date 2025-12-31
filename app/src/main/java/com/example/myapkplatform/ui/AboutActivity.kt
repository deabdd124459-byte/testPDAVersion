package com.example.myapkplatform.ui

import android.os.Bundle
import android.widget.Button
import com.example.myapkplatform.R
import com.example.myapkplatform.ui.base.BaseActivity

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Setup Toolbar
        setupToolbar(findViewById(R.id.toolbar), getString(R.string.system_about))

        // Handle close button click
        findViewById<Button>(R.id.button_close).setOnClickListener {
            finish() // Close this activity and go back to the previous one
        }
    }
}
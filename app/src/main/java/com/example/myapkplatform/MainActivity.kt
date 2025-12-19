package com.example.myapkplatform

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest

class MainActivity : AppCompatActivity() {

    private val installManager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    private lateinit var drawerLayout: DrawerLayout

    private val groups = listOf("System", "SMT", "Picking")

    private val children = mapOf(
        "System" to listOf("Logout", "Change Password"),
        "SMT" to listOf("Load Material", "Mount Stencil", "Mount Solder"),
        "Picking" to listOf("Picking", "OQC")
    )

    private val dfmMap = mapOf(
        "Load Material" to DfmInfo(
            moduleName = "dfm_loadmaterial",
            fragmentClass = "com.example.myapkplatform.dfm_loadmaterial.LoadMaterialFragment"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        setupButtons()
        setupMenu()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_open_menu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<Button>(R.id.btn_close_app).setOnClickListener {
            finishAffinity()
        }
    }

    private fun setupMenu() {
        val expandableListView = findViewById<ExpandableListView>(R.id.function_list)

        val groupData = groups.map { mapOf("NAME" to it) }
        val childData = groups.map { group ->
            children[group]!!.map { mapOf("NAME" to it) }
        }

        val adapter = SimpleExpandableListAdapter(
            this,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("NAME"),
            intArrayOf(android.R.id.text1),
            childData,
            android.R.layout.simple_list_item_1,
            arrayOf("NAME"),
            intArrayOf(android.R.id.text1)
        )

        expandableListView.setAdapter(adapter)

        expandableListView.setOnGroupClickListener { _, _, groupPosition, _ ->
            if (expandableListView.isGroupExpanded(groupPosition)) {
                expandableListView.collapseGroup(groupPosition)
            } else {
                expandableListView.expandGroup(groupPosition, true)
            }
            true
        }

        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val groupName = groups[groupPosition]
            val itemName = children[groupName]!![childPosition]

            drawerLayout.closeDrawer(GravityCompat.START)
            handleMenuClick(itemName)
            true
        }
    }

    private fun handleMenuClick(itemName: String) {
        val dfm = dfmMap[itemName]

        if (dfm == null) {
            Toast.makeText(this, "點擊功能：$itemName", Toast.LENGTH_SHORT).show()
            return
        }

        loadDfm(dfm)
    }

    private fun loadDfm(dfm: DfmInfo) {
        if (installManager.installedModules.contains(dfm.moduleName)) {
            renderFragment(dfm.fragmentClass)
            return
        }

        Toast.makeText(this, "下載模組中…", Toast.LENGTH_SHORT).show()

        val request = SplitInstallRequest.newBuilder()
            .addModule(dfm.moduleName)
            .build()

        installManager.startInstall(request)
            .addOnSuccessListener {
                renderFragment(dfm.fragmentClass)
            }
            .addOnFailureListener {
                Toast.makeText(this, "模組下載失敗", Toast.LENGTH_SHORT).show()
            }
    }

    private fun renderFragment(fragmentClassName: String) {
        try {
            val fragmentClass = Class.forName(fragmentClassName)
            val fragment = fragmentClass.getConstructor().newInstance() as Fragment

            supportFragmentManager.beginTransaction()
                .replace(R.id.dfm_container, fragment)
                .commit()

        } catch (e: Exception) {
            Toast.makeText(this, "Fragment 載入失敗", Toast.LENGTH_SHORT).show()
        }
    }
}

data class DfmInfo(
    val moduleName: String,
    val fragmentClass: String
)

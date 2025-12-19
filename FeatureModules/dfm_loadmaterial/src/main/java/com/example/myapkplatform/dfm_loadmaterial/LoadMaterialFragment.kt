package com.example.myapkplatform.dfm_loadmaterial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class LoadMaterialFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_load_material, container, false)

        val btnLoad = view.findViewById<Button>(R.id.btn_load)
        val editPartNo = view.findViewById<EditText>(R.id.edit_part_no)
        val checkMsd = view.findViewById<CheckBox>(R.id.check_msd)

        btnLoad.setOnClickListener {
            val partNo = editPartNo.text.toString()
            val isMsd = if (checkMsd.isChecked) "是" else "否"

            if (partNo.isEmpty()) {
                Toast.makeText(context, "請先輸入料號", Toast.LENGTH_SHORT).show()
            } else {
                // 這裡未來可以寫入資料庫或對接 API
                Toast.makeText(context, "料號 $partNo 領料成功！ (MSD: $isMsd)", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }
}
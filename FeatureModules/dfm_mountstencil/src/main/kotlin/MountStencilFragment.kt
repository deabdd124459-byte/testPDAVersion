package com.example.myapkplatform.dfm_mountstencil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class MountStencilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_mount_stencil, container, false)

        val spnLine = view.findViewById<Spinner>(R.id.spnLine)
        val txtPartNo = view.findViewById<EditText>(R.id.txtPartNo)
        val txtStencilId = view.findViewById<EditText>(R.id.txtStencilId)
        val btnMount = view.findViewById<Button>(R.id.btnMount)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        // 先給 Spinner 假資料避免 null
        val lines = listOf("SMT-1", "SMT-2", "SMT-3", "SMT-4")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lines)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnLine.adapter = adapter

        btnMount.setOnClickListener {
            val line = spnLine.selectedItem?.toString() ?: ""
            val part = txtPartNo.text.toString()
            val stencil = txtStencilId.text.toString()

            if (part.isEmpty() || stencil.isEmpty()) {
                Toast.makeText(requireContext(), "請輸入必要資料", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "掛網成功\nLine: $line\nPart: $part\nStencil: $stencil",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // 關閉當前 fragment
        btnClose.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        }

        return view
    }
}

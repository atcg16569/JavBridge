package com.example.javBridge

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.javBridge.database.Url
import com.example.javBridge.viewModel.UrlViewModel


class UrlDialogFragment(private val urlViewModel: UrlViewModel) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_url_dialog, null)
            builder.apply {
                setView(view)
                setPositiveButton("save") { _, _ ->
                    val name = view.findViewById<EditText>(R.id.urlName).text.toString()
                    val link = view.findViewById<EditText>(R.id.urlLink).text.toString()
                    if (name.isNotEmpty() && link.isNotEmpty() && link.startsWith("http")) {
                        urlViewModel.liveUrl(name).observe(this@UrlDialogFragment, { url ->
                            if (url == null) {
                                val ur = Url(name, link)
                                urlViewModel.add(ur)
                            } else {
                                Log.d("addUrl", "${url.name} existed")
                            }
                        })
                    }
                }
                setNegativeButton("cancel") { _, _ ->
                    dialog?.cancel()
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
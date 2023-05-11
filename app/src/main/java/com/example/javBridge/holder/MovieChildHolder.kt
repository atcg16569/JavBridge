package com.example.javBridge.holder

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.example.javBridge.database.Url
import com.example.javBridge.databinding.MovieChildItemBinding


class MovieChildHolder(private val binding: MovieChildItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(url: Url, parentID: String) {
        binding.button.text = url.name
        binding.button.setOnClickListener {
            try {
                val context = binding.root.context
//                val idRegex = Regex("%s")
                val idRegex = Regex("\\\$id")
                //link改变需要重启。若live观察一旦变化,则会自动跳转
                //在父adpter观察即可
                if (url.link.contains("\$id")) {
                    val link = idRegex.replace(url.link, parentID)
//                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
//                    binding.root.context.startActivity(webIntent)
                    val builder = CustomTabsIntent.Builder()
                        .setToolbarColor(Color.parseColor("#161828"))
                    val intent = builder.build()
                    enableChromeCustomTabsForOtherBrowsers(intent.intent, context)
                    intent.launchUrl(context, Uri.parse(link))
                    Log.d("position", parentID)
                } else {
                    Toast.makeText(
                        context,
                        "url has no wildcard",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } catch (e: ActivityNotFoundException) {
                e.message?.let { it1 -> Log.e("openWeb", it1) }
            }

        }
    }
}

private fun enableChromeCustomTabsForOtherBrowsers(customTabIntent: Intent?, context: Context) {
    val checkpkgs = arrayOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.chrome.dev",
        "com.google.android.apps.chrome",
        "org.chromium.chrome",
        "org.mozilla.fennec_fdroid",
        "org.mozilla.firefox",
        "org.mozilla.firefox_beta",
        "org.mozilla.fennec_aurora",
        "org.mozilla.klar",
        "org.mozilla.focus"
    )

    // Get all intent handlers for web links
    val pm: PackageManager = context.packageManager
    val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com"))
    val browsers: MutableList<String?> = ArrayList()
    for (ri in pm.queryIntentActivities(urlIntent, 0)) {
        val i = Intent("android.support.customs.action.CustomTabsService")
        i.setPackage(ri.activityInfo.packageName)
        if (pm.resolveService(i, 0) != null) {
            browsers.add(ri.activityInfo.packageName)
        }
    }

    // Check if the user has a "default browser" selected
    val ri = pm.resolveActivity(urlIntent, 0)
    val userDefaultBrowser = ri?.activityInfo?.packageName

    // Select which browser to use out of all installed customtab supporting browsers
    var pkg: String? = null
    if (browsers.isEmpty()) {
        pkg = null
    } else if (browsers.size == 1) {
        pkg = browsers[0]
    } else if (!TextUtils.isEmpty(userDefaultBrowser) && browsers.contains(userDefaultBrowser)) {
        pkg = userDefaultBrowser
    } else {
        for (checkpkg in checkpkgs) {
            if (browsers.contains(checkpkg)) {
                pkg = checkpkg
                break
            }
        }
        if (pkg == null && browsers.isNotEmpty()) {
            pkg = browsers[0]
        }
    }
    if (pkg != null && customTabIntent != null) {
        customTabIntent.setPackage(pkg)
    }
}
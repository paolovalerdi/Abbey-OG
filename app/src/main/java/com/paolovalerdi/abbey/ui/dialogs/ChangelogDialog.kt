package com.paolovalerdi.abbey.ui.dialogs

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.lifecycleScope
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.dialog_web_view.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class ChangelogDialog : RoundedBottomSheetDialog() {

    companion object {

        fun create(): ChangelogDialog = ChangelogDialog()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.dialog_web_view,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundColor = colorToCSS(requireContext().resolveAttrColor(R.attr.colorBackgroundFloating))
        val contentColor = colorToCSS(requireContext().resolveAttrColor(android.R.attr.textColorPrimary))

        lifecycleScope.launch(IO) {
            try {
                val data = async {
                    val buf = StringBuilder()
                    val json = requireContext().assets.open("abbey-changelog.html")
                    val input = BufferedReader(InputStreamReader(json, "UTF-8"))
                    input.lineSequence().forEach {
                        buf.append(it)
                    }
                    input.close()
                    buf.toString()
                        .replace(
                            "{style-placeholder}",
                            String.format(
                                "body { background-color: %s; color: %s; }",
                                backgroundColor,
                                contentColor
                            )
                        )
                        .replace("{link-color}", contentColor)
                        .replace("{link-color-active}", contentColor)

                }
                withContext(Main) {
                    webView.loadData(
                        data.await(),
                        "text/html",
                        "UTF-8"
                    )
                    setChangelogRead(requireContext())
                }

            } catch (e: Throwable) {
                withContext(Main) {
                    webView.loadData("<h1>Unable to load</h1><p> ${e.localizedMessage} </p>", "text/html", "UTF-8")
                }
            }
        }
    }

    private fun setChangelogRead(context: Context) {
        try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val currentVersion = PackageInfoCompat.getLongVersionCode(pInfo).toInt()
            PreferenceUtil.lastChangelogVersion = currentVersion
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun colorToCSS(color: Int) = String.format(
        "rgb(%d, %d, %d)",
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )

}
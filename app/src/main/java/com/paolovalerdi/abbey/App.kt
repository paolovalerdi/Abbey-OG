package com.paolovalerdi.abbey

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.kabouzeid.appthemehelper.ThemeStore
import com.paolovalerdi.abbey.appshortcuts.DynamicShortcutManager
import com.paolovalerdi.abbey.repository.SongRepositoryKT
import com.paolovalerdi.abbey.util.extensions.makeToast

class App : Application() {

    companion object {

        const val GOOGLE_PLAY_LICENSE_KEY = BuildConfig.GOOGLE_PLAY_LICENSE_KEY
        const val PRO_VERSION_PRODUCT_ID = "pro_version"

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
            private set

        @SuppressLint("StaticFieldLeak")
        lateinit var staticContext: Context
            private set

        val isProVersion
            get() = BuildConfig.DEBUG or instance.billingProcessor.isPurchased(PRO_VERSION_PRODUCT_ID)

    }

    private lateinit var billingProcessor: BillingProcessor

    override fun onCreate() {
        super.onCreate()
        instance = this

        staticContext = applicationContext
        SongRepositoryKT.create(contentResolver)

        if (ThemeStore.isConfigured(this, 1).not()) {
            ThemeStore.editTheme(this)
                .primaryColorRes(R.color.md_white_1000)
                .accentColorRes(R.color.abbey_accent_color)
                .commit()
        }

        // Set up dynamic shortcuts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            DynamicShortcutManager(this).initDynamicShortcuts()
        }

        billingProcessor = BillingProcessor(this, GOOGLE_PLAY_LICENSE_KEY, object : BillingProcessor.IBillingHandler {

            override fun onBillingInitialized() {}

            override fun onPurchaseHistoryRestored() {
                makeToast("Purchase restored.")
            }

            override fun onProductPurchased(productId: String, details: TransactionDetails?) {}

            override fun onBillingError(errorCode: Int, error: Throwable?) {}

        })

    }

    override fun onTerminate() {
        super.onTerminate()
        billingProcessor.release()
    }

}

package com.paolovalerdi.abbey.ui.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.material.button.MaterialButton
import com.paolovalerdi.abbey.App
import com.paolovalerdi.abbey.App.Companion.GOOGLE_PLAY_LICENSE_KEY
import com.paolovalerdi.abbey.App.Companion.PRO_VERSION_PRODUCT_ID
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.ProPagerAdapter
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import com.paolovalerdi.abbey.util.extensions.makeToast
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PurchaseDialog : DialogFragment(), BillingProcessor.IBillingHandler {

    companion object {
        fun newInstance() = PurchaseDialog()
    }

    private lateinit var billingProcessor: BillingProcessor

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        billingProcessor = BillingProcessor.newBillingProcessor(requireActivity(), GOOGLE_PLAY_LICENSE_KEY, this)
        billingProcessor.initialize()

        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_purchase, null)
        customView.findViewById<MaterialButton>(R.id.restoreButton).apply {
            setOnClickListener { restorePurchase() }
        }
        customView.findViewById<MaterialButton>(R.id.purchaseButton).apply {
            applyAccentColor()
            setOnClickListener { purchase() }
        }
        customView.findViewById<ViewPager>(R.id.purchaseFeatures).apply {
            adapter = ProPagerAdapter(requireContext())
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.support_development)
            .setView(customView)
            .create()
    }

    override fun onBillingInitialized() {}

    override fun onPurchaseHistoryRestored() {
        if (App.isProVersion) {
            requireContext().makeToast("Done! Thank you")
            dismiss()
        } else {
            requireContext().makeToast("No purchase found")
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        requireContext().makeToast("Thank you! Enjoy")
        dismiss()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.d("PurchaseDialog", "Billing error code: $errorCode", error)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data).not()) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        billingProcessor.release()
        super.onDestroy()
    }

    private fun purchase() {
        if (billingProcessor.isOneTimePurchaseSupported) {
            billingProcessor.purchase(requireActivity(), PRO_VERSION_PRODUCT_ID)
        } else {
            requireContext().makeToast("No Google services :(")
        }
    }

    private fun restorePurchase() {
        lifecycleScope.launch {
            val isPurchased = async(IO) { billingProcessor.loadOwnedPurchasesFromGoogle() }
            if (isPurchased.await()) onPurchaseHistoryRestored()
        }
    }

}
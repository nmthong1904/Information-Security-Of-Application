package com.example.adsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.*


class AdsActivity : AppCompatActivity() {

    private companion object {
        private const val TAG = "BANNER_AD_TAG"
    }

    private var adsView:AdView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)

        title = "Banner Ads"

        MobileAds.initialize(this){
            Log.d(TAG,"onInitializationCompleted:")
        }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("PLACE_TEST_DEVICE_ID_1_HERE","PLACE_TEST_DEVICE_ID_2_HERE"))
                .build()
        )

        adsView = findViewById(R.id.AdsBanner)

        val adRequest = AdRequest.Builder().build()

        adsView?.loadAd(adRequest)

        adsView?.adListener = object : AdListener(){
            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG,"onAdClicked: ")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(TAG,"onAdClosed: ")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.d(TAG,"onAdFailedToLoad: ${adError.message}")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(TAG,"onAdImpression: ")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG,"onAdLoaded: ")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(TAG,"onAdOpened: ")
            }
        }
    }

    override fun onPause() {
        adsView?.pause()
        super.onPause()
        Log.d(TAG,"onPause: ")
    }

    override fun onResume() {
        adsView?.resume()
        super.onResume()
        Log.d(TAG,"onResume: ")
    }

    override fun onDestroy() {
        adsView?.destroy()
        super.onDestroy()
        Log.d(TAG,"onDestroy: ")
    }
}
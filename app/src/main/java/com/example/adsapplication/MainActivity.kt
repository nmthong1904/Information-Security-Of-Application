package com.example.adsapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var bannerbtn:MaterialButton

    private lateinit var Interstitialbtn:MaterialButton

    private lateinit var rewardbtn:MaterialButton

    private lateinit var qrcodebtn:MaterialButton

    private lateinit var loginbtn:MaterialButton

    private lateinit var smsverify:MaterialButton

    private lateinit var biometric:MaterialButton

    private lateinit var pincode:MaterialButton

    private lateinit var host:MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "AdMob Ads"

        bannerbtn = findViewById(R.id.ads)
        Interstitialbtn = findViewById(R.id.Interstitial)
        rewardbtn = findViewById(R.id.reward)
        qrcodebtn = findViewById(R.id.qrcode)
        loginbtn = findViewById(R.id.login)
        smsverify = findViewById(R.id.smsverify)
        biometric = findViewById(R.id.biometric)
        pincode = findViewById(R.id.pincode)
        host = findViewById(R.id.host)




        bannerbtn.setOnClickListener {
            startActivity(Intent(this,AdsActivity::class.java))
        }
        Interstitialbtn.setOnClickListener {
            startActivity(Intent(this,InterstitialAdActivity::class.java))
        }
        rewardbtn.setOnClickListener{
            startActivity(Intent(this,RewardActivity::class.java))
        }
        qrcodebtn.setOnClickListener{
            startActivity(Intent(this,QrCodeActivity::class.java))
        }
        loginbtn.setOnClickListener{
            startActivity(Intent(this,RecaptchaActivity::class.java))
        }
        smsverify.setOnClickListener{
            startActivity(Intent(this,SmsverifyActivity::class.java))
        }
        biometric.setOnClickListener{
            startActivity(Intent(this,BiometricActivity::class.java))
        }
        pincode.setOnClickListener{
            startActivity(Intent(this,PinCodeActivity::class.java))
        }
        host.setOnClickListener{
            startActivity(Intent(this,HostActivity::class.java))
        }
    }
}
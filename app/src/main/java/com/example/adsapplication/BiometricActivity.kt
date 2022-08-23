package com.example.adsapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class BiometricActivity : AppCompatActivity() {

    private lateinit var executor:Executor
    private lateinit var biometricPrompt : BiometricPrompt
    private lateinit var promptInfo:BiometricPrompt.PromptInfo
    private lateinit var verifyBtn:Button
    private lateinit var tv:TextView
    private lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        verifyBtn = findViewById(R.id.verify)
        tv = findViewById(R.id.tv)
        img = findViewById(R.id.imgFingure)

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this@BiometricActivity, executor, object :BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                tv.text="Authentication Error: $errString"
                Toast.makeText(this@BiometricActivity,"Authentication Error: $errString" , Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                tv.text="Authentication Success:"
                Toast.makeText(this@BiometricActivity,"Authentication Success", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                tv.text="Authentication Failed:"
                Toast.makeText(this@BiometricActivity,"Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
        verifyBtn.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        img.setOnClickListener {
            checkDeviceHasBiometric()
        }

    }

     fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                tv.text = "App can authenticate using biometrics."
                verifyBtn.isEnabled = true

            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                tv.text = "No biometric features available on this device."
                verifyBtn.isEnabled = false

            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                tv.text = "Biometric features are currently unavailable."
                verifyBtn.isEnabled = false

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                verifyBtn.isEnabled = false

                startActivityForResult(enrollIntent, 100)
            }
        }
    }
}
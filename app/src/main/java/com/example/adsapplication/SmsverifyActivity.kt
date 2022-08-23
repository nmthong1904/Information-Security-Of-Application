package com.example.adsapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.adsapplication.databinding.ActivitySmsverifyBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class SmsverifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsverifyBinding

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    private var mVerificationId:String?=null

    private val TAG = "MAIN_TAG"

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsverifyBinding.inflate(layoutInflater)
        //turn off screen capturing
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)

        binding.phonelayout.visibility = View.VISIBLE
        binding.requestOtp.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        mCallbacks = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG,"onVerificationCompleted")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.d(TAG,"onVerificationFailed:${e.message}")
                Toast.makeText(this@SmsverifyActivity,"${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG,"onCodeSent:$verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                binding.phonelayout.visibility = View.GONE
                binding.requestOtp.visibility = View.VISIBLE

                Toast.makeText(this@SmsverifyActivity,"Verification code sent", Toast.LENGTH_SHORT).show()
                binding.description.visibility = View.VISIBLE
                binding.description.text = "Please type verification code we sent to${binding.phone.text.toString().trim()}"

            }
        }
        binding.submit.setOnClickListener {
            val phone = binding.phone.text.toString().trim()
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@SmsverifyActivity,"Please enter phone number", Toast.LENGTH_SHORT).show()
            }else if (phone.length>15){
                Toast.makeText(this@SmsverifyActivity,"Wrong phone number", Toast.LENGTH_SHORT).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }
        binding.resend.setOnClickListener {
            val phone = binding.phone.text.toString().trim()
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@SmsverifyActivity,"Please enter phone number", Toast.LENGTH_SHORT).show()
            }else if (phone.length>15){
                Toast.makeText(this@SmsverifyActivity,"Wrong phone number", Toast.LENGTH_SHORT).show()
            }else{
                resendVerificationCode(phone,forceResendingToken!!)
            }
        }
        binding.submmit.setOnClickListener {
            val code = binding.otp.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this@SmsverifyActivity,"Please enter OTP code", Toast.LENGTH_SHORT).show()
            }else{
                verifyPhoneNumberWithCode(mVerificationId,code)
            }
        }
    }
    private fun startPhoneNumberVerification(phone:String){
        Log.d(TAG,"startPhoneNumberVerification:${phone}")

        progressDialog.setMessage("Verify your phone number")
        progressDialog.show()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun resendVerificationCode(phone: String,token: PhoneAuthProvider.ForceResendingToken){
        progressDialog.setMessage("Resending Code")
        progressDialog.show()

        Log.d(TAG,"resendVerificationCode:${phone}")


        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun verifyPhoneNumberWithCode(verificationId:String?,code:String){
        Log.d(TAG,"verifyPhoneNumberWithCode:$verificationId $code")

        progressDialog.setMessage("Verifying Code")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId!!,code)
        signInWithPhoneAuthCredential(credential)

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){

        Log.d(TAG,"signInWithPhoneAuthCredential:")

        progressDialog.setMessage("Logging In")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this,"Logging", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,SuccessActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Fail", Toast.LENGTH_SHORT).show()
            }
    }
}
package com.example.adsapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap



class RecaptchaActivity : AppCompatActivity() {

    var TAG = MainActivity::class.java.simpleName
    private lateinit var btnverifyCaptcha: Button
    var SITE_KEY = "6Ldc22shAAAAAC8psyMJg0A1r7vBJt4Ak60SEAFN"
    var SECRET_KEY = "6Ldc22shAAAAACZ-P7nwIX2k-P-fnfmKTGNwOqXh"
    var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recaptcha)
        btnverifyCaptcha = findViewById(R.id.button)

        btnverifyCaptcha.setOnClickListener {
            onClick()
        }

        queue = Volley.newRequestQueue(applicationContext)
    }

    private fun onClick() {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
            .addOnSuccessListener(this) { response ->
                if (!response.tokenResult!!.isEmpty()) {
                    handleSiteVerify(response.tokenResult)
                }
            }
            .addOnFailureListener(this) { e ->
                if (e is ApiException) {
                    Log.d(
                        TAG, "Error message: " +
                                CommonStatusCodes.getStatusCodeString(e.statusCode)
                    )
                } else {
                    Log.d(TAG, "Unknown type of error: " + e.message)
                }
            }
    }

    private fun handleSiteVerify(responseToken: String?) {
        //it is google recaptcha siteverify server
        //you can place your server url
        val url = "https://www.google.com/recaptcha/api/siteverify"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        //code logic when captcha returns true Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getBoolean("success")),Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(
                            applicationContext,
                            jsonObject.getString("error-codes").toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (ex: Exception) {
                    Log.d(TAG, "JSON exception: " + ex.message)
                }
            },
            Response.ErrorListener { error -> Log.d(TAG, "Error message: " + error.message) }) {
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["secret"] = SECRET_KEY
                params["response"] = responseToken!!
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(request)
    }
}
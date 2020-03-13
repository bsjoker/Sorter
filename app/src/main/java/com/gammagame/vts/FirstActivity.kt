package com.gammagame.vts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bolts.AppLinks
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_load.*

class FirstActivity : AppCompatActivity() {

    private var userCountry: String = "na"
    private var isStart = false
    private var fb: String? = ""
    private val sp = App.instance.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        FacebookSdk.sdkInitialize(this)

        val ed = sp.edit()
        myRef = FirebaseDatabase.getInstance().getReference()

        startProgressBar()

        var getUserCountry: () -> Unit = {
            val telephonyManager =
                App.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            Log.d(get(), "Country: " + telephonyManager.simCountryIso.toLowerCase())
            userCountry = telephonyManager.simCountryIso.toLowerCase()
        }

        getUserCountry()

        setSubscribeToTopic()

        var msg = sp.getString("fb", "sss")
        Log.d(get(), msg)

        AppLinkData.fetchDeferredAppLinkData(
            this,
            object : AppLinkData.CompletionHandler {
                override fun onDeferredAppLinkDataFetched(appLinkData: AppLinkData?) {
                    val uri = if (appLinkData != null) {
                        appLinkData.targetUri
                    } else {
                        AppLinks.getTargetUrlFromInboundIntent(applicationContext, intent)
                    }
                    var value = ""
                    if (uri != null) {
                        var count = 1
                        value = uri.pathSegments.joinToString("&") {
                            "subid${count++}=$it"
                        }
                    }
                    fb = value
                    Log.d(get(), "fb: " + fb)
                    if (fb != "") {
                        with(ed) {
                            putString("fb", fb)
                            apply()
                        }
                    }
                }
            }
        )

        if (sp.getString("fb", null) == null) {
            myRef.child("state").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = dataSnapshot.getValue(Boolean::class.java)
                    Log.d(get(), "Value state is: $value")
                    if (value != null) {
                        with(ed) {
                            putBoolean("state", value)
                            apply()
                        }
                    }
                    if (!isStart && value!!) {
                        myRef.child("AcceptCountry").addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                val value = dataSnapshot.getValue(String::class.java)
                                Log.d(get(), "Value AcceptCountry is: $value")
                                if (value != null) {
                                    with(ed) {
                                        putString("country", value)
                                        apply()
                                    }
                                }
                                Log.d(get(), "Value userCounry is: " + userCountry)
                                if (!isStart) {
                                    nextActivity(value!!.contains(userCountry, true))
                                    isStart = true
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                Log.w(get(), "Failed to read value.", error.toException())
                            }
                        })
                    } else if (!isStart) {
                        nextActivity(isStart)
                        isStart = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(get(), "Failed to read value.", error.toException())
                }
            })
        } else {
            Log.d(get(), sp.getString("fb", null))
            nextActivity(true)
            isStart = true
        }
    }

    private fun nextActivity(isWViewNext: Boolean) {
        when (isWViewNext) {
            true -> startActivity(
                Intent(
                    this@FirstActivity,
                    BestActivity::class.java
                )
            )

            false -> startActivity(
                Intent(
                    this@FirstActivity,
                    StartActivity::class.java
                )
            )
        }
    }


    fun setSubscribeToTopic() {
        FirebaseMessaging.getInstance().apply {
            subscribeToTopic("pharaon")
                .addOnSuccessListener { println("!!!") }
            subscribeToTopic("country_" + userCountry)
                .addOnSuccessListener { println("!!!") }
        }
    }

    private fun startProgressBar() {
        Thread(Runnable {
            var progress = 0
            while (progress < 100) {

                Thread.sleep(500)

                progress.also {
                    progress_bar_h.setProgress(it)
                    progress = it + 5
                }
            }
            if (!isStart) {
                nextActivity(false)
                isStart = true
            }
        }).start()
    }

    private inline fun <reified T> T.get() = T::class.java.simpleName
}
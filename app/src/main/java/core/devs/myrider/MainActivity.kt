package core.devs.myrider

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val sharedPrefFile = "sharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

        //Check if A user is already signed In!
        when (sharedPreferences.getInt("signedIn",0)) {
            0 -> {
                startActivity(
                    Intent(
                        this@MainActivity,
                        SignIn::class.java
                    )
                )
            }
            1 -> {
                startActivity(
                    Intent(
                        this@MainActivity,
                        HomePage::class.java
                    )
                )
            }
            else -> {
                startActivity(
                    Intent(
                        this@MainActivity,
                        SignIn::class.java
                    )
                )
            }
        }

    }


}
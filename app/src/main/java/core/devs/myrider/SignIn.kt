package core.devs.myrider

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SignIn : AppCompatActivity() {

    private var edtE: EditText? = null
    private var edtP: EditText? = null
    private lateinit var fP: TextView
    private lateinit var btnIn : AppCompatButton
    private lateinit var btnUp : AppCompatButton
    private lateinit var prog : ProgressBar

    private var em: String? = null
    private var pa: String? = null

    private val sharedPrefFile = "sharedPref"

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        fP = findViewById(R.id.forgot_password)
        edtE = findViewById(R.id.login_email)
        edtP = findViewById(R.id.login_password)
        btnIn = findViewById(R.id.sign_in_btn)
        btnUp = findViewById(R.id.sign_up_btn)
        prog = findViewById(R.id.prog)

        //Forget Password Click Function
        fP.setOnClickListener{
            Toast.makeText(this, "Currently Unavialable", Toast.LENGTH_SHORT).show()
        }

        //Sign In Button Click Function
        btnIn.setOnClickListener{
            validateInputs()
        }

        //Sign Up Button Click Function
        btnUp.setOnClickListener {
            startActivity(
                Intent(
                    this@SignIn,
                    SignUp::class.java
                )
            )
        }

    }

    //Validate Input Data before Sign In process
    private fun validateInputs(){
        if (TextUtils.isEmpty(edtE!!.text.toString())) {
            Toast.makeText(this, "Email is Required!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(edtP!!.text.toString())) {
            Toast.makeText(this, "Password is Required!", Toast.LENGTH_SHORT).show()
            return
        }

        em = edtE?.text.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
        pa = edtP?.text.toString().trim { it <= ' ' }

        //Disable All Button and Textview
        edtE!!.isEnabled = false
        edtP!!.isEnabled = false
        btnIn.isEnabled = false
        btnUp.isEnabled = false
        fP.isEnabled = false
        prog.visibility = View.VISIBLE

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        //Email and Password Ok Try Sign In Process!
        signInProcess()
    }

    //Sign In Process
    private fun signInProcess() {
        val postUrl = "https://tranxfercrypto.com/raider/api/login.php"
        val requestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        try {
            postData.put("email", em)
            postData.put("password", pa)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, postUrl, postData,
            { response ->
                val stat = response["status"]
                val res = response["message"]

                if(stat == "Ok"){
                    val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putInt("signedIn",1)
                    editor.putString("profile", response.toString())
                    editor.apply()
                    Toast.makeText(applicationContext, "$res", Toast.LENGTH_LONG).show()
                    startActivity(
                        Intent(
                            this@SignIn,
                            HomePage::class.java
                        )
                    )
                    restoreDefault()
                } else {
                    restoreDefault()
                    Toast.makeText(applicationContext, "$res", Toast.LENGTH_LONG).show()
                }

            }
        ) { error ->
            run {
                restoreDefault()
                Toast.makeText(
                    applicationContext,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    //Enable all Buttons and Textviews
    private fun restoreDefault(){
        prog.visibility = View.GONE
        edtE!!.isEnabled = true
        edtP!!.isEnabled = true
        btnIn.isEnabled = true
        btnUp.isEnabled = true
        fP.isEnabled = true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage(getString(R.string.sure_to_exit))
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.no)
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setNegativeButton(
                getString(R.string.yes_exit)
            ) { _: DialogInterface?, _: Int -> finishAffinity() }
        dialog = alertDialogBuilder.create()
        dialog!!.show()
    }
}
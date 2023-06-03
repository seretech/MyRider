package core.devs.myrider

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SignUp : AppCompatActivity() {

    private var edtF: EditText? = null
    private var edtL: EditText? = null
    private var edtP: EditText? = null
    private var edtE: EditText? = null
    private var edtPa: EditText? = null

    private lateinit var havAccount : TextView
    private lateinit var btnUp : AppCompatButton
    private lateinit var prog : ProgressBar

    private var fn: String? = null
    private var ln: String? = null
    private var ph: String? = null
    private var em: String? = null
    private var pa: String? = null

    private val sharedPrefFile = "sharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        havAccount = findViewById(R.id.havAccount)
        edtF = findViewById(R.id.fName)
        edtL = findViewById(R.id.lName)
        edtP = findViewById(R.id.phone)
        edtE = findViewById(R.id.email)
        edtPa = findViewById(R.id.pass)
        btnUp = findViewById(R.id.sign_up_btn)
        prog = findViewById(R.id.prog)

        //Already Have An Account Click Function
        havAccount.setOnClickListener {
            startActivity(
                Intent(
                    this@SignUp,
                    SignIn::class.java
                )
            )
        }

        //Sign Up Button Click Function
        btnUp.setOnClickListener{
            validateInputs()
        }
    }

    //Validate Input Data before Sign Up Process
    private fun validateInputs(){
        if (TextUtils.isEmpty(edtF!!.text.toString())) {
            edtF!!.requestFocus()
            Toast.makeText(this, "FirstName is Required", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(edtL!!.text.toString())) {
            edtL!!.requestFocus()
            Toast.makeText(this, "Last Name is Required!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(edtP!!.text.toString())) {
            edtP!!.requestFocus()
            Toast.makeText(this, "Phone Number is Required!", Toast.LENGTH_SHORT).show()
            return
        }

        if (edtP!!.text.toString().length != 11) {
            edtP!!.requestFocus()
            Toast.makeText(this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(edtE!!.text.toString())) {
            edtE!!.requestFocus()
            Toast.makeText(this, "Email is Required!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(edtPa!!.text.toString())) {
            edtPa!!.requestFocus()
            Toast.makeText(this, "Password is Required", Toast.LENGTH_SHORT).show()
            return
        }

        fn = edtF?.text.toString().trim { it <= ' ' }
        ln = edtF?.text.toString().trim { it <= ' ' }
        ph = edtP?.text.toString().trim { it <= ' ' }
        em = edtE?.text.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
        pa = edtPa?.text.toString().trim { it <= ' ' }

        //Disable All Button and Textview
        edtF!!.isEnabled = false
        edtL!!.isEnabled = false
        edtPa!!.isEnabled = false
        edtE!!.isEnabled = false
        edtP!!.isEnabled = false
        btnUp.isEnabled = false
        havAccount.isEnabled = false
        prog.visibility = View.VISIBLE
        btnUp.visibility = View.INVISIBLE

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        //All Input Ok Try Sign Up Process!
        signUpProcess()
    }

    //Sign Up Process
    private fun signUpProcess() {
        val postUrl = "https://tranxfercrypto.com/raider/api/reg.php"
        val requestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        try {
            postData.put("firstname", fn)
            postData.put("lastname", ln)
            postData.put("phone", ph)
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
                    val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
                        MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putInt("signedIn",0)
                    editor.apply()
                    Toast.makeText(applicationContext, "Account Created Successfully Please Sign In", Toast.LENGTH_LONG).show()
                    startActivity(
                        Intent(
                            this@SignUp,
                            SignIn::class.java
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
        btnUp.visibility = View.VISIBLE
        edtF!!.isEnabled = true
        edtL!!.isEnabled = true
        edtP!!.isEnabled = true
        edtE!!.isEnabled = true
        edtPa!!.isEnabled = true
        btnUp.isEnabled = true
        havAccount.isEnabled = true
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "startActivity(Intent(this@SignUp, SignIn::class.java))",
        "android.content.Intent"
    )
    )
    override fun onBackPressed() {
        startActivity(
            Intent(
                this@SignUp,
                SignIn::class.java
            )
        )
    }

}
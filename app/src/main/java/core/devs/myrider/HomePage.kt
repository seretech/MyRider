package core.devs.myrider

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class HomePage : AppCompatActivity() {

    private lateinit var welcome: TextView
    private lateinit var signOut: TextView

    private lateinit var edtF: EditText
    private lateinit var edtT: EditText

    private lateinit var naTxt: TextView
    private lateinit var teTxt: TextView
    private lateinit var riTxt: TextView
    private lateinit var coTxt: TextView
    private lateinit var reTxt: TextView
    private lateinit var diTxt: TextView

    private lateinit var btnReq: AppCompatButton
    private lateinit var prog: ProgressBar
    private lateinit var ls: ListView
    private lateinit var arv: CardView

    private lateinit var cd: CountDownTimer

    private var driverList: ArrayList<DriverClass>? = null
    private var driverAdapter: DriverAdapter? = null

    private val sharedPrefFile = "sharedPref"
    private var t: Int? = 0
    private var dis: Long? = 0

    private var fmLoc: String? = null
    private var toLoc: String? = null

    private var js: JSONObject? = null

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        welcome = findViewById(R.id.welcome)
        signOut = findViewById(R.id.sign_out)
        btnReq = findViewById(R.id.req_btn)
        prog = findViewById(R.id.prog)
        ls = findViewById(R.id.ls)
        arv = findViewById(R.id.arvLayout)
        naTxt = findViewById(R.id.dName)
        teTxt = findViewById(R.id.dTel)
        riTxt = findViewById(R.id.dRide)
        coTxt = findViewById(R.id.dColor)
        reTxt = findViewById(R.id.dReg)
        diTxt = findViewById(R.id.dDis)
        edtF = findViewById(R.id.edtF)
        edtT = findViewById(R.id.edtT)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        js = sharedPreferences.getString("profile", "")?.let { JSONObject(it) }

        welcome.text = buildString {
            append("Welcome Back\n")
            append(js!!.getJSONObject("data")["lName"].toString())
            append(" ")
            append(js!!.getJSONObject("data")["fName"].toString())
        }

        signOut.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("signedIn", 0)
            editor.apply()
            startActivity(
                Intent(
                    this@HomePage,
                    SignIn::class.java
                )
            )
        }

        btnReq.setOnClickListener {
            when (btnReq.text) {
                "Request A Ride" -> {
                    validateInputs()
                }
                "Cancel Ride" -> {
                    restoreDefault1()
                }
                else -> {
                    Toast.makeText(this, "Feature Currently Unavailable", Toast.LENGTH_SHORT).show()
                }
            }

        }

        ls.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val dClass: DriverClass = driverList!![position]
            val str: String?
            str = dClass.getname() + "\n" + dClass.getride()

            naTxt.text = dClass.getname()
            teTxt.text = dClass.gettel()
            riTxt.text = dClass.getride()
            coTxt.text = dClass.getcolors()
            reTxt.text = dClass.getreg()

            dis = dClass.getdis()?.toLong()

            confirmBooking(str)
        }

    }

    //Validate Input Data
    private fun validateInputs(){
        if (TextUtils.isEmpty(edtF.text.toString())) {
            Toast.makeText(this, "Enter Pick-Up Location!", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(edtT.text.toString())) {
            Toast.makeText(this, "Enter Drop-Off Location!", Toast.LENGTH_SHORT).show()
            return
        }

        fmLoc = edtF.text.toString().trim { it <= ' ' }
        toLoc = edtT.text.toString().trim { it <= ' ' }

        //Disable All Button and Textview
        edtF.isEnabled = false
        edtT.isEnabled = false
        btnReq.isEnabled = false
        prog.visibility = View.VISIBLE

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        //Pick-Up and Drop-Off Ok Try Load Available Rides!
        loadData()
    }

    private fun loadData() {
        driverList = ArrayList<DriverClass>()

        val url = "https://tranxfercrypto.com/raider/api/drivers.json"
        val requestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.GET, url,
            null,
            { response: JSONObject ->
                try {
                    val jsonArray = response.getJSONArray("data")
                    t = jsonArray.length() - 1
                    for (k in 0..t!!) {
                        val js = jsonArray.optJSONObject(k)
                        val userId = js.optString("userId", "NA")
                        val name = js.optString("name", "NA")
                        val tel = js.optString("tel", "NA")
                        val ride = js.optString("ride", "NA")
                        val colors = js.optString("colors", "NA")
                        val reg = js.optString("reg", "NA")
                        val dis = js.optString("distance", "NA")

                        val dClass = DriverClass()
                        dClass.setuserId(userId)
                        dClass.setname(name)
                        dClass.settel(tel)
                        dClass.setride(ride)
                        dClass.setcolors(colors)
                        dClass.setreg(reg)
                        dClass.setdis(dis)

                        driverList!!.add(dClass)
                    }

                    driverAdapter = DriverAdapter(
                        this,
                        R.layout.driver_adapter,
                        driverList!!
                    )
                    prog.visibility = View.GONE
                    ls.visibility = View.VISIBLE
                    btnReq.text = getString(R.string.sel_ride)
                    driverAdapter!!.setListData(driverList!!)
                    ls.adapter = driverAdapter
                } catch (e: JSONException) {
                    restoreDefault()
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
        requestQueue.add(request)
    }

    private fun restoreDefault() {
        prog.visibility = View.GONE
        btnReq.isEnabled = true
    }

    private fun startTimer() {
        val x = dis!! * 1000 * 60
        cd = object : CountDownTimer(x, 1000) {
            override fun onTick(c: Long) {
                val a = c / 1000
                diTxt.text = buildString {
                    append("Arriving In ")
                    append(a.toString())
                    append(" Seconds ")
                }
            }

            override fun onFinish() {
                diTxt.text = getString(R.string.ride_here)
                Toast.makeText(applicationContext, "Your Ride is Here", Toast.LENGTH_LONG).show()
            }
        }.start()
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

    private fun confirmBooking(str: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setTitle(getString(R.string.confirm_ride))
            .setMessage(str)
            .setIcon(R.drawable.logo)
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.no)
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setNegativeButton(
                getString(R.string.yes)
            ) { _: DialogInterface?, _: Int -> processBooking() }
        dialog = alertDialogBuilder.create()
        dialog!!.show()
    }

    private fun processBooking() {
        btnReq.isEnabled = true
        driverList?.clear()
        ls.visibility = View.GONE
        arv.visibility = View.VISIBLE
        btnReq.text = getString(R.string.cancel_ride)
        startTimer()
    }

    private fun restoreDefault1(){
        cd.cancel()
        edtF.isEnabled = true
        edtT.isEnabled = true
        edtF.text = null
        edtT.text = null
        arv.visibility = View.GONE
        btnReq.text = getString(R.string.request_ride)
    }

}


package core.devs.myrider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DriverAdapter(
    myContext: Context,
    private val layoutResourceId: Int,
    driverList1: ArrayList<DriverClass>
) :
    ArrayAdapter<DriverClass?>(myContext, layoutResourceId,
        driverList1 as List<DriverClass?>
    ) {
    private var driverList: ArrayList<DriverClass>

    init {
         driverList = driverList1
    }

    fun setListData(moviesClasses: ArrayList<DriverClass>) {
        driverList = moviesClasses
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(layoutResourceId, parent, false)
            holder = ViewHolder()
            holder.dName = view.findViewById(R.id.dName)
            holder.dTel = view.findViewById(R.id.dTel)
            holder.dRide = view.findViewById(R.id.dRide)
            holder.dColor = view.findViewById(R.id.dColor)
            holder.dReg = view.findViewById(R.id.dReg)
            holder.dDis = view.findViewById(R.id.dDis)

            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        val dClass: DriverClass = driverList[position]

        holder.dName!!.text = "Driver's Name - "+dClass.name
        holder.dTel!!.text = "Driver's Tel - "+dClass.tel
        holder.dRide!!.text = "Car Name - "+dClass.ride
        holder.dColor!!.text = "Car Color - "+dClass.colors
        holder.dReg!!.text = "Car Registration - "+dClass.reg
        holder.dDis!!.text = "Pick Up In - "+dClass.dis+" Minutes"

        return view!!
    }

    private class ViewHolder {
        var dName: TextView? = null
        var dTel: TextView? = null
        var dRide: TextView? = null
        var dColor: TextView? = null
        var dReg: TextView? = null
        var dDis: TextView? = null
    }
}
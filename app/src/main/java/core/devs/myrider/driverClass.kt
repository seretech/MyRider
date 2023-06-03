package core.devs.myrider

import androidx.annotation.Keep

@Keep
class DriverClass {

    private var userId: String? = null
    var name: String? = null
    var tel: String? = null
    var colors: String? = null
    var reg: String? = null
    var dis: String? = null
    var ride: String? = null

    fun setuserId(userId: String?) {
        this.userId = userId
    }

    fun getname(): String? {
        return name
    }

    fun setname(name: String?) {
        this.name = name
    }

    fun gettel(): String? {
        return tel
    }

    fun settel(tel: String?) {
        this.tel = tel
    }

    fun getride(): String? {
        return ride
    }

    fun setride(ride: String?) {
        this.ride = ride
    }

    fun getcolors(): String? {
        return colors
    }

    fun setcolors(colors: String?) {
        this.colors = colors
    }

    fun getreg(): String? {
        return reg
    }

    fun setreg(reg: String?) {
        this.reg = reg
    }

    fun getdis(): String? {
        return dis
    }

    fun setdis(dis: String?) {
        this.dis = dis
    }

}
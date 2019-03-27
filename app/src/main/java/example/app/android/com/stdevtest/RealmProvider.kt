package example.app.android.com.stdevtest

import android.content.Context
import example.app.android.com.stdevtest.utils.Constants.ERROR
import example.app.android.com.stdevtest.utils.Constants.SUCCESS
import example.app.android.com.stdevtest.models.UrlAddress


import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort

class RealmProvider(private val context: Context) {

    private var realm: Realm? = null

    val allNews: List<UrlAddress>
        get() {
            val realmResults = realm!!.where(UrlAddress::class.java).findAll()
            return realmResults.subList(0, realmResults.size)
        }

    fun init() {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
    }

    fun put(result: UrlAddress): Boolean {
        realm!!.beginTransaction()
        val urlAddress = realm!!.where(UrlAddress::class.java).equalTo("name", result.name).findFirst()
        if (urlAddress != null) {
            realm!!.commitTransaction()
            return false
        } else {
            val item = realm!!.createObject(UrlAddress::class.java)
            item.name = (result.name)
            item.image= result.image
            item.state = result.state
        }
        realm!!.commitTransaction()
        return true
    }
    //
    fun updateAddress(user: UrlAddress) {
        realm!!.beginTransaction()
        val urlAddress = realm!!.where(UrlAddress::class.java).equalTo("name", user.name).findFirst()
        urlAddress!!.state = user.state
        urlAddress!!.image= user.image
        realm!!.commitTransaction()
    }

    fun getByName(name: String): List<UrlAddress> {
        val it = realm?.where(UrlAddress::class.java)?.contains("name", name)!!.findAll()
        return it.subList(0, it.size)
    }

    fun deleteByUrl(url: String) {
        realm!!.beginTransaction()
        val user = realm?.where(UrlAddress::class.java)?.equalTo("name", url)!!.findFirst()
        user?.deleteFromRealm()
        realm!!.commitTransaction()
    }

    fun getForSortByNAme(): List<UrlAddress> {
        val realmResults = realm?.where(UrlAddress::class.java)?.findAll()?.sort("name")?.sort("name")
        return realmResults!!.subList(0, realmResults.size)
    }

    fun getForSortByDescending(): List<UrlAddress> {
        val realmResults = realm?.where(UrlAddress::class.java)?.findAll()?.sort("name")?.
            sort("name", Sort.DESCENDING)
        return realmResults!!.subList(0, realmResults.size)
    }

    fun getBySuccess(): List<UrlAddress> {
        val realmResults = realm?.where(UrlAddress::class.java)?.contains("state", SUCCESS)?.findAll()
        return realmResults!!.subList(0, realmResults.size)
    }

    fun getByError(): List<UrlAddress> {
        val realmResults = realm?.where(UrlAddress::class.java)?.contains("state", ERROR)?.findAll()
        return realmResults!!.subList(0, realmResults.size)
    }
}

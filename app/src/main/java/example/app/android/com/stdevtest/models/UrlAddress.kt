package example.app.android.com.stdevtest.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
 open class UrlAddress (
     open var name: String = "",
     open var image : Int = 0,
     open var state : String = ""
):RealmObject()





package example.app.android.com.stdevtest.activities

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import example.app.android.com.stdevtest.utils.Constants.ERROR
import example.app.android.com.stdevtest.utils.Constants.LOADING
import example.app.android.com.stdevtest.utils.Constants.SUCCESS
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.widget.ImageView
import android.widget.LinearLayout
import example.app.android.com.stdevtest.R
import example.app.android.com.stdevtest.RealmProvider
import example.app.android.com.stdevtest.adapters.CustomAdapter
import example.app.android.com.stdevtest.models.UrlAddress


class MainActivity : AppCompatActivity(), CustomAdapter.ChangeStateListener,
    CustomAdapter.OnDeleteClickListener {


    private var recyclerView: RecyclerView? = null
    private var customAdapter: CustomAdapter? = null
    private var buttonAdd: ImageView? = null
    private var rootLayout: LinearLayout? = null
    private var editText: EditText? = null
    private var list: MutableList<UrlAddress>? = null

    private var realmProvider: RealmProvider? = null
    internal var defaultState = R.drawable.ic_hourglass


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list = ArrayList<UrlAddress>()

        realmProvider = RealmProvider(this)
        realmProvider!!.init()

        editText = findViewById(R.id.edit_text_email)
        buttonAdd = findViewById(R.id.btnAdd)
        recyclerView = findViewById(R.id.recyclerview)
        rootLayout = findViewById(R.id.rootLayout)

        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this,
            R.drawable.item_divider
        )!!)
        recyclerView!!.addItemDecoration(itemDecorator)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = layoutManager

        customAdapter = CustomAdapter(this)
        customAdapter!!.setChangeStateListener(this)
        customAdapter!!.setOnDeleteListener(this)

        customAdapter!!.addAll(realmProvider!!.allNews)
        //  customAdapter.notifyDataSetChanged();
        recyclerView!!.adapter = customAdapter


        editText?.setText("https://www.")
        Selection.setSelection(editText?.getText(), editText?.getText()!!.length)
        editText?.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }
                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
                ) {
                }
                override fun afterTextChanged(s: Editable) {
                    if (!s.toString().startsWith("https://www.")) {
                        editText?.setText("https://www.")
                        Selection.setSelection(editText?.getText(), editText?.getText()!!.length)
                    }
                }
            })



        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                customAdapter!!.addAll(if (newText == "") realmProvider!!.allNews else realmProvider!!.getByName(newText))
                return false
            }
        })

        buttonAdd!!.setOnClickListener {

            if (isNetworkAvailable()) {
                if (!editText!!.text.toString().matches("".toRegex())
                    && !editText!!.text.toString().equals("https://www.") && checkSpace()
                ) {
                    val addAddress = UrlAddress(editText!!.text.toString(), defaultState, LOADING)
                    if (realmProvider!!.put(addAddress)) {
                        Handler().postDelayed({
                            list!!.add(addAddress)
                            customAdapter!!.add(addAddress)
                            requestCheck(editText!!.text.toString())
                        }, 100)
                    } else {
                        Toast.makeText(this@MainActivity, "Address already exist", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Please enter correct URL address", Toast.LENGTH_SHORT).show()
                }
            }else{
                Snackbar.make(rootLayout!!, // Parent view
                    "Please check internet connection!", // Message to show
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
            }
        }
    }


    fun requestCheck(url: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    try {
                        TimeUnit.SECONDS.sleep(1)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    // customAdapter.yourMethodName(State.ERROR,0);
                    list!![list!!.size - 1].state = ERROR
                    customAdapter!!.notifyDataSetChanged()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        try {
                            TimeUnit.SECONDS.sleep(2)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        list!![list!!.size - 1].state = SUCCESS
                        customAdapter!!.notifyDataSetChanged()
                    }

                }
            }
        })
    }

    fun checkSpace():Boolean{
        if (editText?.text.toString().contains(" ")){
            return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sub_menu_item1 -> {
                customAdapter!!.addAll(realmProvider!!.getBySuccess())
                customAdapter!!.notifyDataSetChanged()
                return true
            }
            R.id.sub_menu_item2 -> {
                customAdapter!!.addAll(realmProvider!!.getForSortByNAme())
                customAdapter!!.notifyDataSetChanged()
                return true
            }
            R.id.sub_error -> {
                customAdapter!!.addAll(realmProvider!!.getByError())
                customAdapter!!.notifyDataSetChanged()
                return true
            }
            R.id.sub_hakarak -> {
                customAdapter!!.addAll(realmProvider!!.getForSortByDescending())
                customAdapter!!.notifyDataSetChanged()
                return true
            }
            else ->
                //pass the event to parent, for options menu, use below
                return super.onOptionsItemSelected(item)
        }
    }

    fun  isNetworkAvailable():Boolean{
        val connectivityManager=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }


    override fun changeSuccessState(urlAddress: UrlAddress) {
        realmProvider!!.updateAddress(urlAddress)
    }

    override fun changeErrorState(urlAddress: UrlAddress) {
        //realmProvider.updateAddress(urlAddress);
        realmProvider!!.updateAddress(urlAddress)
    }

    override fun onDelete(urlAddress: String) {
        realmProvider!!.deleteByUrl(urlAddress)
    }
}


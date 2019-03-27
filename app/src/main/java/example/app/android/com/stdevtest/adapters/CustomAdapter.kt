package example.app.android.com.stdevtest.adapters

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import example.app.android.com.stdevtest.R
import example.app.android.com.stdevtest.RealmProvider
import example.app.android.com.stdevtest.utils.Constants.ERROR
import example.app.android.com.stdevtest.utils.Constants.LOADING
import example.app.android.com.stdevtest.utils.Constants.SUCCESS
import example.app.android.com.stdevtest.models.UrlAddress
import java.util.ArrayList

class CustomAdapter(private val context: Context) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    private val urlAdressList: MutableList<UrlAddress>?
    private val realmProvider: RealmProvider? = null
    private var changeStateListener: ChangeStateListener? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null



    fun setChangeStateListener(changeStateListener: ChangeStateListener) {
        this.changeStateListener = changeStateListener
    }

    fun setOnDeleteListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    init {
        urlAdressList = ArrayList<UrlAddress>()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val current = urlAdressList!![i]
        myViewHolder.textViewName.setText(current.name)
        myViewHolder.imageViewStatus.setImageResource(current.image!!)

        if (current.state.equals(SUCCESS)) {
            myViewHolder.imageViewStatus.setImageResource(R.drawable.ic_check)
            changeStateListener!!.changeSuccessState(current)

        } else if (current.state.equals(ERROR)) {
            myViewHolder.imageViewStatus.setImageResource(R.drawable.ic_error)
            changeStateListener!!.changeErrorState(current)
        }

        myViewHolder.imgDelete.setOnClickListener {
            if (!current.state.equals(LOADING)) {
                urlAdressList.remove(current)
                onDeleteClickListener!!.onDelete(current.name)
                notifyDataSetChanged()
            }else{
                AlertDialog.Builder(context)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        urlAdressList.remove(current)
                        onDeleteClickListener!!.onDelete(current.name)
                        notifyDataSetChanged()
                    }
                    .setNegativeButton("NO", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return urlAdressList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView
        val imageViewStatus: ImageView
        val imgDelete: ImageView

        init {
            textViewName = itemView.findViewById(R.id.tv_title)
            imageViewStatus = itemView.findViewById(R.id.image)
            imgDelete = itemView.findViewById(R.id.imgDelete)
        }
    }


    fun add(user: UrlAddress) {
        urlAdressList?.add(user)
        notifyDataSetChanged()
    }

    fun addAll(users: List<UrlAddress>) {
        urlAdressList!!.clear()
        if (users.size != 0) {
            urlAdressList.addAll(users)
        }

        notifyDataSetChanged()
    }

    interface ChangeStateListener {
        fun changeSuccessState(user: UrlAddress)
        fun changeErrorState(user: UrlAddress)
    }

    interface OnDeleteClickListener {
        fun onDelete(user: String)
    }
}
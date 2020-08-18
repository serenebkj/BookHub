package com.serene.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.serene.bookhub.R
import com.serene.bookhub.database.BookDatabase
import com.serene.bookhub.database.BookEntity
import com.serene.bookhub.model.Book
import com.serene.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor:TextView
    lateinit var txtBookPrice:TextView
    lateinit var txtBookRating:TextView
    lateinit var imgBookimage:ImageView
    lateinit var txtBookDescrip:TextView
    lateinit var btnAddToFav:Button
    lateinit var ProgressBar:ProgressBar
    lateinit var Progresslayout:RelativeLayout
    lateinit var AboutContent:LinearLayout
    lateinit var toolbar: Toolbar
    var bookId:String?="100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName=findViewById(R.id.txtBookName)
        txtBookAuthor=findViewById(R.id.txtBookAuthor)
        txtBookPrice=findViewById(R.id.txtBookPrice)
        txtBookRating=findViewById(R.id.txtBookRating)
        imgBookimage=findViewById(R.id.imgBookimage)
        btnAddToFav=findViewById(R.id.btnAddToFav)
        txtBookDescrip=findViewById(R.id.txtBookDescrip)
        ProgressBar=findViewById(R.id.Progressbar)
        Progresslayout=findViewById(R.id.Progresslayout)
        ProgressBar.visibility= View.VISIBLE
        Progresslayout.visibility=View.VISIBLE
        toolbar=findViewById(R.id.ToolBAR)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book details"

        if(intent!=null){
            bookId=intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
        }
        if(bookId=="100"){
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
        }
        val queue= Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"

        val jsonParams=JSONObject()
        jsonParams.put("book_id",bookId)
        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){
            val jsonRequest=object:JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener {
                try{
                    val success=it.getBoolean("success")

                    if(success){

                        val bookJsonObject=it.getJSONObject("book_data")
                        Progresslayout.visibility=View.GONE
                        val bookImageurl=bookJsonObject.getString("image")
                        Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookimage)

                        txtBookName.text=bookJsonObject.getString("name")
                        txtBookAuthor.text=bookJsonObject.getString("author")
                        txtBookRating.text=bookJsonObject.getString("rating")
                        txtBookPrice.text=bookJsonObject.getString("price")
                        txtBookDescrip.text=bookJsonObject.getString("description")
                        val bookEntity=BookEntity(
                            bookId?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDescrip.text.toString(),
                            bookImageurl
                        )
                        val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                        val isFav=checkFav.get()
                        if(isFav){
                            btnAddToFav.text="Remove From Favourites"
                            val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                            btnAddToFav.setBackgroundColor(favColor)
                        }else{
                            btnAddToFav.text="Add to Favourites"
                            val favNoColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                            btnAddToFav.setBackgroundColor(favNoColor)
                        }
                        btnAddToFav.setOnClickListener {
                            if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                val result=async.get()
                                if(result){
                                    Toast.makeText(this@DescriptionActivity,"Book Added to Favourites",Toast.LENGTH_SHORT).show()
                                    btnAddToFav.text="Remove From Favourites"
                                    val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()

                                }

                            }else{
                                val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                val result=async.get()
                                if(result){
                                    Toast.makeText(this@DescriptionActivity,"Book removed from favourites",Toast.LENGTH_SHORT).show()
                                    btnAddToFav.text="Add to Favoutites"
                                    val favNoColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                    btnAddToFav.setBackgroundColor(favNoColor)
                                }else{
                                    Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()

                                }
                            }
                        }

                    }
                    else{
                        Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()

                    }
                }catch(e:Exception){
                    Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener {
                Toast.makeText(this@DescriptionActivity,"Volley error $it",Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="7f89bc837594ad"
                    return headers
                }

            }
            queue.add(jsonRequest)

        }
        else{
            val dialog= AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){
                    text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }


}
    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int): AsyncTask<Void,Void,Boolean>() {
        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null


                }
                2->{
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true

                }
                3->{
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }
            return false
        }
    }
}

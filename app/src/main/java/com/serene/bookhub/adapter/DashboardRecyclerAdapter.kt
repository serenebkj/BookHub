package com.serene.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.serene.bookhub.R
import com.serene.bookhub.activity.DescriptionActivity
import com.serene.bookhub.model.Book
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class DashboardRecyclerAdapter(val context: Context, val itemList:ArrayList<Book>):RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book=itemList[position]
        holder.textBookName.text=book.bookName
        holder.textBookAuthor.text=book.bookAuthor
        holder.textBookPrice.text=book.bookPrice
        holder.textBookRating.text=book.bookRating
        //holder.textBookImage.setImageResource(book.bookImage )
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.textBookImage)
        holder.Parentlayout.setOnClickListener{
            val intent=Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }

    }

    class DashboardViewHolder(view: View):RecyclerView.ViewHolder(view){
        val textBookName:TextView=view.findViewById(R.id.txtNameOfBook)
        val textBookAuthor:TextView=view.findViewById(R.id.txtNamesOfAuthors)
        val textBookPrice:TextView=view.findViewById(R.id.txtPrice)
        val textBookRating:TextView=view.findViewById(R.id.txtRating)
        val textBookImage:ImageView=view.findViewById(R.id.imgBookImage)
        val Parentlayout:LinearLayout=view.findViewById(R.id.Parentlayout)


    }
}
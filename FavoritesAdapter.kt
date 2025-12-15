package com.example.lab5_jennyfer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab5_jennyfer.R
import com.example.lab5_jennyfer.data.FavoriteMedia

// RecyclerView adapter for displaying favorite media items
class FavoritesAdapter(

    // List of favorite media items
    private var items: MutableList<FavoriteMedia>,

    // Callback function for delete button click
    private val onDeleteClick: (FavoriteMedia, Int) -> Unit

) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    // Update adapter data when favorites list changes
    fun update(newItems: List<FavoriteMedia>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    // Inflate the layout for each RecyclerView item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_favorite, parent, false)
        return FavViewHolder(view)
    }

    // Bind data to the RecyclerView item views
    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val item = items[position]

        // Display media URI in TextView
        holder.mediaUriTextView.text = item.uri

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item, position)
        }
    }

    // Return total number of items
    override fun getItemCount(): Int = items.size

    // ViewHolder class to hold item views
    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // TextView displaying media URI
        val mediaUriTextView: TextView =
            itemView.findViewById(R.id.MediaUriTextView)

        // Button used to delete favorite item
        val deleteButton: Button =
            itemView.findViewById(R.id.deleteButton)
    }
}

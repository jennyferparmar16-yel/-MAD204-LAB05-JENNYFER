package com.example.lab5_jennyfer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab5_jennyfer.R
import com.example.lab5_jennyfer.data.FavoriteMedia

class FavoritesAdapter(
    private var items: MutableList<FavoriteMedia>,
    private val onDeleteClick: (FavoriteMedia, Int) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    fun update(newItems: List<FavoriteMedia>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_favorite, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val item = items[position]
        holder.mediaUriTextView.text = item.uri

        holder.deleteButton.setOnClickListener {
            onDeleteClick(item, position)
        }
    }

    override fun getItemCount(): Int = items.size

    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mediaUriTextView: TextView =
            itemView.findViewById(R.id.MediaUriTextView)

        val deleteButton: Button =
            itemView.findViewById(R.id.deleteButton)
    }
}

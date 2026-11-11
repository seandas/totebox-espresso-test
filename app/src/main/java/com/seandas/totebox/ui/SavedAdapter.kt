package com.seandas.totebox.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seandas.totebox.R
import com.seandas.totebox.data.Product
import com.seandas.totebox.databinding.ItemSavedBinding
import com.seandas.totebox.util.formatPrice

/** Rows on the cart screen's "Saved for later" tab. */
class SavedAdapter(
    private val onMoveToCart: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
) : RecyclerView.Adapter<SavedAdapter.Holder>() {

    private var items: List<Pair<Product, Int>> = emptyList()

    fun submit(lines: List<Pair<Product, Int>>) {
        items = lines
        notifyDataSetChanged()
    }

    class Holder(val b: ItemSavedBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val (product, quantity) = items[position]
        val context = holder.itemView.context
        with(holder.b) {
            savedItemName.text = product.name
            savedItemPrice.text = context.getString(R.string.price_each, formatPrice(product.priceCents))
            savedItemQty.text = context.getString(R.string.saved_quantity, quantity)
            savedItemImage.setImageResource(product.imageRes)
            savedItemThumb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(product.colorHex))

            savedItemMove.contentDescription = context.getString(R.string.move_named_to_cart, product.name)
            savedItemDelete.contentDescription = context.getString(R.string.delete_named, product.name)
            savedItemMove.setOnClickListener { onMoveToCart(product) }
            savedItemDelete.setOnClickListener { onDelete(product) }
        }
    }
}

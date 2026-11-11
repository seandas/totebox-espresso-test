package com.seandas.totebox.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seandas.totebox.R
import com.seandas.totebox.data.Cart
import com.seandas.totebox.data.Product
import com.seandas.totebox.databinding.ItemCartBinding
import com.seandas.totebox.util.formatPrice

class CartAdapter(
    private val onQuantityChange: (productId: Int, quantity: Int) -> Unit,
    private val onRemove: (productId: Int) -> Unit,
    private val onSaveForLater: (Product) -> Unit,
) : RecyclerView.Adapter<CartAdapter.Holder>() {

    private var items: List<Pair<Product, Int>> = emptyList()

    fun submit(lines: List<Pair<Product, Int>>) {
        items = lines
        notifyDataSetChanged()
    }

    class Holder(val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val (product, quantity) = items[position]
        val context = holder.itemView.context
        with(holder.b) {
            cartItemName.text = product.name
            cartItemUnitPrice.text = context.getString(R.string.price_each, formatPrice(product.priceCents))
            cartItemQty.text = quantity.toString()
            cartItemLineTotal.text = formatPrice(product.priceCents * quantity)
            cartItemImage.setImageResource(product.imageRes)
            cartItemThumb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(product.colorHex))

            cartItemMinus.isEnabled = quantity > 1
            cartItemPlus.isEnabled = quantity < Cart.MAX_QUANTITY
            cartItemMinus.contentDescription = context.getString(R.string.decrease_named, product.name)
            cartItemPlus.contentDescription = context.getString(R.string.increase_named, product.name)
            cartItemRemove.contentDescription = context.getString(R.string.remove_named, product.name)
            cartItemSave.contentDescription = context.getString(R.string.save_named_for_later, product.name)

            cartItemMinus.setOnClickListener { onQuantityChange(product.id, quantity - 1) }
            cartItemPlus.setOnClickListener { onQuantityChange(product.id, quantity + 1) }
            cartItemRemove.setOnClickListener { onRemove(product.id) }
            cartItemSave.setOnClickListener { onSaveForLater(product) }
        }
    }
}

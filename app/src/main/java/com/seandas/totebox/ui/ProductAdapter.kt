package com.seandas.totebox.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seandas.totebox.R
import com.seandas.totebox.data.Product
import com.seandas.totebox.databinding.ItemProductBinding
import com.seandas.totebox.util.formatPrice

class ProductAdapter(
    private val onOpen: (Product) -> Unit,
    private val onAdd: (Product) -> Unit,
    private val onSave: (Product) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.Holder>() {

    private var items: List<Product> = emptyList()

    fun submit(products: List<Product>) {
        items = products
        notifyDataSetChanged()
    }

    class Holder(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val product = items[position]
        val context = holder.itemView.context
        with(holder.b) {
            productName.text = product.name
            productCategory.text = product.category
            productPrice.text = formatPrice(product.priceCents)
            productImage.setImageResource(product.imageRes)
            productThumb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(product.colorHex))
            root.setOnClickListener { onOpen(product) }
            addButton.setOnClickListener { onAdd(product) }
            addButton.contentDescription = context.getString(R.string.add_named_to_cart, product.name)
            // The crash test item has no price and can't be saved
            productPrice.visibility = if (product.isCrashTest) View.GONE else View.VISIBLE
            saveButton.visibility = if (product.isCrashTest) View.GONE else View.VISIBLE
            saveButton.setOnClickListener { onSave(product) }
            saveButton.contentDescription = context.getString(R.string.save_named_for_later, product.name)
        }
    }
}

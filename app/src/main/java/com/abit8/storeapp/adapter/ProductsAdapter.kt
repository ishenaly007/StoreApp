package com.abit8.storeapp.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abit8.storeapp.R
import com.abit8.storeapp.databinding.ItemProductBinding
import com.abit8.storeapp.model.Product
import com.bumptech.glide.Glide

class ProductsAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun submitList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemProductBinding

        init {
            binding = ItemProductBinding.bind(itemView)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = products[position]
                    onItemClick(product)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = "${product.price}$"
            Glide.with(binding.productIv1)
                .load(product.image)
                .into(binding.productIv1)
        }
    }
}


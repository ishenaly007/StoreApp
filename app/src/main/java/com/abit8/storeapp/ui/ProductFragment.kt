package com.abit8.storeapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abit8.storeapp.data.db.AppDatabase
import com.abit8.storeapp.databinding.FragmentProductBinding
import com.abit8.storeapp.repository.StoreRepository
import com.abit8.storeapp.viewmodel.ProductViewModel
import com.abit8.storeapp.viewmodel.ProductViewModelFactory
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(createStoreRepository())
    }
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var storeRepository: StoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getInstance(requireContext())
        storeRepository = createStoreRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getLong("productId")

        if (isNetworkAvailable(requireContext())) {
            // Если есть доступ к интернету, загрузить продукт из сети
            viewLifecycleOwner.lifecycleScope.launch {
                productId?.let { viewModel.fetchProductById(it) }
            }
        } else {
            // Если нет доступа к интернету, получить продукт из бд
            productId?.let { loadProductFromCache(it) }
        }

        viewModel.product.observe(viewLifecycleOwner) { product ->
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = "${product.price}$"
            binding.tvProductDesc.text = product.description
            Glide.with(this).load(product.image).into(binding.productIv)
        }
        //сетт
        binding.btnShare.setOnClickListener {
            val product = viewModel.product.value
            if (product != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out this product: ${product.title}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        }
    }

    private fun loadProductFromCache(productId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val cachedProduct = storeRepository.getProductByIdFromCache(productId)
            viewModel.setProduct(cachedProduct)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.activeNetwork?.let {
                connectivityManager.getNetworkCapabilities(it)
            }
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createStoreRepository(): StoreRepository {
        return StoreRepository(database)
    }
}




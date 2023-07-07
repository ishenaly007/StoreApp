package com.abit8.storeapp.ui


import android.content.Context
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abit8.storeapp.adapter.ProductsAdapter
import com.abit8.storeapp.data.db.AppDatabase
import com.abit8.storeapp.databinding.FragmentProductsBinding
import com.abit8.storeapp.model.Product
import com.abit8.storeapp.repository.StoreRepository
import com.abit8.storeapp.viewmodel.ProductsViewModel
import com.abit8.storeapp.viewmodel.ProductsViewModelFactory
import kotlinx.coroutines.launch

class ProductsFragment : Fragment() {
    private val viewModel: ProductsViewModel by viewModels {
        ProductsViewModelFactory(createStoreRepository())
    }
    private var _binding: FragmentProductsBinding? = null
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
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productsAdapter = ProductsAdapter(emptyList()) { product ->
            onProductClick(product)
        }

        binding.productsRecyclerView.apply {
            adapter = productsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val category = arguments?.getString("category")
        val isNetworkAvailable = isNetworkAvailable(requireContext())

        val listener = object : StoreRepository.OnProductsSavedListener {
            override fun onProductsSaved() {
                // Получение продуктов из кэша после сохранения
                viewLifecycleOwner.lifecycleScope.launch {
                    category?.let {
                        viewModel.getProductsFromCacheByCategory(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            category?.let {
                if (isNetworkAvailable) {
                    viewModel.fetchProductsByCategory(it, null, listener)
                } else {
                    viewModel.fetchProductsByCategory(it, null)
                }
            }
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            productsAdapter.submitList(products)
        }
    }

    private fun onProductClick(product: Product) {
        val action = ProductsFragmentDirections.actionProductsFragmentToProductFragment(
            product.id
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createStoreRepository(): StoreRepository {
        return StoreRepository(database)
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
}

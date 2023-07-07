package com.abit8.storeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abit8.storeapp.adapter.CategoriesAdapter
import com.abit8.storeapp.data.db.AppDatabase
import com.abit8.storeapp.databinding.FragmentCategoriesBinding
import com.abit8.storeapp.model.Category
import com.abit8.storeapp.repository.StoreRepository
import com.abit8.storeapp.viewmodel.CategoriesViewModel
import com.abit8.storeapp.viewmodel.CategoriesViewModelFactory
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {
    private val viewModel: CategoriesViewModel by viewModels {
        CategoriesViewModelFactory(createStoreRepository())
    }
    private var _binding: FragmentCategoriesBinding? = null
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
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesAdapter = CategoriesAdapter(emptyList()) { category ->
            onCategoryClick(category)
        }

        binding.rvCategories.apply {
            adapter = categoriesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        //тоже
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.categories = categories
            categoriesAdapter.notifyDataSetChanged()
        }

        //обновление и просмотр и вставка
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchCategories()
        }
    }

    private fun onCategoryClick(category: Category) {
        val action = CategoriesFragmentDirections.actionCategoriesFragmentToProductsFragment(
            category.name
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //чтобы красным не горело, а так горит же без него
    private fun createStoreRepository(): StoreRepository {
        return StoreRepository(database)
    }
}




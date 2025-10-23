package screen.store_gallery

import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentStoreGalleryBinding
import model.CategoryType
import model.Product
typealias drawable = R.drawable
class StoreGalleryFragment : BaseFragment<FragmentStoreGalleryBinding>(FragmentStoreGalleryBinding::inflate) {

    private lateinit var filtersAdapter: FilterAdapter
    private lateinit var productAdapter: ProductAdapter

    private lateinit var allProducts: List<Product>

    override fun bind() {
        setupProducts()
        setupFiltersRecyclerView()
        setupProductsRecyclerView()
    }

    private fun setupProducts() {
        allProducts = listOf(
            Product(drawable.woman_black_leather_jacket, getString(R.string.product_name_black_jacket), getString(R.string.product_price_black_jacket), CategoryType.CASUAL),
            Product(drawable.woman_yellow_jacket, getString(R.string.product_name_yellow_hoodie), getString(R.string.product_price_yellow_hoodie), CategoryType.CASUAL),
            Product(drawable.woman_blue_hoodie, getString(R.string.product_name_blue_hoodie), getString(R.string.product_price_blue_hoodie), CategoryType.CASUAL),
            Product(drawable.woman_red_hoodie, getString(R.string.product_name_red_hoodie), getString(R.string.product_price_red_hoodie), CategoryType.CASUAL),
            Product(drawable.man_hiker, getString(R.string.product_name_hiking_equipment), getString(R.string.product_price_hiking_equipment), CategoryType.CAMPING),
            Product(drawable.man_rugby_uniform, getString(R.string.product_name_rugby_uniform), getString(R.string.product_price_rugby_uniform), CategoryType.SPORT),
            Product(drawable.woman_fancy_blue_dress, getString(R.string.product_name_blue_dress), getString(R.string.product_price_blue_dress), CategoryType.PARTY),
            Product(drawable.man_in_a_suit, getString(R.string.product_name_kostumchik), getString(R.string.product_price_kostumchik), CategoryType.FANCY)
        )
    }

    private fun setupFiltersRecyclerView() {
        val buttons = listOf(
            FilterButton(getString(R.string.filter_all), isSelected = true),
            FilterButton(getString(R.string.filter_party), category = CategoryType.PARTY),
            FilterButton(getString(R.string.filter_camping), category = CategoryType.CAMPING),
            FilterButton(getString(R.string.filter_sports), category = CategoryType.SPORT),
            FilterButton(getString(R.string.filter_casual), category = CategoryType.CASUAL),
            FilterButton(getString(R.string.filter_fancy), category = CategoryType.FANCY)
        )

        filtersAdapter = FilterAdapter { clickedButton ->
            val newList = filtersAdapter.currentList.map {
                it.copy(isSelected = it.text == clickedButton.text)
            }
            filtersAdapter.submitList(newList)
            filterProducts(clickedButton.category)
        }

        binding.filtersRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filtersAdapter
        }
        filtersAdapter.submitList(buttons)
    }

    private fun setupProductsRecyclerView() {
        productAdapter = ProductAdapter()
        binding.productsRecyclerView.adapter = productAdapter
        productAdapter.submitList(allProducts)
    }

    private fun filterProducts(category: CategoryType?) {
        val filteredList = if (category == null) {
            allProducts
        } else {
            allProducts.filter { it.category == category }
        }
        productAdapter.submitList(filteredList)
    }
}

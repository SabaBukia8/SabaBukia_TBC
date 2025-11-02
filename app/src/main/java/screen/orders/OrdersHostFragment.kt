package screen.orders

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentOrdersHostBinding
import com.google.android.material.tabs.TabLayoutMediator
import model.OrderStatus

class OrdersHostFragment :
    BaseFragment<FragmentOrdersHostBinding>(FragmentOrdersHostBinding::inflate) {

    override fun bind() = with(binding) {
        val pagerAdapter = object : FragmentStateAdapter(this@OrdersHostFragment) {
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> OrdersListFragment.newInstance(OrderStatus.ACTIVE)
                else -> OrdersListFragment.newInstance(OrderStatus.COMPLETED)
            }
        }
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(com.example.sababukia_tbc.R.string.status_active)
                else -> getString(com.example.sababukia_tbc.R.string.status_completed)
            }
        }.attach()
    }
}

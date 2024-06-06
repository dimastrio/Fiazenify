package id.dimas.fiazenify

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.ActivityMainBinding
import id.dimas.fiazenify.ui.main.home.HomeFragment
import id.dimas.fiazenify.ui.main.planner.AddPlanningFragment
import id.dimas.fiazenify.ui.main.planner.PlanningFragment
import id.dimas.fiazenify.ui.main.profile.ProfileFragment
import id.dimas.fiazenify.ui.main.statistics.StatisticFragment
import id.dimas.fiazenify.ui.main.transaction.AddTransactionFragment
import id.dimas.fiazenify.ui.main.transaction.ListTransactionFragment
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    private val profileFragment = ProfileFragment()
    private val homeFragment = HomeFragment()

    //    private val addBottomSheet = AddBottomSheet()
    private val planningFragment = PlanningFragment()
    private val addTransactionFragment = AddTransactionFragment()
    private val addPlanningFragment = AddPlanningFragment()
    private val statisticFragment = StatisticFragment()
    private val listTransactionFragment = ListTransactionFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setupBottomNavigationBar()
        setLightStatusBar(window.decorView, true)

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setLightStatusBar(window.decorView, true)
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            setVisibilityBottomNav(false)
        } else if (supportFragmentManager.backStackEntryCount > 0 && !homeFragment.isHidden) {
            supportFragmentManager.popBackStack()
            setVisibilityBottomNav(true)
            homeFragment.refresh()
            statisticFragment.refresh()
        } else if (supportFragmentManager.backStackEntryCount > 0 && !planningFragment.isHidden) {
            supportFragmentManager.popBackStack()
            setVisibilityBottomNav(true)
            planningFragment.refresh()
            setTabStateFragment(TabState.PLANNING).commit()
            binding.bottomNavMenu.menu.findItem(R.id.navigation_planning).isChecked = true
        } else if (supportFragmentManager.backStackEntryCount > 0 && (addTransactionFragment.isHidden)) {
            supportFragmentManager.popBackStack()
        } else if (supportFragmentManager.backStackEntryCount > 0 || !homeFragment.isHidden) {
            super.onBackPressed()
            setVisibilityBottomNav(true)
        } else {
            homeFragment.refresh()
            statisticFragment.refresh()
            addTransactionFragment.clearView()
            setVisibilityBottomNav(true)
            setTabStateFragment(TabState.HOME).commit()
            binding.bottomNavMenu.menu.findItem(R.id.navigation_home).isChecked = true
        }


//        if (supportFragmentManager.backStackEntryCount > 1) {
//            supportFragmentManager.popBackStack()
//            setVisibilityBottomNav(false)
//        } else if (supportFragmentManager.backStackEntryCount > 0 && !homeFragment.isHidden) {
//            supportFragmentManager.popBackStack()
//            setVisibilityBottomNav(true)
//        } else if (supportFragmentManager.backStackEntryCount > 0 && (addTransaction.isHidden && profileFragment.isHidden)) {
//            supportFragmentManager.popBackStack()
//        } else if (supportFragmentManager.backStackEntryCount > 0 && homeFragment.isHidden) {
//            supportFragmentManager.popBackStack()
////            addTransaction.clearView()
//            addTransaction.clearView()
//            setVisibilityBottomNav(true)
//            setTabStateFragment(TabState.PROFILE).commit()
//            binding.bottomNavMenu.menu.findItem(R.id.navigation_profile).isChecked = true
//        } else if (supportFragmentManager.backStackEntryCount > 0 || !homeFragment.isHidden) {
//            super.getOnBackPressedDispatcher().onBackPressed()
//            setVisibilityBottomNav(true)
//        } else if(supportFragmentManager.backStackEntryCount > 0 && planningFragment.isHidden) {
//            supportFragmentManager.popBackStack()
//            setVisibilityBottomNav(true)
//            setTabStateFragment(TabState.PLANNING).commit()
//            binding.bottomNavMenu.menu.findItem(R.id.navigation_planning).isChecked = true
//        } else {
////            addTransaction.clearView()
//            addTransaction.clearView()
//            setVisibilityBottomNav(true)
//            setTabStateFragment(TabState.HOME).commit()
//            binding.bottomNavMenu.menu.findItem(R.id.navigation_home).isChecked = true
//        }
    }


    private fun setupBottomNavigationBar() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_nav_host, homeFragment)
            .add(R.id.main_nav_host, planningFragment)
            .add(R.id.main_nav_host, addTransactionFragment)
//            .add(R.id.main_nav_host, addTransaction)
            .add(R.id.main_nav_host, profileFragment)
//            .add(R.id.main_nav_host, addBottomSheet)
            .add(R.id.main_nav_host, statisticFragment)
            .commit()

        setTabStateFragment(TabState.HOME).commit()

        binding.bottomNavMenu.background = null
        binding.bottomNavMenu.menu.getItem(4).isEnabled = false
        binding.bottomNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    setTabStateFragment(TabState.HOME).commit()
                }

                R.id.navigation_planning -> {
                    setTabStateFragment(TabState.PLANNING).commit()
                }

                R.id.navigation_statistic -> {
                    setTabStateFragment(TabState.STATISTIC).commit()
                }

                R.id.navigation_profile -> {
                    setTabStateFragment(TabState.PROFILE).commit()
                }
            }
            true
        }
        binding.bottomNavMenu.menu.findItem(R.id.navigation_home).isChecked = true

        binding.fab.setOnClickListener {
            setTabStateFragment(TabState.ADDTRANSACTION).commit()
        }

    }

    private fun setTabStateFragment(state: TabState): FragmentTransaction {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
        setVisibilityBottomNav(true)
        when (state) {
            TabState.HOME -> {
                transaction.show(homeFragment)
                transaction.hide(planningFragment)
                transaction.hide(statisticFragment)
//                transaction.hide(historyFragment)
                transaction.hide(profileFragment)
                transaction.hide(addTransactionFragment)
            }

            TabState.PLANNING -> {
                transaction.hide(homeFragment)
                transaction.show(planningFragment)
                transaction.hide(statisticFragment)
//                transaction.hide(historyFragment)
                transaction.hide(profileFragment)
                transaction.hide(addTransactionFragment)
            }

            TabState.ADDTRANSACTION -> {
                transaction.hide(homeFragment)
                transaction.hide(planningFragment)
                transaction.hide(statisticFragment)
//                transaction.hide(historyFragment)
                transaction.hide(profileFragment)
                transaction.show(addTransactionFragment)
                setVisibilityBottomNav(false)
            }

            TabState.STATISTIC -> {
                transaction.hide(homeFragment)
                transaction.hide(planningFragment)
                transaction.show(statisticFragment)
//                transaction.show(historyFragment)
                transaction.hide(profileFragment)
                transaction.hide(addTransactionFragment)
            }

            TabState.PROFILE -> {
                transaction.hide(homeFragment)
                transaction.hide(planningFragment)
                transaction.hide(statisticFragment)
//                transaction.hide(historyFragment)
                transaction.show(profileFragment)
            }
        }
        return transaction
    }

    private fun setLightStatusBar(view: View, isLight: Boolean) {
        ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = isLight
    }

    private fun setVisibilityBottomNav(isVisible: Boolean) {
        binding.bottomAppBar.isVisible = isVisible
        if (isVisible) binding.fab.show() else binding.fab.hide()
    }

    private enum class TabState {
        HOME,
        PLANNING,
        ADDTRANSACTION,
        STATISTIC,
        PROFILE,
    }
}
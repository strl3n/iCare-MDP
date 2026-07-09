package com.istts.finalproject.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.istts.finalproject.R
import com.istts.finalproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    // Layar-layar yang tidak menampilkan toolbar & bottom navigation (alur auth)
    private val authDestinations = setOf(R.id.loginFragment, R.id.registerFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Wajib dipanggil sebelum setupActionBarWithNavController(),
        // kalau tidak app akan crash (theme-nya NoActionBar sehingga
        // supportActionBar masih null tanpa toolbar ini).
        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.nav

        // Ambil NavController langsung dari NavHostFragment lewat FragmentManager,
        // BUKAN lewat findNavController(viewId). Cara findNavController(viewId) mencari
        // NavController lewat tag di View hierarchy dan bisa gagal ("does not have a
        // NavController set") tergantung timing attach fragment. Ambil dari
        // FragmentManager selalu aman karena tidak bergantung pada urutan render View.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_mood_tracker,
                R.id.navigation_mood_history,
                R.id.navigation_articles,
                R.id.navigation_emergency
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Sembunyikan toolbar & bottom nav saat di layar login/register
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen = destination.id in authDestinations
            binding.toolbar.visibility = if (isAuthScreen) View.GONE else View.VISIBLE
            navView.visibility = if (isAuthScreen) View.GONE else View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_profile) {
            navController.navigate(R.id.navigation_profile)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

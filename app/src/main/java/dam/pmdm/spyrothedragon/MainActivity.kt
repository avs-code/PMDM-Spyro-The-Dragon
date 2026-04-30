package dam.pmdm.spyrothedragon

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private var currentGuideScreen = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)

        navHostFragment?.let {
            navController = NavHostFragment.findNavController(it)
            NavigationUI.setupWithNavController(binding.navView, navController!!)
            NavigationUI.setupActionBarWithNavController(this, navController!!)
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            selectedBottomMenu(menuItem)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_characters,
                R.id.navigation_worlds,
                R.id.navigation_collectibles -> {
                    // En las pantallas de los tabs no mostramos la flecha atrás
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    // En el resto de pantallas sí
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }
        val prefs = getSharedPreferences("guide", MODE_PRIVATE)
        val shown = prefs.getBoolean("shown", false)

        if (!shown) {
            showGuideScreen(R.layout.guide_screen_1)
        }
    }

    private fun selectedBottomMenu(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_characters ->
                navController?.navigate(R.id.navigation_characters)
            R.id.nav_worlds ->
                navController?.navigate(R.id.navigation_worlds)
            else ->
                navController?.navigate(R.id.navigation_collectibles)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_info) {
            showInfoDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.text_about)
            .setPositiveButton(R.string.accept, null)
            .show()
    }

    fun showGuideScreen(layoutId: Int) {
        val container = findViewById<FrameLayout>(R.id.guideOverlayContainer)
        container.removeAllViews()

        val view = layoutInflater.inflate(layoutId, container, false)
        container.addView(view)

        container.visibility = View.VISIBLE

        // Botón siguiente
        view.findViewById<View?>(R.id.btnSiguiente)?.setOnClickListener {
            nextGuideScreen()
        }

        // Botón omitir
        view.findViewById<View?>(R.id.btnOmitir)?.setOnClickListener {
            endGuide()
        }
    }

    fun hideGuide() {
        findViewById<FrameLayout>(R.id.guideOverlayContainer).visibility = View.GONE
    }

    fun nextGuideScreen() {
        currentGuideScreen++

        when (currentGuideScreen) {
            2 -> showGuideScreen(R.layout.guide_screen_2)
            3 -> showGuideScreen(R.layout.guide_screen_3)
            4 -> showGuideScreen(R.layout.guide_screen_4)
            5 -> showGuideScreen(R.layout.guide_screen_5)
            6 -> showGuideScreen(R.layout.guide_screen_6)
            else -> endGuide()
        }
    }
    fun endGuide() {
        val prefs = getSharedPreferences("guide", MODE_PRIVATE)
        prefs.edit().putBoolean("shown", true).apply()
        hideGuide()
    }
}

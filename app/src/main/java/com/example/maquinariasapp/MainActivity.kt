package com.example.maquinariasapp

import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.maquinariasapp.ui.linProduccion.LineaProduccionFragment
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaFragment
import com.example.maquinariasapp.ui.planProduccion.PlantaProduccionFragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(MaestroMaquinariaFragment())
                }
                R.id.nav_gallery -> {
                    loadFragment(LineaProduccionFragment())
                }
                R.id.nav_slideshow -> {
                    loadFragment(PlantaProduccionFragment())
                }
                R.id.nav_logout -> {
                    backToLogin()
                }
            }
            drawerLayout.closeDrawers() // Cierra el drawer después de la selección
            true
        }

        // Carga el fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(MaestroMaquinariaFragment())
        }

    }

    private fun backToLogin() {
        setResult(RESULT_OK)
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
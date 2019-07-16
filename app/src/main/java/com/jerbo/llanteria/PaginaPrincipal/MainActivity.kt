package com.jerbo.llanteria.PaginaPrincipal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.jerbo.llanteria.Clientes.AddCliente
import com.jerbo.llanteria.R
import com.jerbo.llanteria.Venta.AddVenta
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var resultados: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val venta: TextView = findViewById(R.id.venta)
        val cliente: TextView = findViewById(R.id.cliente)
        val ctx: Context = this
        venta.setOnClickListener {
            val intent = Intent(ctx, AddVenta::class.java)
            startActivity(intent)
            finish()
        }
        cliente.setOnClickListener {
            val intent = Intent(ctx, AddCliente::class.java)
            startActivity(intent)
            finish()
        }
        link_to_web.setOnClickListener{
            val url = Uri.parse("http://198.23.255.30/~u20180584/facturacion/")
            startActivity(Intent(Intent.ACTION_VIEW,url))
        }


    }




    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
               startActivity(Intent(this,AddVenta::class.java))
                finish()
            }
            R.id.nav_gallery -> {
                startActivity(Intent(this,AddCliente::class.java))
                finish()
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

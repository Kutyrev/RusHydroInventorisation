package com.github.kutyrev.rushydroinventorisation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.github.kutyrev.rushydroinventorisation.databinding.ActivityMainBinding
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        RetrofitClient.initSettings(prefs.getString("service_login", "").toString(),
            prefs.getString("service_pass", "").toString())

        Common.base_url = prefs.getString("server_addr", "").toString()
        Common.db_name = prefs.getString("db_name", "").toString()

        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, s ->
            if(s == "service_login"){
                RetrofitClient.initSettings(sharedPreferences.getString("service_login", "").toString(),
                    prefs.getString("service_pass", "").toString())
            }
            if(s == "service_pass"){
                RetrofitClient.initSettings(sharedPreferences.getString("service_login", "").toString(),
                    prefs.getString("service_pass", "").toString())
            }
            if(s == "server_addr"){
                Common.base_url =  prefs.getString("server_addr", "").toString()
            }

            if(s == "db_name"){
                Common.db_name = prefs.getString("db_name", "").toString()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings ->  {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.open_settings_fragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
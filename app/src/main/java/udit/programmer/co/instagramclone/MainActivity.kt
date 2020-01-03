package udit.programmer.co.instagramclone

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import udit.programmer.co.instagramclone.Fragments.NotificationFragment
import udit.programmer.co.instagramclone.Fragments.ProfileFragment
import udit.programmer.co.instagramclone.Fragments.SearchFragment
import udit.programmer.co.instagramclone.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.nav_home -> {
                    movetofrag(udit.programmer.co.instagramclone.Fragments.HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_search -> {
                    movetofrag(SearchFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_add_post -> {
                    it.isChecked = false
                    startActivity(Intent(this@MainActivity, AddNewPostActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_notifications -> {
                    movetofrag(NotificationFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.nav_profile -> {
                    movetofrag(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }

            }

            false

        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        movetofrag(udit.programmer.co.instagramclone.Fragments.HomeFragment())

        /*val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/
    }

    private fun movetofrag(fragment: Fragment) {

        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_cntainer, fragment)
        fragmentTrans.commit()
    }
}

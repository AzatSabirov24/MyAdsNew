package com.azat_sabirov.myAdsNew.act

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.adapters.AdsRcAdapter
import com.azat_sabirov.myAdsNew.databinding.ActivityMainBinding
import com.azat_sabirov.myAdsNew.dialogHelper.DialogConstants
import com.azat_sabirov.myAdsNew.dialogHelper.DialogHelper
import com.azat_sabirov.myAdsNew.dialogHelper.GoogleAccConst
import com.azat_sabirov.myAdsNew.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    lateinit var tvAccount: TextView
    private val adsRcAdapter = AdsRcAdapter(mAuth)
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        iniRecyclerView()
        initViewModel()
        firebaseViewModel.loadAllAds()
        bottomNavItemOnClick()
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.botNavView.selectedItemId = R.id.home
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    dialogHelper.accHelper.signToFireBaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        setSupportActionBar(binding.mainContent.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.mainContent.toolbar,
            R.string.open,
            R.string.close
        )
        toggle.syncState()
        binding.drawerLayout.addDrawerListener(toggle)
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this, {
            adsRcAdapter.updateAdapter(it)
        })
    }

    private fun bottomNavItemOnClick() = with(binding) {
        mainContent.botNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)
                }
                R.id.my_ads -> {
                    Toast.makeText(this@MainActivity, "MyAds", Toast.LENGTH_SHORT).show()
                }
                R.id.fav_ads -> {
                    Toast.makeText(this@MainActivity, "Favourite Ads", Toast.LENGTH_SHORT).show()
                }
                R.id.home -> {
                    Toast.makeText(this@MainActivity, "Home", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun iniRecyclerView() = with(binding) {
        mainContent.rcView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adsRcAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_my_ads -> {
                Toast.makeText(this, "my_ads", Toast.LENGTH_SHORT).show();
            }
            R.id.id_car -> {

            }
            R.id.id_pc -> {

            }
            R.id.id_smart -> {

            }
            R.id.id_dm -> {

            }
            R.id.sign_up -> {
                dialogHelper.createSignDialog(DialogConstants.SIGN_UP_DIALOG_STATE)
                uiUpdate(mAuth.currentUser)
                Log.d("MyLog", "OK")
            }
            R.id.sign_in -> {
                dialogHelper.createSignDialog(DialogConstants.SIGN_IN_DIALOG_STATE)
                uiUpdate(mAuth.currentUser)

            }
            R.id.sign_out -> {
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutG()

            }

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else user.email
    }
}
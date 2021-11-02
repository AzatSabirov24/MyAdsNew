package com.azat_sabirov.myAdsNew.act

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.adapters.AdsRcAdapter
import com.azat_sabirov.myAdsNew.data.Ad
import com.azat_sabirov.myAdsNew.databinding.ActivityMainBinding
import com.azat_sabirov.myAdsNew.db.DBManager
import com.azat_sabirov.myAdsNew.db.ReadDataCallback
import com.azat_sabirov.myAdsNew.dialogHelper.DialogConstants
import com.azat_sabirov.myAdsNew.dialogHelper.DialogHelper
import com.azat_sabirov.myAdsNew.dialogHelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ReadDataCallback {
    lateinit var rootElement: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    lateinit var tvAccount: TextView
    private val dbManager = DBManager(this)
    private val adsRcAdapter = AdsRcAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()
        iniRecyclerView()
        dbManager.readDataFromDb()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.id_new_ads){
            val i = Intent(this, EditAdsAct::class.java)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
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
        setSupportActionBar(rootElement.mainContent.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            rootElement.drawerLayout,
            rootElement.mainContent.toolbar,
            R.string.open,
            R.string.close
        )
        toggle.syncState()
        rootElement.drawerLayout.addDrawerListener(toggle)
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    private fun iniRecyclerView() = with(rootElement){
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
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else user.email
    }

    override fun readData(list: List<Ad>) {
        adsRcAdapter.updateAdapter(list)
    }
}
package com.lado.travago.transpido.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lado.travago.transpido.R
import com.lado.travago.transpido.repo.State
import com.lado.travago.transpido.repo.firebase.FirestoreRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreRepo

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setTheme(R.style.SplashTheme)

        CoroutineScope(Dispatchers.Main).launch {
            splash()
        }
    }

    /**
     * Depending on if the user is an authenticated scanner or not, it redirects to the corresponding screen
     */
    private suspend fun splash(){
        auth = FirebaseAuth.getInstance()
        db = FirestoreRepo()

        when(auth.currentUser){
            //If no user is found, we redirect the user to the Search screen
            null  -> startActivity(Intent(this, MainActivity::class.java))
            else ->{
                if(auth.currentUser!!.isAnonymous){
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                }else {
                    db.identifyUser(auth.currentUser!!.uid).collect {
                        when(it){
                            is State.Failed ->{
                                //Is a Booker, we redirect later to the ticket screen
                            }
                            is State.Success ->{
                                //Is a Scanner, Redirect to the Scanner panel
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.lado.travago.tripbook.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.repo.State
import com.lado.travago.tripbook.repo.firebase.FirestoreRepo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
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
            null  -> startActivity(Intent(this, BookerActivity::class.java))
            else ->{
                if(auth.currentUser!!.isAnonymous){
                    auth.signOut()
                    startActivity(Intent(this, BookerActivity::class.java))
                }else {
                    db.identifyUser(auth.currentUser!!.uid).collect {
                        when(it){
                            is State.Failed ->{
                                Toast.makeText(applicationContext, "Its a Booker!", Toast.LENGTH_LONG).show()
                                //Is a Booker, we redirect later to the ticket screen
                            }
                            is State.Success ->{
                                Toast.makeText(applicationContext, "Its a Booker!", Toast.LENGTH_LONG).show()
                                //Is a Scanner, Redirect to the Scanner panel
                            }
                        }
                    }
                }
            }
        }
    }
}
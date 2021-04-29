package com.azat_sabirov.myAdsNew.accountHelper

import android.util.Log
import android.widget.Toast
import com.azat_sabirov.ads.constants.FireBaseAuthConstants
import com.azat_sabirov.myAdsNew.MainActivity
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.dialogHelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccountHelper(private val act: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        act.uiUpdate(task.result?.user)
                    } else {
//                        Log.d("MyLog", "Exception: ${task.exception}")
                        if(task.exception is FirebaseAuthInvalidUserException){
                            val exception = task.exception as FirebaseAuthInvalidUserException
                            Log.d("MyLog", "Exception: ${exception.errorCode}")

                        }
                        else if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            if (exception.errorCode == FireBaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                                linkEmailWithGoogle(email, password)
                               /* Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE,
                                    Toast.LENGTH_LONG
                                ).show()*/
                            }
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == FireBaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception = task.exception as FirebaseAuthWeakPasswordException
                            if (exception.errorCode == FireBaseAuthConstants.ERROR_WEAK_PASSWORD) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_WEAK_PASSWORD,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.uiUpdate(task.result?.user)
                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.success_sign_in),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException){
                        val exception = task.exception as FirebaseAuthInvalidUserException
                        if (exception.errorCode == FireBaseAuthConstants.ERROR_USER_NOT_FOUND){
                            Toast.makeText(act, R.string.sign_up_or_sign_in_with_google, Toast.LENGTH_LONG).show()
                        }
                    }

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val exception =
                            task.exception as FirebaseAuthInvalidCredentialsException
                        Log.d("MyLog", exception.errorCode)
                        if (exception.errorCode == FireBaseAuthConstants.ERROR_INVALID_EMAIL) {
                            Toast.makeText(
                                act,
                                FireBaseAuthConstants.ERROR_INVALID_EMAIL,
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (exception.errorCode == FireBaseAuthConstants.ERROR_WRONG_PASSWORD) {
                            Toast.makeText(
                                act,
                                FireBaseAuthConstants.ERROR_WRONG_PASSWORD,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, R.string.send_verification_done, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act, R.string.send_verification_email_error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle() {
        val signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signOutG(){
        getSignInClient().signOut()
    }

    fun signToFireBaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user)
            }
        }
    }

    private fun linkEmailWithGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(act, R.string.link_done, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(act, R.string.enter_to_g, Toast.LENGTH_LONG).show()
        }
    }


}


package com.azat_sabirov.myAdsNew.dialogHelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.azat_sabirov.myAdsNew.MainActivity
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.accountHelper.AccountHelper
import com.azat_sabirov.myAdsNew.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {

    val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        val dialog = builder.create()
        stateOfDialog(index, rootDialogElement)

        rootDialogElement.btSign.setOnClickListener {
            stateOfViews(index, rootDialogElement, dialog)
        }

        rootDialogElement.btResetPass.setOnClickListener {
            resetPassword(rootDialogElement, dialog)
        }

        rootDialogElement.btSignGoogle.setOnClickListener {
            accHelper.signInWithGoogle()
            dialog.dismiss()
        }
        dialog.show()

    }


    private fun stateOfViews(
        index: Int,
        rootDialogElement: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        if (index == DialogConstants.SIGN_UP_DIALOG_STATE) {
            accHelper.signUpWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        } else {
            accHelper.signInWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        }
        dialog?.dismiss()
    }

    private fun stateOfDialog(index: Int, rootDialogElement: SignDialogBinding) {
        if (index == DialogConstants.SIGN_UP_DIALOG_STATE) {
            rootDialogElement.btSign.text = act.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.btSign.text = act.resources.getString(R.string.sign_in_action)
            rootDialogElement.tvSignUp.text = act.resources.getString(R.string.ac_sign_in)
            rootDialogElement.btResetPass.visibility = View.VISIBLE
        }
    }

    private fun resetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        if (rootDialogElement.edSignEmail.text.isNotEmpty()) {
            act.mAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            act,
                            R.string.email_reset_password_was_send,
                            Toast.LENGTH_LONG
                        ).show()
                        dialog?.dismiss()
                    }
                }
        }
    }


}
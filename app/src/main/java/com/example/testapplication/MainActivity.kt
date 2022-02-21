package com.example.testapplication

import android.app.Activity
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.testapplication.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.groups.visibility = View.INVISIBLE
        binding.google.visibility = View.VISIBLE

        oneTapClient = Identity.getSignInClient(baseContext)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.cliend_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

//        val googleSignIn = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestIdToken(getString(R.string.cliend_id))
//            .build()
//
//        val gclient = GoogleSignIn.getClient(this, googleSignIn)


         val result = this.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
             if (it.resultCode == Activity.RESULT_OK) {
                 Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
                 var signInCredential = oneTapClient.getSignInCredentialFromIntent(it.data)
//                 handleSignIn(task)
                 updateUI(signInCredential)
             } else {
                 Toast.makeText(applicationContext, "failed", Toast.LENGTH_SHORT).show()
             }
         }

        binding.google.setOnClickListener{
//            val intenet = gclient.signInIntent
//            result.launch(intenet)
            print("asdf")
            oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(this) {
                try {
                    val intent = it.pendingIntent.intentSender
                    val intentRequest = IntentSenderRequest.Builder(intent).build()
                    result.launch(intentRequest)
                } catch (exception: IntentSender.SendIntentException){
                    Log.d("maileee", exception.toString())
            } }
                .addOnFailureListener {
                    Log.d("maile", it.localizedMessage)
                    Toast.makeText(applicationContext, "No QBurst mail found", Toast.LENGTH_SHORT).show()

                }
        }

        binding.logout.setOnClickListener{
            oneTapClient.signOut().addOnCompleteListener {
                if (it.isComplete) {
                    binding.groups.visibility = View.INVISIBLE
                    binding.google.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun handleSignIn(task: SignInCredential) {
        try {
//            updateUI(account)
            Log.d("mail", task.displayName.toString())

        } catch (e: ApiException) {
            Toast.makeText(applicationContext, "failied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(credential: SignInCredential) {
//        val vt = arrayOf("vt1", "vt2", "vt3")
//        AlertDialog.Builder(baseContext)
//            .setTitle("Select yout VT")
//            .
//
        binding.groups.visibility = View.VISIBLE
        binding.name.text = credential.displayName
        binding.google.visibility = View.INVISIBLE
        binding.token.text = credential.googleIdToken
    }
}
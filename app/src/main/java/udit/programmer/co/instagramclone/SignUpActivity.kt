package udit.programmer.co.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signin_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signup_btn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {

        val fullname = fullname_signup.text.toString()
        val username = username_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()

        when {
            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "fullname is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(username) -> Toast.makeText(
                this,
                "username is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "email is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "password is required",
                Toast.LENGTH_SHORT
            ).show()

            else -> {

                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveUserInfo(fullname, username, email, progressDialog)
                        } else {
                            val message = it.exception.toString()
                            Toast.makeText(this, "Error : $message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(
        fullname: String,
        username: String,
        email: String,
        progressDialog: ProgressDialog
    ) {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val usermap = HashMap<String, Any>()

        usermap["uid"] = currentUserId
        usermap["fullname"] = fullname.toLowerCase()
        usermap["username"] = username.toLowerCase()
        usermap["email"] = email
        usermap["Bio"] = "Hey! i am using Instagram"
        usermap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/instagram-clone-app-6ddd5.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=0159ca77-9221-4540-be07-1f7d0d962c3b"

        usersRef.child(currentUserId).setValue(usermap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been created sucessfully", Toast.LENGTH_SHORT)
                        .show()

                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserId)
                        .child("Following").child(currentUserId)
                        .setValue(true)


                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val message = it.exception.toString()
                    Toast.makeText(this, "Error : $message", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()

                }
            }
    }
}

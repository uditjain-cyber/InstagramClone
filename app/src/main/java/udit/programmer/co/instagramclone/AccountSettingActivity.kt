package udit.programmer.co.instagramclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.user_item_layout.*
import udit.programmer.co.instagramclone.Models.User

class AccountSettingActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicref: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicref = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

        change_image_text_btn.setOnClickListener {

            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingActivity)
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {

                uploadImageandUpdateInfo()

            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun uploadImageandUpdateInfo() {


        if (imageUri == null) {
            Toast.makeText(this, "Please select image first", Toast.LENGTH_SHORT).show()
        } else if (full_name_profile_frag.text.toString() == "") {
            Toast.makeText(this, "Please write full name first", Toast.LENGTH_SHORT).show()
        } else if (username_profile_frag.text.toString() == "") {
            Toast.makeText(this, "Please write user name first", Toast.LENGTH_SHORT).show()
        } else if (bio_profile_frag.text.toString() == "") {
            Toast.makeText(this, "Please write your bio first", Toast.LENGTH_SHORT).show()
        } else {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Account Settings")
            progressDialog.setMessage("Please wait , we are updating your info")
            progressDialog.show()

            val fileref = storageProfilePicref!!.child(firebaseUser!!.uid + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileref.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                        progressDialog.dismiss()
                    }
                }
                return@Continuation fileref.downloadUrl
            }).addOnCompleteListener(
                OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val usermap = HashMap<String, Any>()

                        usermap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                        usermap["username"] = username_profile_frag.text.toString().toLowerCase()
                        usermap["Bio"] = bio_profile_frag.text.toString().toLowerCase()
                        usermap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(usermap)

                        Toast.makeText(
                            this,
                            "Account Information has been updated sucessfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                }
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && requestCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {
        if (full_name_profile_frag.text.toString() == "") {
            Toast.makeText(this, "Please write full name first", Toast.LENGTH_SHORT).show()
        } else if (username_profile_frag.text.toString() == "") {
            Toast.makeText(this, "Please write user name first", Toast.LENGTH_SHORT).show()
        } else if (bio_profile_frag.text.toString() == "") {
            Toast.makeText(this, "Please write your bio first", Toast.LENGTH_SHORT).show()
        } else {
            val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

            val usermap = HashMap<String, Any>()

            usermap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
            usermap["username"] = username_profile_frag.text.toString().toLowerCase()
            usermap["Bio"] = bio_profile_frag.text.toString().toLowerCase()

            usersRef.child(firebaseUser.uid).updateChildren(usermap)

            Toast.makeText(
                this,
                "Account Information has been updated sucessfully",
                Toast.LENGTH_SHORT
            )
                .show()

            val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun userInfo() {

        val usersRef =
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                /* if (context != null) {
                     return
                 }
 */
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)
                    username_profile_frag.setText(user!!.getusername())
                    full_name_profile_frag?.setText(user!!.getfullname())
                    bio_profile_frag?.setText(user!!.getbio())
                }
            }

        })
    }
}

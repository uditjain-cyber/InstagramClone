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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_add_new_post.*

class AddNewPostActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicref: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_post)

        storagePostPicref = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        save_new_post_btn.setOnClickListener {
            uploadImage()
        }

        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@AddNewPostActivity)
    }

    private fun uploadImage() {

        if (imageUri == null) {
            Toast.makeText(this, "Please select image first", Toast.LENGTH_SHORT).show()
        } else if (description_post.text.toString() == "") {
            Toast.makeText(this, "Please write description first", Toast.LENGTH_SHORT).show()
        } else {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Adding your Post")
            progressDialog.setMessage("Please wait , we are updating your info")
            progressDialog.show()

            val fileref = storagePostPicref!!.child(System.currentTimeMillis().toString() + ".jpg")

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

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postid = ref.push().key

                        val postmap = HashMap<String, Any>()

                        postmap["postid"] = postid!!
                        postmap["discription"] = description_post.text.toString().toLowerCase()
                        postmap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postmap["postimage"] = myUrl

                        ref.child(postid).updateChildren(postmap)

                        Toast.makeText(
                            this,
                            "Post uploaded sucessfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val intent = Intent(this@AddNewPostActivity, MainActivity::class.java)
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
            image_post.setImageURI(imageUri)
        }
    }
}

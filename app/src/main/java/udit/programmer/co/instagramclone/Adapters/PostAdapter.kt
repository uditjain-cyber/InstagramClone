package udit.programmer.co.instagramclone.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull
import udit.programmer.co.instagramclone.Models.Post
import udit.programmer.co.instagramclone.Models.User
import udit.programmer.co.instagramclone.R

class PostAdapter(private val mContext: Context, private val mPost: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    private var firebaseUser: FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(udit.programmer.co.instagramclone.R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        publisherInfo(holder.profileImage, holder.username, holder.publisher, post.getPublisher())
    }

    private fun publisherInfo(
        profileImage: CircleImageView,
        username: TextView,
        publisher: TextView,
        publisherID: String
    ) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    val user = p0.getValue(User::class.java)
                    Picasso.get().load(user!!.getimage()).placeholder(R.drawable.profile)
                        .into(profileImage)
                    username.setText(user!!.getusername())
                    publisher.setText(user!!.getfullname())
                }
            }

        })

    }

    inner class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likebutton: ImageView
        var commentbutton: ImageView
        var savebutton: ImageView
        var username: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.user_profile_image_post)
            postImage =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.post_image_home)
            likebutton =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.post_image_like_btn)
            commentbutton =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.post_image_comment_btn)
            savebutton =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.post_save_comment_btn)
            username =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.user_name_post)
            likes =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.likes)
            publisher =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.publisher)
            description =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.description)
            comments =
                itemView.findViewById(udit.programmer.co.instagramclone.R.id.comments)

        }


    }
}
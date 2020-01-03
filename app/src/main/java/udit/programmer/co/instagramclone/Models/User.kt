package udit.programmer.co.instagramclone.Models

class User {

    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var email: String = ""

    constructor()

    constructor(
        username: String,
        fullname: String,
        bio: String,
        image: String,
        uid: String,
        email: String
    ) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.email = email
        this.uid = uid
    }

    fun getusername(): String {
        return this.username
    }

    fun getfullname(): String {
        return this.fullname
    }

    fun getbio(): String {
        return this.bio
    }

    fun getimage(): String {
        return this.image
    }

    fun getemail(): String {
        return this.email
    }

    fun getuid(): String {
        return this.uid
    }

    fun setusername(username: String) {
        this.username = username
    }

    fun setfullname(fullname: String) {
        this.fullname = fullname
    }

    fun setemail(email: String) {
        this.email = email
    }

    fun setbio(bio: String) {
        this.bio = bio
    }

    fun setimage(image: String) {
        this.image = image
    }

    fun setuid(uid: String) {
        this.uid = uid
    }

}
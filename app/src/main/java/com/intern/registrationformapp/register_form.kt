package com.intern.registrationformapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class register_form : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        buSave.setOnClickListener {
            performRegister()
            uploadImageToFirebaseStorage()
        }


        IvuploadPerson.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            IvuploadPerson.setImageBitmap(bitmap)

            IvuploadPerson.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val fullname:String= etfullname.text.toString()
        val gender:String= etgender.text.toString()
        val date: String= date.text.toString()
        val email = etEmailReg.text.toString()
        val posn = etPositionReg.text.toString()
        val HomeAdress:String=etAddressReg.text.toString()
        val phone:String=etPhoneReg.text.toString()


    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }
    // save to Firebase
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Members/$uid")

        val user = User(uid,profileImageUrl,etfullname.text.toString(),etgender.text.toString(),date.text.toString(),etEmailReg.text.toString(),etPositionReg.text.toString(),etAddressReg.text.toString(),etPhoneReg.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this,"Member Successfully Added",Toast.LENGTH_LONG).show()
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Toast.makeText(this,"Member not Added,Try Again",Toast.LENGTH_LONG).show()
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

}

class User(val uid: String, val profileImageUrl: String, val fullname: String, val gender:String,val dob: String, val email:String, val posn: String, val Haddress:String,val phone: String)
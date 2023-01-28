package com.ltl.recipes.data.user

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository {

    companion object{
        private const val TAG = "UserRepository"
        private const val COLLECTION = "users"
        private const val COLLECTION_TEST = "users-test"
    }

    private val db = Firebase.firestore

    fun addUser(user: UserRegistrationRequest) {
        val docRef = db.collection(COLLECTION_TEST).document(user.email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()){
                    docRef.set(UserModel(user.displayName, user.email))
                    Log.d(TAG, "Add user: success ${document.id}")
                } else {
                    Log.d(TAG, "Add user: already exists ${document.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Add user: error $exception")
            }
    }

    fun addUser(user: UserModel) {
        val docRef = db.collection(COLLECTION_TEST).document(user.email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()){
                    docRef.set(UserModel(user.displayName, user.email))
                    Log.d(TAG, "Add user: success ${document.id}")
                } else {
                    Log.d(TAG, "Add user: already exists ${document.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Add user: error $exception")
            }
    }

    fun getUserByEmail(email: String): UserModel {
        var user = UserModel("", "")

        val docRef = db.collection(COLLECTION_TEST).document(email)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    user = UserModel(
                        document["displayName"].toString(),
                        document["email"].toString(),
                    )
                    Log.d(TAG, "Document: ${document.id}")
                }
                else { Log.d(TAG, "Document not exists: ${document.id}") }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return user
    }
}
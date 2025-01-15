package com.example.firebase.Container

import com.example.firebase.Repository.MahasiswaRepository
import com.example.firebase.Repository.NetworkMahasiswaRepository
import com.google.firebase.firestore.FirebaseFirestore

interface AppContainer{
    val mahasiswaRepository: MahasiswaRepository
}

class MahasiswaContainer: AppContainer{
    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()

    override val mahasiswaRepository: MahasiswaRepository by lazy {
        NetworkMahasiswaRepository(firebase)
    }

}
package com.example.firebase.model


data class Mahasiswa(
    val nim: String,
    val nama: String,
    val alamat: String,
    val jenis_kelamin: String,
    val kelas: String,
    val angkatan:String
)
{
    constructor(

    ):this("","","","","","")
}
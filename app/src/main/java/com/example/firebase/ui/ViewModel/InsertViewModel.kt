package com.example.firebase.ui.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebase.model.Mahasiswa
import com.example.firebase.Repository.MahasiswaRepository
import kotlinx.coroutines.launch

class InsertViewModel(
    private val mhs: MahasiswaRepository
) :ViewModel(){
    var uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set
    var uiState: FromState by mutableStateOf(FromState.Idle)

    //Memperbarui state berdasarkan input pengguna
    fun updateState(mahasiswaEvent: MahasiswaEvent) {
        uiEvent = uiEvent.copy(
            insertUiEvent = mahasiswaEvent,
        )
    }
    //Validasi data input Pengguna
    fun validateFields(): Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FromErrorState(
            nim = if (event.nim.isEmpty()) "NIM tidak boleh kosong" else null,
            nama = if (event.nama.isEmpty()) "Nama tidak boleh kosong" else null,
            jenis_kelamin = if (event.jenis_kelamin.isEmpty()) "Jenis Kelamin tidak boleh kosong" else null,
            alamat = if (event.alamat.isEmpty()) "Alamat tidak boleh kosong" else null,
            kelas = if (event.kelas.isEmpty()) "Kelas tidak boleh kosong" else null,
            angkatan = if (event.angkatan.isEmpty()) "Angkatan tidak boleh kosong" else null,
        )
        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }
    fun insertMhs() {
        if (validateFields()) {
            viewModelScope.launch {
                uiState = FromState.Loading
                try {
                    mhs.insertMahasiswa(uiEvent.insertUiEvent.toMhsModel())
                    uiState = FromState.Success("Data berhasil ditambahkan")
                } catch (e: Exception) {
                    uiState = FromState.Error("Data gagal ditambahkan")
                }
            }
        } else {
            uiState = FromState.Error("Data tidak valid")
        }
    }
    fun resetState() {
        uiEvent = InsertUiState()
        uiState = FromState.Idle}

    fun resetSnackBarMessage() {
        uiState = FromState.Idle
    }
}

sealed class FromState{
    object Idle : FromState()
    object Loading : FromState()
    data class Success(val message: String) : FromState()
    data class Error(val message: String) : FromState()
}

data class InsertUiState(
    val insertUiEvent: MahasiswaEvent = MahasiswaEvent(),
    val isEntryValid: FromErrorState = FromErrorState(),
)

data class FromErrorState(
    val nim: String?= null,
    val nama: String?= null,
    val jenis_kelamin: String?= null,
    val alamat: String?= null,
    val kelas: String?= null,
    val angkatan: String?= null,
){
    fun isValid(): Boolean {
        return nim == null && nama == null && jenis_kelamin == null && alamat == null && kelas == null && angkatan == null
    }
}

data class MahasiswaEvent(
    val nim: String = "",
    val nama: String = "",
    val jenis_kelamin: String = "",
    val alamat: String = "",
    val kelas: String = "",
    val angkatan: String = "",
)
fun MahasiswaEvent.toMhsModel() : Mahasiswa = Mahasiswa(
    nim = nim,
    nama = nama,
    jenis_kelamin = jenis_kelamin,
    alamat = alamat,
    kelas = kelas,
    angkatan = angkatan,
)
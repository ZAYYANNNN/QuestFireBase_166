package com.example.firebase.ui.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebase.model.Mahasiswa
import com.example.firebase.Repository.MahasiswaRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// Status UI buat nampilin kondisi data: sukses, error, atau loading
sealed class HomeUiState {
    data class Success(val mahasiswa: List<Mahasiswa>) : HomeUiState() // Kalau data berhasil diambil
    data class Error(val exception: Throwable) : HomeUiState() // Kalau ada error
    object Loading : HomeUiState() // Kalau lagi loading
}
class HomeViewModel (
    private val mhs: MahasiswaRepository
):ViewModel(){
    var mhsUIState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    // Langsung ambil data pas ViewModel dibuat
    init {
        getMhs()
    }

    // Ambil data mahasiswa dari repository
    fun getMhs() {
        viewModelScope.launch { // Coroutine biar proses ambil data di background
            mhs.getAllMahasiswa()
                .onStart {
                    mhsUIState = HomeUiState.Loading
                }
                .catch {
                    mhsUIState = HomeUiState.Error(it)
                }
                .collect{
                    mhsUIState = if (it.isEmpty()) {
                        HomeUiState.Error(Exception("Belum ada daftar mahasiswa"))
                    }else{
                        HomeUiState.Success(it)
                    }
                }
        }
    }
    fun deleteMhs(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            try {
                mhs.deleteMahasiswa(mahasiswa)
            }catch (e: Exception) {
                mhsUIState = HomeUiState.Error(e)
            }
        }
    }
}
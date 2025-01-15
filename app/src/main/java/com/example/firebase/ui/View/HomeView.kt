package com.example.firebase.ui.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebase.model.Mahasiswa
import com.example.firebase.ui.ViewModel.HomeUiState
import com.example.firebase.ui.ViewModel.HomeViewModel
import com.example.firebase.ui.ViewModel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit, // Callback untuk navigasi ke halaman tambah mahasiswa
    modifier: Modifier = Modifier, // Modifier untuk mengatur layout composable
    onDetailClick: (String) -> Unit = {}, // Callback untuk navigasi ke detail mahasiswa berdasarkan NIM
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory) // ViewModel untuk memuat data mahasiswa
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior() // Mengatur perilaku scrolling pada TopAppBar

    // Scaffold: Struktur dasar layar dengan komponen seperti TopBar, FAB, dan konten
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // Menambahkan nested scroll behavior
        topBar = {
            TopAppBar(
                title = {Text("Home")}
            )
        },
        floatingActionButton = {
            // Tombol floating untuk menambahkan data mahasiswa
            FloatingActionButton(
                onClick = navigateToItemEntry, // Navigasi ke halaman tambah mahasiswa
                shape = MaterialTheme.shapes.medium, // Mengatur bentuk tombol floating
                modifier = Modifier.padding(18.dp) // Memberikan padding
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // Ikon plus untuk tombol
                    contentDescription = "Add Mahasiswa" // Deskripsi konten ikon
                )
            }
        },
    ) { innerPadding ->
        // Konten utama layar (bagian `Body`) yang menampilkan daftar mahasiswa atau status lain
        HomeStatus(
            homeUiState = viewModel.mhsUIState, // State UI dari ViewModel: Loading, Success, atau Error
            retryAction = { viewModel.getMhs() }, // Callback untuk mencoba lagi memuat data
            modifier = Modifier.padding(innerPadding), // Memberikan padding berdasarkan `Scaffold`
            onDetailClick = onDetailClick, // Aksi ketika item detail mahasiswa diklik
            onDeleteClick = {
                viewModel.deleteMhs(it) // Menghapus data mahasiswa
                viewModel.getMhs()         // Memuat ulang data setelah penghapusan
            }
        )
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState, // UI state yang bisa berupa Loading, Success atau Error
    retryAction: () -> Unit, // Fungsi untuk mencoba lagi setelah error
    modifier: Modifier = Modifier, // Modifier untuk styling tampilan
    onDeleteClick: (Mahasiswa) -> Unit = {}, // Fungsi untuk handle aksi delete mahasiswa
    onDetailClick: (String) -> Unit // Fungsi untuk handle klik detail mahasiswa
) {
    when (homeUiState) {
        // Jika dalam kondisi loading
        is HomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())

        // Jika data berhasil diambil
        is HomeUiState.Success -> {
            // Jika data ada, tampilkan layout mahasiswa
            MhsLayout(
                mahasiswa = homeUiState.mahasiswa,
                modifier = modifier.fillMaxWidth(),
                onDetailClick = {
                    onDetailClick(it.nim) // OnDetailClick memberi akses detail mahasiswa
                },
                onDeleteClick = {
                    onDeleteClick(it) // OnDeleteClick memberi aksi delete
                }
            )
        }

        // Jika terjadi error
        is HomeUiState.Error -> OnError(retryAction, modifier = modifier.fillMaxSize(),
            messaage = homeUiState.exception.localizedMessage!!)
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    // Menampilkan gambar ikon loading (misalnya gambar koneksi error yang ada tulisan "loading")
    Column (
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CircularProgressIndicator()
        Text(text = "Loading")
    }
}

@Composable
fun OnError(retryAction: () -> Unit, modifier: Modifier = Modifier,messaage:String) {
    // Kolom sebagai layout utama
    Column(
        modifier = modifier, // Modifier untuk menyesuaikan style (opsional)
        verticalArrangement = Arrangement.Center, // Untuk menempatkan elemen secara vertikal di tengah
        horizontalAlignment = Alignment.CenterHorizontally // Untuk menempatkan elemen secara horizontal di tengah
    ) {

        // Menampilkan pesan error ("Loading failed" atau lainnya)
        Text(text = messaage, modifier = Modifier.padding(16.dp))
        // Tombol untuk mencoba ulang
        Button(onClick = retryAction) {
            Text("Retry") // Menampilkan text tombol "Retry"
        }
    }
}

@Composable
fun MhsLayout(
    mahasiswa: List<Mahasiswa>, // List data mahasiswa
    modifier: Modifier = Modifier,
    onDetailClick: (Mahasiswa) -> Unit, // Fungsi untuk tampilkan detail mahasiswa
    onDeleteClick: (Mahasiswa) -> Unit = {} // Fungsi untuk hapus data mahasiswa
) {
    // LazyColumn untuk menampilkan list mahasiswa
    LazyColumn(
        modifier = modifier, // Modifier untuk styling (opsional)
        contentPadding = PaddingValues(16.dp), // Padding untuk seluruh list
        verticalArrangement = Arrangement.spacedBy(16.dp) // Jarak antar item dalam list
    ) {
        items(mahasiswa) { mahasiswa -> // Untuk setiap mahasiswa di dalam list
            MhsCard(
                mahasiswa = mahasiswa, // Kirim data mahasiswa ke MhsCard
                modifier = Modifier
                    .fillMaxWidth() // Membuat kartu memakan seluruh lebar layar
                    .clickable { onDetailClick(mahasiswa) }, // Fungsi detail ketika item diklik
                onDeleteClick = { // Fungsi delete ketika tombol delete di kartu diklik
                    onDeleteClick(it)
                }
            )
        }
    }
}

@Composable
fun MhsCard(
    mahasiswa: Mahasiswa, // Objek mahasiswa (berisi data seperti nama, nim, dsb)
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {} // Fungsi yang dipanggil ketika tombol delete di klik
) {
    // Elemen kartu (Card)
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium, // Bentuk kartu
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Ketinggian shadow kartu
    ) {
        // Layout vertikal dalam kartu
        Column(
            modifier = Modifier.padding(16.dp), // Isi kartu diberi padding 16 dp
            verticalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar elemen
        ) {
            // Layout horizontal (baris pertama kartu)
            Row(
                modifier = Modifier.fillMaxWidth(), // Baris ini full-width
                verticalAlignment = Alignment.CenterVertically // Vertikal rata tengah
            ) {
                // Tampilkan nama mahasiswa
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleLarge // Gaya teks besar
                )
                Spacer(Modifier.weight(1f)) // Spacer fleksibel buat memberi jarak

                // Tombol delete
                IconButton(onClick = { onDeleteClick(mahasiswa) }) {
                    Icon(
                        imageVector = Icons.Default.Delete, // Icon trash
                        contentDescription = null, // Tidak perlu deskripsi
                    )
                }
            }

            // Tampilkan NIM di row pertama
            Text(
                text = mahasiswa.nim,
                style = MaterialTheme.typography.titleMedium
            )

            // Data lain (kelas & alamat) di bawahnya
            Text(
                text = mahasiswa.kelas,
                style = MaterialTheme.typography.titleMedium // Gaya teks medium
            )
            Text(
                text = mahasiswa.alamat,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
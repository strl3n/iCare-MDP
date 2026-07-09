package com.istts.finalproject.data.remote.model

// Model response dari BoredAPI (https://www.boredapi.com) — API publik gratis,
// tanpa API key, dipakai sebagai sumber "artikel" berupa saran aktivitas
// positif untuk memperbaiki mood. Dipilih karena temanya cocok untuk aplikasi
// pencegahan bunuh diri (SDG 3.2.4): mendorong aktivitas menyenangkan,
// bukan berita yang berisiko memuat konten negatif/memicu.
data class BoredActivityResponse(
    val activity: String?,
    val type: String?,
    val participants: Int?,
    val price: Double?,
    val accessibility: Double?
)

package com.istts.finalproject.data.remote.repository

import com.istts.finalproject.data.remote.ApiService
import com.istts.finalproject.data.remote.BoredRetrofitClient
import com.istts.finalproject.data.remote.model.Quote
import com.istts.finalproject.utils.Resource

class ArticleRepository(
    private val apiService: ApiService
) {

    suspend fun getRandomQuote(): Resource<Quote> {
        return try {
            val response = apiService.getRandomQuote()
            if (response.isSuccessful && response.body()?.success == true) {
                val quote = response.body()?.data
                if (quote != null && quote.quote.isNotEmpty() && quote.quote != "Test") {
                    Resource.Success(quote)
                } else {
                    Resource.Success(defaultQuote())
                }
            } else {
                Resource.Success(defaultQuote())
            }
        } catch (e: Exception) {
            Resource.Success(defaultQuote())
        }
    }

    private fun defaultQuote() = Quote(
        quote = "Kesehatan mental bukanlah tujuan, tapi proses. Mari jaga kesehatan mental kita bersama.",
        author = "iCare"
    )

    fun getLocalArticles(): List<Article> {
        return listOf(
            Article(
                id = 1,
                title = "Mengenal Tanda-Tanda Depresi pada Remaja",
                category = "Depresi",
                description = "Kenali tanda-tanda depresi pada remaja agar bisa segera ditangani.",
                content = "Depresi pada remaja seringkali tidak terlihat. Tanda-tanda yang perlu diwaspadai antara lain: perubahan suasana hati yang drastis, kehilangan minat pada aktivitas yang disukai, perubahan pola tidur dan makan, serta menarik diri dari pergaulan. Kabar baiknya, depresi adalah kondisi yang bisa diatasi. Dukungan dari keluarga, teman, dan tenaga profesional sangat membantu proses pemulihan.",
                date = "2 Juli 2026"
            ),
            Article(
                id = 2,
                title = "Cara Mengatasi Kecemasan Berlebihan",
                category = "Kecemasan",
                description = "Tips mengatasi kecemasan agar tidak mengganggu aktivitas sehari-hari.",
                content = "Kecemasan adalah respon alami tubuh terhadap stres. Namun jika berlebihan, bisa mengganggu kehidupan. Cara mengatasinya: latihan pernapasan, meditasi, olahraga teratur, dan berbicara dengan orang yang dipercaya. Ingat, kamu tidak sendirian — banyak orang berhasil melewati fase ini dan menemukan ketenangan kembali.",
                date = "2 Juli 2026"
            ),
            Article(
                id = 3,
                title = "Pentingnya Kesehatan Mental bagi Mahasiswa",
                category = "Kesehatan Mental",
                description = "Menjaga kesehatan mental sama pentingnya dengan kesehatan fisik.",
                content = "Mahasiswa sering menghadapi tekanan akademik yang tinggi. Penting untuk menjaga kesehatan mental dengan: mengatur waktu belajar, istirahat cukup, bersosialisasi, dan jangan ragu meminta bantuan jika merasa kewalahan. Merayakan kemajuan kecil setiap hari juga bisa membantu menjaga semangat tetap positif.",
                date = "2 Juli 2026"
            ),
            Article(
                id = 4,
                title = "Kekuatan Rasa Syukur untuk Kesehatan Mental",
                category = "Kebahagiaan",
                description = "Membiasakan diri bersyukur terbukti meningkatkan kebahagiaan jangka panjang.",
                content = "Penelitian psikologi menunjukkan bahwa mencatat 3 hal yang disyukuri setiap hari dapat meningkatkan mood dan mengurangi gejala stres secara signifikan dalam beberapa minggu. Coba mulai dari hal kecil: secangkir kopi hangat, senyuman teman, atau matahari pagi yang cerah.",
                date = "3 Juli 2026"
            ),
            Article(
                id = 5,
                title = "Kisah Inspiratif: Bangkit dari Masa Sulit",
                category = "Inspirasi",
                description = "Setiap orang punya masanya, dan setiap masa akan berlalu.",
                content = "Banyak orang yang pernah berada di titik terendah dalam hidupnya, namun berhasil bangkit dan menemukan makna baru. Kuncinya adalah tidak menghadapi semuanya sendirian — berbicara dengan orang terdekat atau profesional bisa menjadi langkah pertama menuju pemulihan. Kamu berharga, dan hidupmu punya arti.",
                date = "4 Juli 2026"
            )
        )
    }

    // Ambil "artikel" tambahan berupa saran aktivitas positif dari BoredAPI
    // (3rd-party, gratis, tanpa API key, tidak diblokir di Indonesia).
    // Dipilih karena sejalan dengan misi aplikasi pencegahan bunuh diri (SDG 3.2.4):
    // mendorong aktivitas yang menyenangkan, bukan konten berita yang berisiko negatif.
    suspend fun getRemoteArticles(): Resource<List<Article>> {
        // Beberapa kategori aktivitas yang cenderung positif & menyenangkan
        val types = listOf("social", "relaxation", "recreational", "charity", "music")
        val results = mutableListOf<Article>()

        for ((index, type) in types.withIndex()) {
            try {
                val response = BoredRetrofitClient.instance.getActivity(type)
                if (response.isSuccessful) {
                    val body = response.body()
                    val activityText = body?.activity
                    if (!activityText.isNullOrBlank()) {
                        results.add(
                            Article(
                                id = 1000 + index,
                                title = "Ide Aktivitas Menyenangkan: $activityText",
                                category = categoryLabel(type),
                                description = "Coba lakukan aktivitas ini untuk membantu memperbaiki mood-mu hari ini.",
                                content = buildString {
                                    append("Kadang, hal sederhana bisa membuat perbedaan besar untuk suasana hati.\n\n")
                                    append("Coba luangkan waktu untuk: \"$activityText\"\n\n")
                                    append("Kategori: ${categoryLabel(type)}\n")
                                    body?.participants?.let {
                                        append(if (it > 1) "Aktivitas ini bisa dilakukan bersama $it orang — ajak teman atau keluargamu!\n" else "Aktivitas ini bisa dilakukan sendiri, cocok untuk me-time.\n")
                                    }
                                    append("\nIngat, merawat diri sendiri bukan hal yang egois. Kamu berhak merasa bahagia.")
                                },
                                date = "Hari ini"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Kalau satu request gagal, lanjut ke type berikutnya. Tidak perlu gagalkan semuanya.
                continue
            }
        }

        return if (results.isNotEmpty()) {
            Resource.Success(results)
        } else {
            Resource.Error("Tidak bisa mengambil aktivitas tambahan saat ini")
        }
    }

    private fun categoryLabel(type: String): String {
        return when (type) {
            "social" -> "Sosial"
            "relaxation" -> "Relaksasi"
            "recreational" -> "Rekreasi"
            "charity" -> "Kebaikan"
            "music" -> "Musik"
            else -> "Aktivitas"
        }
    }
}

data class Article(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val content: String,
    val date: String
)

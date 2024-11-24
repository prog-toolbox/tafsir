package tb.oss.tafsir.service

case class DetailedAyah(
  surahNumber: Int,
  surahName: String,
  ayahNumber: Int,
  ayahText: String,
  tafsirName: String,
  interpretation: String
)

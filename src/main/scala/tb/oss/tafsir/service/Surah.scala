package tb.oss.tafsir.service

import tb.oss.tafsir.service.Client.Chapter

case class Surah private (id: Int, nameArabic: String, versesCount: Int, revelationPlace: String)

object Surah {
  def fromChapter(chapter: Chapter): Surah =
    Surah(
      id = chapter.`id`,
      nameArabic = chapter.`name_arabic`,
      versesCount = chapter.`verses_count`,
      revelationPlace = if (chapter.`revelation_place` == "makkah") "مكّة" else "المدينة"
    )
}

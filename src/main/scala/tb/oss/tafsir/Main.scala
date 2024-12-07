package tb.oss.tafsir

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalajs.dom
import tb.oss.tafsir.service.{Client, Surah, TafsirService}
import org.scalajs.dom.document
import org.scalajs.dom.html
import tb.oss.tafsir.service.Client.{Ayah, AyahInterpretation}
import scalatags.Text.all.*

object Main {

  def formatAyahInterpretation(
    surahNumber: Int,
    surah: Surah,
    ayahNumber: Int,
    ayah: Ayah,
    ayahInterpretation: AyahInterpretation
  ): String = {
    val fields = List(
      s"رقم السورة: $surahNumber",
      s"السورة: ${surah.nameArabic}",
      s"مكان النزول: ${surah.revelationPlace}",
      s"عدد الآيات: ${surah.versesCount}",
      s"الآية: ${ayah.verse.`text_uthmani`}",
      s"رقم الآية: $ayahNumber",
      s"كتاب التفسير: ${ayahInterpretation.tafsir.`resource_name`}",
      s"التفسير: ${ayahInterpretation.tafsir.`text`}"
    )

    fields.map(field => s"• $field").mkString("\n")
  }

  def main(args: Array[String]): Unit = {
    implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

    val service = TafsirService.impl[IO](Client.impl[IO]())

    val tafsirForm = form(style := "direction: rtl; text-align: right; white-space: pre-wrap;")(
      input(name := "tafsirId", placeholder := "رقم التفسير"),
      input(name := "surahNumber", placeholder := "رقم السورة"),
      input(name := "ayahNumber", placeholder := "رقم الآية"),
      button("تفسير الآية")
    )

    val formElement = document.createElement("form").asInstanceOf[html.Form]
    formElement.setAttribute("style", "direction: rtl; text-align: right; white-space: pre-wrap;")
    formElement.innerHTML = tafsirForm.render
    document.body.appendChild(formElement)

    formElement.onsubmit = (e: dom.Event) => {
      e.preventDefault()

      val resultContainer = Option(document.getElementById("resultContainer")).getOrElse {
        val newResultContainer = document.createElement("div")
        newResultContainer.id = "resultContainer"
        document.body.appendChild(newResultContainer)
        newResultContainer
      }.asInstanceOf[html.Element]

      resultContainer.innerHTML = ""

      val tafsirId = formElement.elements.namedItem("tafsirId").asInstanceOf[html.Input].value.toInt
      val surahNumber = formElement.elements.namedItem("surahNumber").asInstanceOf[html.Input].value.toInt
      val ayahNumber = formElement.elements.namedItem("ayahNumber").asInstanceOf[html.Input].value.toInt

      val resultIO: IO[(AyahInterpretation, Ayah, Surah)] = for {
        interpretation <- service.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
        ayah <- service.getAyah(surahNumber, ayahNumber)
        surah <- service.getSurah(surahNumber)
      } yield (interpretation, ayah, surah)

      resultIO.unsafeRunAsync {
        case Right((interpretation, ayah, surah)) =>
          val resultNode = div(style := "direction: rtl; text-align: right; white-space: pre-wrap;")(
            formatAyahInterpretation(surahNumber, surah, ayahNumber, ayah, interpretation)
          )
          resultContainer.innerHTML = resultNode.render
        case Left(ex) =>
          val errorNode = div(s"Failed to interpret: ${ex.getMessage}")
          resultContainer.innerHTML = errorNode.render
      }
    }
  }
}

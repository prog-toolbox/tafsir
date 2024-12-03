package tb.oss.tafsir

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalajs.dom
import tb.oss.tafsir.service.{Client, TafsirService}
import org.scalajs.dom.document
import org.scalajs.dom.html
import tb.oss.tafsir.service.Client.{Ayah, AyahInterpretation}
import scalatags.Text.all.*

object Main {

  def formatAyahInterpretation(
    surahNumber: Int,
    ayahNumber: Int,
    ayah: Ayah,
    ayahInterpretation: AyahInterpretation
  ): String = {
    val fields = List(
      s"رقم السورة: $surahNumber",
      s"رقم الآية: $ayahNumber",
      s"الآية: ${ayah.verse.`text_uthmani`}",
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
      val tafsirId = formElement.elements.namedItem("tafsirId").asInstanceOf[html.Input].value.toInt
      val surahNumber = formElement.elements.namedItem("surahNumber").asInstanceOf[html.Input].value.toInt
      val ayahNumber = formElement.elements.namedItem("ayahNumber").asInstanceOf[html.Input].value.toInt

      val ayahIO: IO[Ayah] = service.getAyah(surahNumber, ayahNumber)
      val resultIO: IO[(AyahInterpretation, Ayah)] = service
        .getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
        .flatMap(interpretation => ayahIO.map(ayah => (interpretation, ayah)))

      resultIO.unsafeRunAsync {
        case Right((interpretation, ayah)) =>
          val resultNode = div(style := "direction: rtl; text-align: right; white-space: pre-wrap;")(
            formatAyahInterpretation(surahNumber, ayahNumber, ayah, interpretation)
          )
          val resultElement = document.createElement("div")
          resultElement.innerHTML = resultNode.render
          document.body.appendChild(resultElement)
        case Left(ex) =>
          val errorNode = div(s"Failed to interpret: ${ex.getMessage}")
          val errorElement = document.createElement("div")
          errorElement.innerHTML = errorNode.render
          document.body.appendChild(errorElement)
      }
    }
  }
}

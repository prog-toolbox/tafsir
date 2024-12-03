package tb.oss.tafsir

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalajs.dom
import tb.oss.tafsir.service.{Client, TafsirService}
import org.scalajs.dom.document
import org.scalajs.dom.html
import tb.oss.tafsir.service.Client.{Ayah, AyahInterpretation}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

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

    val form = document.createElement("form").asInstanceOf[html.Form]

    val tafsirIdInput = document.createElement("input").asInstanceOf[html.Input]
    tafsirIdInput.name = "tafsirId"
    tafsirIdInput.placeholder = "رقم التفسير"

    val surahNumberInput = document.createElement("input").asInstanceOf[html.Input]
    surahNumberInput.name = "surahNumber"
    surahNumberInput.placeholder = "رقم السورة"

    val ayahNumberInput = document.createElement("input").asInstanceOf[html.Input]
    ayahNumberInput.name = "ayahNumber"
    ayahNumberInput.placeholder = "رقم الآية"

    val submitButton = document.createElement("button").asInstanceOf[html.Button]
    submitButton.textContent = "تفسير الآية"

    form.appendChild(tafsirIdInput)
    form.appendChild(surahNumberInput)
    form.appendChild(ayahNumberInput)
    form.appendChild(submitButton)
    form.setAttribute("style", "direction: rtl; text-align: right; white-space: pre-wrap;")

    document.body.appendChild(form)

    val service = TafsirService.impl[IO](Client.impl[IO]())

    form.onsubmit = (e: dom.Event) => {
      e.preventDefault()
      val tafsirId = tafsirIdInput.value.toInt
      val surahNumber = surahNumberInput.value.toInt
      val ayahNumber = ayahNumberInput.value.toInt

      val ayahIO: IO[Ayah] = service.getAyah(surahNumber, ayahNumber)
      val resultIO: IO[(AyahInterpretation, Ayah)] = service
        .getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
        .flatMap(interpretation => ayahIO.map(ayah => (interpretation, ayah)))

      val interpretIO: IO[AyahInterpretation] = service.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)

      val interpretFuture: Future[AyahInterpretation] = interpretIO.unsafeToFuture()

      resultIO.unsafeRunAsync {
        case Right((interpretation, ayah)) =>
          val resultNode = document.createElement("div")
          resultNode.innerHTML = formatAyahInterpretation(surahNumber, ayahNumber, ayah, interpretation)
          resultNode.setAttribute("style", "direction: rtl; text-align: right; white-space: pre-wrap;")
          document.body.appendChild(resultNode)
        case Left(ex) =>
          val errorNode = document.createElement("div")
          errorNode.textContent = s"Failed to interpret: ${ex.getMessage}"
          document.body.appendChild(errorNode)
      }
    }
  }
}

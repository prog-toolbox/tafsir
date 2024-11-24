package tb.oss.tafsir

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalajs.dom
import tb.oss.tafsir.service.{Client, DetailedAyah, TafsirService}
import org.scalajs.dom.document
import org.scalajs.dom.html

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def formatDetailedAyah(ayah: DetailedAyah): String = {
    val fields = List(
      s"رقم السورة: ${ayah.surahNumber}",
      s"اسم السورة: ${ayah.surahName}",
      s"رقم الآية: ${ayah.ayahNumber}",
      s"نص الآية: ${ayah.ayahText}",
      s"كتاب التفسير: ${ayah.tafsirName}",
      s"التفسير: ${ayah.interpretation}"
    )

    fields.map(field => s"• $field").mkString("\n")
  }

  def main(args: Array[String]): Unit = {
    implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

    val form = document.createElement("form").asInstanceOf[html.Form]
    val tafsirIdInput = document.createElement("input").asInstanceOf[html.Input]
    tafsirIdInput.name = "tafsirId"
    tafsirIdInput.placeholder = "Tafsir ID"

    val surahNumberInput = document.createElement("input").asInstanceOf[html.Input]
    surahNumberInput.name = "surahNumber"
    surahNumberInput.placeholder = "Surah Number"

    val ayahNumberInput = document.createElement("input").asInstanceOf[html.Input]
    ayahNumberInput.name = "ayahNumber"
    ayahNumberInput.placeholder = "Ayah Number"

    val submitButton = document.createElement("button").asInstanceOf[html.Button]
    submitButton.textContent = "Interpret"

    form.appendChild(tafsirIdInput)
    form.appendChild(surahNumberInput)
    form.appendChild(ayahNumberInput)
    form.appendChild(submitButton)

    document.body.appendChild(form)

    val service = TafsirService.impl[IO](Client.impl[IO]())

    form.onsubmit = (e: dom.Event) => {
      e.preventDefault()
      val tafsirId = tafsirIdInput.value.toInt
      val surahNumber = surahNumberInput.value.toInt
      val ayahNumber = ayahNumberInput.value.toInt

      val interpretIO: IO[DetailedAyah] = service.interpret(tafsirId, surahNumber, ayahNumber)

      val interpretFuture: Future[DetailedAyah] = interpretIO.unsafeToFuture()

      interpretFuture.onComplete {
        case Success(result) =>
          val resultNode = document.createElement("div")
          resultNode.innerHTML = formatDetailedAyah(result)
          resultNode.setAttribute("style", "direction: rtl; text-align: right; white-space: pre-wrap;")
          document.body.appendChild(resultNode)
        case Failure(ex) =>
          val errorNode = document.createElement("div")
          errorNode.textContent = s"Failed to interpret: ${ex.getMessage}"
          document.body.appendChild(errorNode)
      }
    }
  }
}

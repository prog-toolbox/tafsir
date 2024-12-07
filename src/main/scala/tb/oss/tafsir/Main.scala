package tb.oss.tafsir

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalajs.dom
import tb.oss.tafsir.service.{Client, Surah, Tafsir, TafsirService}
import org.scalajs.dom.document
import org.scalajs.dom.html
import scalatags.Text
import tb.oss.tafsir.service.Client.{Ayah, AyahInterpretation}
import scalatags.Text.all.*

object Main {

  private def stripHtmlTags(html: String): String = {
    val tagPattern = "<[^>]*>".r
    tagPattern.replaceAllIn(html, "")
  }

  private def displayAyahInterpretation(ayah: Ayah, ayahInterpretation: AyahInterpretation): Text.TypedTag[String] = {
    {
      div(cls := "card mb-3", style := "direction: rtl; text-align: right;")(
        div(cls := "card-body")(
          h5(cls := "card-title")("تفسير الآية"),
          p(cls := "card-text")(strong("الآية: "), span(ayah.verse.`text_uthmani`)),
          p(cls := "card-text")(strong("التفسير: "), span(stripHtmlTags(ayahInterpretation.tafsir.`text`)))
        )
      )
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

    val service = TafsirService.impl[IO](Client.impl[IO]())

    val surahOptions = Surah.list.toList.sortBy(_._1).map { case (number, name) =>
      option(value := number.toString)(s"$number - $name")
    }

    val tafsirOptions = Tafsir.list.toList.sortBy(_._1).map { case (number, name) =>
      option(value := number.toString)(name)
    }

    val tafsirForm =
      div(cls := "d-flex align-items-center justify-content-center", style := "direction: rtl; white-space: pre-wrap;")(
        form(cls := "container text-end", style := "max-width: 400px;")(
          div(cls := "mb-3 ms-2")(
            label(`for` := "surahNumber", cls := "form-label")("السورة"),
            select(cls := "form-select", name := "surahNumber", id := "surahNumber")(surahOptions)
          ),
          div(cls := "mb-3 ms-2")(
            label(`for` := "ayahNumber", cls := "form-label")("الآية"),
            select(cls := "form-select", name := "ayahNumber", id := "ayahNumber")()
          ),
          div(cls := "mb-3 ms-2")(
            label(`for` := "tafsirId", cls := "form-label")("التفسير"),
            select(cls := "form-select", name := "tafsirId", id := "tafsirId")(tafsirOptions)
          ),
          div(cls := "pt-3")(button(cls := "btn btn-primary", `type` := "submit")("تفسير الآية"))
        )
      )

    val formElement = document.createElement("form").asInstanceOf[html.Form]
    formElement.innerHTML = tafsirForm.render
    document.body.appendChild(formElement)

    val script = document.createElement("script")
    script.textContent = s"""
      function updateAyahOptions() {
        var surahSelect = document.getElementsByName('surahNumber')[0];
        var ayahSelect = document.getElementsByName('ayahNumber')[0];
        var surahNumber = parseInt(surahSelect.value);
        var versesCount = getVersesCount(surahNumber);

        ayahSelect.innerHTML = '';
        for (var i = 1; i <= versesCount; i++) {
          var option = document.createElement('option');
          option.value = i.toString();
          option.text = i.toString();
          ayahSelect.appendChild(option);
        }
      }

      function getVersesCount(surahNumber) {
        var versesCount = { ${Surah.ayahsCount.map(surah => s"${surah._1}: ${surah._2}").mkString(",")} };
        return versesCount[surahNumber] || 0;
      }

      // Add change event listener to surahNumber dropdown
      document.addEventListener('DOMContentLoaded', function() {
        updateAyahOptions();
        var surahSelect = document.getElementsByName('surahNumber')[0];
        surahSelect.addEventListener('change', updateAyahOptions);
      });
    """
    document.head.appendChild(script)

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

      val resultIO: IO[(AyahInterpretation, Ayah)] = for {
        interpretation <- service.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
        ayah <- service.getAyah(surahNumber, ayahNumber)
      } yield (interpretation, ayah)

      resultIO.unsafeRunAsync {
        case Right((interpretation, ayah)) =>
          val resultNode = div(style := "direction: rtl; text-align: right; white-space: pre-wrap;")(
            displayAyahInterpretation(ayah, interpretation)
          )
          resultContainer.innerHTML = resultNode.render
        case Left(ex) =>
          val errorNode = div(s"Failed to interpret: ${ex.getMessage}")
          resultContainer.innerHTML = errorNode.render
      }
    }
  }
}

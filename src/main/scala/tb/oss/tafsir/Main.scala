package tb.oss.tafsir

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import org.scalajs.dom
import tb.oss.tafsir.service.{Client, Surah, Tafsir, TafsirService}
import org.scalajs.dom.document
import org.scalajs.dom.html
import tb.oss.tafsir.service.Client.{Ayah, AyahInterpretation}
import scalatags.Text.all.*

object Main {

  private def formatAyahInterpretation(surah: Surah, ayah: Ayah, ayahInterpretation: AyahInterpretation): String = {
    val fields = List(
      s"مكان النزول: ${surah.revelationPlace}",
      s"الآية: ${ayah.verse.`text_uthmani`}",
      s"التفسير: ${ayahInterpretation.tafsir.`text`}"
    )

    fields.map(field => s"• $field").mkString("\n")
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

    val tafsirForm = form(style := "direction: rtl; text-align: right; white-space: pre-wrap;")(
      select(name := "surahNumber")(surahOptions),
      select(name := "ayahNumber")(),
      select(name := "tafsirId")(tafsirOptions),
      button("تفسير الآية")
    )

    val formElement = document.createElement("form").asInstanceOf[html.Form]
    formElement.setAttribute("style", "direction: rtl; text-align: right; white-space: pre-wrap;")
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

      // Initialize ayahNumber options
      document.addEventListener('DOMContentLoaded', function() {
        updateAyahOptions();
      });

      // Add change event listener to surahNumber dropdown
      document.addEventListener('DOMContentLoaded', function() {
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

      val resultIO: IO[(AyahInterpretation, Ayah, Surah)] = for {
        interpretation <- service.getAyahInterpretation(tafsirId, surahNumber, ayahNumber)
        ayah <- service.getAyah(surahNumber, ayahNumber)
        surah <- service.getSurah(surahNumber)
      } yield (interpretation, ayah, surah)

      resultIO.unsafeRunAsync {
        case Right((interpretation, ayah, surah)) =>
          val resultNode = div(style := "direction: rtl; text-align: right; white-space: pre-wrap;")(
            formatAyahInterpretation(surah, ayah, interpretation)
          )
          resultContainer.innerHTML = resultNode.render
        case Left(ex) =>
          val errorNode = div(s"Failed to interpret: ${ex.getMessage}")
          resultContainer.innerHTML = errorNode.render
      }
    }
  }
}

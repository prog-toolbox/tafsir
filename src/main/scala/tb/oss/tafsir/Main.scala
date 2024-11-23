package tb.oss.tafsir

import org.scalajs.dom
import org.scalajs.dom.document

object Main {

  def main(args: Array[String]): Unit = {
    val message = "Tafsir"
    val node = document.createElement("div")
    node.textContent = message
    document.body.appendChild(node)
  }
}

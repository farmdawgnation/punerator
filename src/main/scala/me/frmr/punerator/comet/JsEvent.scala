package me.frmr.punerator.comet

import net.liftweb.json._
  import Extraction._
import net.liftweb.http.js._
  import JE._

class SimpleJsEvent(eventName:String) extends JsCmd {
  import Serialization._

  implicit def typeHints = Serialization.formats(NoTypeHints)

  def toJsCmd = {
    Call("jsevent.event", eventName, decompose(this)).cmd.toJsCmd
  }
}
class JsEvent(eventName:String, parameters:JObject) extends JsCmd {
  def toJsCmd = {
    Call("jsevent.event", eventName, parameters).cmd.toJsCmd
  }
}

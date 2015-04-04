package me.frmr.punerator.comet

import net.liftweb.json._
  import Extraction._
import net.liftweb.http.js._
  import JE._

import org.bson.types._

class SimpleJsEvent(eventName:String) extends JsCmd {
  import Serialization._

  implicit def typeHints = Serialization.formats(NoTypeHints) + new Serializer[ObjectId] {
    val clazz = classOf[ObjectId]

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case objectId: ObjectId =>
        JString(objectId.toString())
    }

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), ObjectId] = {
      case (TypeInfo(`clazz`, None), JString(objectIdString)) =>
        new ObjectId(objectIdString)
    }
  }

  def toJsCmd = {
    Call("jsevent.event", eventName, decompose(this)).cmd.toJsCmd
  }
}
class JsEvent(eventName:String, parameters:JObject) extends JsCmd {
  def toJsCmd = {
    Call("jsevent.event", eventName, parameters).cmd.toJsCmd
  }
}

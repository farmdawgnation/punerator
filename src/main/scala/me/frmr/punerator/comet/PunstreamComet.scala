package me.frmr.punerator
package comet

import net.liftweb._
  import http._
    import SHtml._
  import comet._
  import actor._
  import json._
  import util._
    import Helpers._
  import mongodb._
    import BsonDSL._

import me.frmr.punerator.model._

case class PunVotedPunny(id: String) extends SimpleJsEvent("pun-voted-punny")
case class PunVotedTearable(id: String) extends SimpleJsEvent("pun-voted-tearable")

sealed trait PunstreamCometMessage
case class NewPunCreated(pun: Pun) extends PunstreamCometMessage
case class PunVoteCast(pundId: String, voteType: String) extends PunstreamCometMessage

class PunstreamComet extends CometActor {
  override val dontCacheRendering = true
  implicit val formats = DefaultFormats
  private val initialNumberOfPuns = 20

  var visiblePuns = Pun.findAll(Nil, ("createdAt" -> -1), Limit(initialNumberOfPuns))
  var newPuns: List[Pun] = Nil

  def render = {
    ".new-puns" #> (newPuns.isEmpty ? ClearNodes | PassThru) andThen
    ".new-pun-count *" #> newPuns.length &
    ".pun" #> visiblePuns.map { pun =>
      "^ [data-pun-id]" #> pun._id.toString &
      ".pun-author *" #> pun.author &
      ".pun-text *" #> pun.pun &
      ".punny-count *" #> pun.punny &
      ".tearable-count *" #> pun.tearable &
      ".vote-punny [onclick]" #> onEvent { _ =>
        Pun.update("_id" -> pun._id, "$inc" -> ("punny" -> 1))
        this ! PunVoteCast(pun._id.toString, "punny")
      } &
      ".vote-tearable [onclick]" #> onEvent { _ =>
        Pun.update("_id" -> pun._id, "$inc" -> ("tearable" -> 1))
        PunVotedTearable(pun._id.toString, "tearable")
      }
    }
  }

  override def lowPriority = {
    case NewPunCreated(pun: Pun) =>
      newPuns = pun +: newPuns
      reRender()

    case PunVoteCast(punId, voteType) =>
      //TODO
  }
}

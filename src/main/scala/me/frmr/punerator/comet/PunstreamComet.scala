package me.frmr.punerator
package comet

import net.liftweb._
  import http._
    import js.JsCmds._
    import SHtml._
  import comet._
  import actor._
  import json._
    import Extraction._
  import util._
    import Helpers._
  import mongodb._
    import BsonDSL._

import me.frmr.punerator.model._
import me.frmr.punerator.actor._

case class PunVotedPunny(id: String) extends SimpleJsEvent("pun-voted-punny")
case class PunVotedTearable(id: String) extends SimpleJsEvent("pun-voted-tearable")

sealed trait PunstreamCometMessage
case class NewPunCreated(pun: Pun) extends PunstreamCometMessage
case class PunVoteCast(pundId: String, voteType: String) extends PunstreamCometMessage
case object DisplayNewPuns extends PunstreamCometMessage
case object DisplayOlderPuns extends PunstreamCometMessage

class PunstreamComet extends CometActor {
  implicit val formats = Pun.formats
  private val initialNumberOfPuns = 10
  private var totalNumberOfPuns = Pun.count(JObject(Nil))

  var visiblePuns = Pun.findAll(Nil, ("createdAt" -> -1), Limit(initialNumberOfPuns))
  var newPuns: List[Pun] = Nil

  def oldPunCount = {
    totalNumberOfPuns - (visiblePuns.length + newPuns.length)
  }

  def thereAreOlderPuns_? = {
    oldPunCount > 0
  }

  def render = {
    ".new-puns" #> (newPuns.isEmpty ? ClearNodes | PassThru) andThen
    ".old-puns" #> (thereAreOlderPuns_? ? PassThru | ClearNodes) andThen
    ".new-pun-count *" #> newPuns.length &
    ".display-new-puns [onclick]" #> onEvent { _ =>
      this ! DisplayNewPuns
      Noop
    } &
    ".old-pun-count *" #> oldPunCount &
    ".display-old-puns [onclick]" #> onEvent { _ =>
      this ! DisplayOlderPuns
      Noop
    } &
    ".pun" #> visiblePuns.map { pun =>
      "^ [data-pun-id]" #> pun._id.toString &
      ".pun-author *" #> pun.author &
      ".pun-text *" #> pun.pun &
      ".punny-count *" #> pun.punny &
      ".tearable-count *" #> pun.tearable &
      ".vote-punny [onclick]" #> onEvent { _ =>
        Pun.update("_id" -> pun._id, "$inc" -> ("punny" -> 1))

        CometBroadcastActor ! BroadcastCometMessage(
          PunVoteCast(pun._id.toString, "punny"),
          "PunstreamComet"
        )
      } &
      ".vote-tearable [onclick]" #> onEvent { _ =>
        Pun.update("_id" -> pun._id, "$inc" -> ("tearable" -> 1))

        CometBroadcastActor ! BroadcastCometMessage(
          PunVoteCast(pun._id.toString, "tearable"),
          "PunstreamComet"
        )
      }
    }
  }

  private def punUpdater(punId: String, voteType: String)(pun: Pun) = {
    pun match {
      case matchingPun if matchingPun._id.toString == punId =>
        if (voteType == "punny") {
          matchingPun.copy(punny = matchingPun.punny + 1)
        } else {
          matchingPun.copy(tearable = matchingPun.tearable + 1)
        }

      case otherPun =>
        otherPun
    }
  }

  override def lowPriority = {
    case DisplayNewPuns =>
      visiblePuns = newPuns ::: visiblePuns
      newPuns = Nil
      reRender()

    case DisplayOlderPuns =>
      // Find the oldest pun then find the next 20 oldest puns.
      for (oldestPun <- visiblePuns.lastOption) {
        val olderPuns = Pun.findAll(
          ("createdAt" -> ("$lt" -> decompose(oldestPun.createdAt))),
          ("createdAt" -> -1),
          Limit(initialNumberOfPuns)
        )

        println("older puns")
        println(olderPuns)

        visiblePuns = visiblePuns ::: olderPuns
        reRender()
      }

    case NewPunCreated(pun: Pun) =>
      totalNumberOfPuns = totalNumberOfPuns + 1
      newPuns = pun +: newPuns
      reRender()

    case PunVoteCast(punId, voteType) =>
      visiblePuns = visiblePuns.map(punUpdater(punId, voteType))
      newPuns = newPuns.map(punUpdater(punId, voteType))
      reRender()
  }
}

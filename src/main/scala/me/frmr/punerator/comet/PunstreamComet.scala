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

case class PunstreamLoaded(puns: List[Pun]) extends SimpleJsEvent("punstream-loaded")
case class NewPun(pun: Pun) extends SimpleJsEvent("new-pun")
case class PunVote(punId: String, voteType: String) extends SimpleJsEvent("pun-vote")

sealed trait PunstreamCometMessage
case class NewPunCreated(pun: Pun) extends PunstreamCometMessage
case class PunVoteCast(pundId: String, voteType: String) extends PunstreamCometMessage

class PunstreamComet extends CometActor {
  override val dontCacheRendering = true
  implicit val formats = DefaultFormats
  private val initialNumberOfPuns = 20

  var mostRecentPuns = Pun.findAll(Nil, ("createdAt" -> -1), Limit(initialNumberOfPuns))

  def render = {
    S.appendJs(PunstreamLoaded(mostRecentPuns))
    PassThru
  }

  override def lowPriority = {
    case NewPunCreated(pun: Pun) =>
      mostRecentPuns = (pun +: mostRecentPuns).take(initialNumberOfPuns)
      partialUpdate(NewPun(pun))

    case PunVoteCast(punId, voteType) =>
      partialUpdate(PunVote(punId, voteType))
  }
}

package me.frmr.punerator.actor

import net.liftweb._
  import common._
  import actor._
  import http._

case class BroadcastCometMessage(message: Any, cometType: String, cometName: Box[String] = Empty)

object CometBroadcastActor extends LiftActor {
  private var sessionInfos: Map[String, SessionInfo] = Map.empty

  def messageHandler = {
    case SessionWatcherInfo(newSessions) =>
      sessionInfos = newSessions

    case BroadcastCometMessage(message, cometType, cometName) =>
      for {
        (sessionId, sessionInfo) <- sessionInfos
        matchingComet <- sessionInfo.session.findComet(cometType, cometName)
      } {
        matchingComet ! message
      }
  }
}

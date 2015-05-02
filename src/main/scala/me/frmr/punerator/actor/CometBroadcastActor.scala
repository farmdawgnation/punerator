package me.frmr.punerator.actor

import net.liftweb._
  import common._
  import actor._
  import http._

case class BroadcastCometMessage(message: Any, cometType: String)
case class BroadcastCometMessageWithName(message: Any, cometType: String, cometName: Box[String] = Empty)

case class NewlyCreatedSession(session: LiftSession)

object CometBroadcastActor extends LiftActor {
  private var sessionInfos: Map[String, SessionInfo] = Map.empty

  def messageHandler = {
    case SessionWatcherInfo(newSessions) =>
      sessionInfos = newSessions

    case NewlyCreatedSession(newSession) =>
      sessionInfos = sessionInfos + (newSession.uniqueId -> SessionInfo(newSession, Empty, Empty, 0, 0))

    case BroadcastCometMessage(message, cometType) =>
      for {
        (sessionId, sessionInfo) <- sessionInfos
        matchingComet <- sessionInfo.session.findComet(cometType)
      } {
        matchingComet ! message
      }

    case BroadcastCometMessageWithName(message, cometType, cometName) =>
      for {
        (sessionId, sessionInfo) <- sessionInfos
        matchingComet <- sessionInfo.session.findComet(cometType, cometName)
      } {
        matchingComet ! message
      }
  }
}

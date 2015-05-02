package bootstrap.liftweb

import net.liftweb._
  import util._
    import Helpers._
  import common._
  import http._
    import sitemap._
    import Loc._
  import mongodb._

import com.mongodb._

import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._

import me.frmr.punerator.actor._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("me.frmr.punerator")

    // connect to mongo
    for {
      hostname <- Props.get("mongodb.host")
      port <- Props.get("mongodb.port").map(_.toInt)
      database <- Props.get("mongodb.database")
    } {
      MongoDB.defineDb(DefaultConnectionIdentifier, new MongoClient(hostname, port), database)
    }

    // Build SiteMap
    val entries = List(
      Menu.i("Home") / "index", // the simple way to declare a menu
      Menu.i("Submit") / "submit"
    )

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Add the CometBroadcastActor as a session watcher
    SessionMaster.sessionWatchers = CometBroadcastActor +: SessionMaster.sessionWatchers

    // A hack to ensure that we find out about new sessions asap.
    LiftRules.sessionCreator = {
      case (httpSession, contextPath) =>
        val newSession = new LiftSession(contextPath, httpSession.sessionId, Full(httpSession))
        CometBroadcastActor ! NewlyCreatedSession(newSession)
        newSession
    }

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()

  }
}

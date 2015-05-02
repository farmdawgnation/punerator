package me.frmr.punerator
package snippet

import net.liftweb._
  import http._
    import js._
      import JsCmds._
    import SHtml._
  import common._
  import util._
    import Helpers._
  import mongodb._

import me.frmr.punerator.model._
import me.frmr.punerator.actor._
import me.frmr.punerator.comet._

object PunSubmitter {
  def persistPun(name: String, pun: String) = {
    val newPun = Pun(name, pun)
    newPun.save
    CometBroadcastActor ! BroadcastCometMessage(
      NewPunCreated(newPun),
      "PunstreamComet"
    )

    Alert("Your pun has been created!") &
    Reload
  }

  def render = {
    var name = ""
    var pun = ""

    makeFormsAjax andThen
    "#pun-author" #> text(name, name = _) &
    "#pun-content" #> textarea(pun, pun = _) &
    "#submit-pun" #> ajaxOnSubmit(() => persistPun(name, pun))
  }
}

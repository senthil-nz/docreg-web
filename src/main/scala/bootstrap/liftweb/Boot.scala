package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import _root_.vvv.docreg.model._
import _root_.vvv.docreg.backend._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // where to search snippet
    LiftRules.addToPackages("vvv.docreg")
    Schemifier.schemify(true, Schemifier.infoF _, User, Project, Document, Revision)

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) ::
    User.sitemap

    LiftRules.statelessDispatchTable.append(MyCSSMorpher)

    LiftRules.setSiteMap(SiteMap(entries:_*))

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)

    val backend = new SimulatedBackend
    backend start
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}

import net.liftweb.http.rest._

object MyCSSMorpher extends RestHelper {
  serve {
    case r @ Req("dynocss" :: file :: _, "css", GetRequest) =>
      for {
        convertFunc <- findConvertFunc(r)
        fileContents <- readFile(file+".css")
        converted <- convertFunc(fileContents)
      } yield CSSResponse(converted)
  }

  // based on the browser detected, return a function 
  // that will convert HTML5 css into CSS for that browser
  def findConvertFunc(req: Req): Box[String => Box[String]] =
    Empty

  // load the file from the specific location...
  // are you going put the CSS templates in
  // resources, etc.
  def readFile(name: String): Box[String] = Empty
}

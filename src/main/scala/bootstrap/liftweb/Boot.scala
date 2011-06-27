

package bootstrap.liftweb

import net.liftweb._
import http.{LiftRules, NotFoundAsTemplate, ParsePath,RedirectResponse}
import sitemap.{SiteMap, Menu, Loc}
import util.{ NamedPF }
import _root_.net.liftweb.sitemap.Loc._
import net.liftweb._
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import util.{Props}
import common.{Full}
import http._
import com.photography.model._
import net.liftweb.sitemap.Loc._
import net.liftweb.http.LiftRules
import net.liftweb.sitemap._

class Boot {
  def boot {
  
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
        new StandardDBVendor(Props.get("db.driver") openOr "com.mysql.jdbc.Driver",
        			               Props.get("db.url") openOr "jdbc:mysql://localhost:3306/photography",
        			               Full("photo_user"), Full("photo"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, Gallery, Photograph, GalleriesPhotographs)

    // where to search snippet
    LiftRules.addToPackages("com.photography")
    
    val loggedIn = If(() => User.loggedIn_?,
              () => RedirectResponse("/user_mgt/login"))
    
    // build sitemap
    val entries = List(
      Menu.i("Home") / "index",
      Menu.i("Photograph") / "photograph" >> LocGroup("public") >> Hidden,
      Menu.i("Create Gallery") / "gallery" / "create" >> loggedIn,
      Menu.i("List Gallery") / "gallery" / "list",
      Menu.i("View Gallery") / "gallery" / "view",
      Menu.i("Upload Photo") / "photograph" / "create" >> loggedIn >> Hidden,
      Menu.i("List Photos") / "photograph" / "list" >> loggedIn,
      Menu.i("Image URLs") / "images" / **
      ) ::: User.menus 

    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    // Use HTML5
    //LiftRules.htmlProperties.default.set((r: Req) =>new Html5Properties(r.userAgent))
    
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding 
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)
    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)
    
    
    LiftRules.statelessRewrite.append {
      case RewriteRequest(
        ParsePath("gallery" :: "view" :: userId :: galleryName :: Nil, _, _, _), _, _) => 
        RewriteResponse("/gallery/view" :: Nil, Map("id" -> userId, "name"-> galleryName))
    }


    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
    
  }
}
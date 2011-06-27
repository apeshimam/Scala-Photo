package com.photography.snippet

import net.liftweb.common.{Box,Empty,Full,Logger}
import net.liftweb.http.{FileParamHolder,S,SHtml,StatefulSnippet}
import org.slf4j.LoggerFactory
import java.text.ParseException
import net.liftweb._
import http._
import common._
import util.Helpers._
import scala.xml.NodeSeq
import scala.xml.{NodeSeq}
import net.liftweb.util.Helpers._
import com.photography.model._
import scala.xml._
import scala.collection.mutable.ListBuffer
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers
import net.liftweb.http._
import Helpers._
import net.liftweb.mapper.{By, ByList}
import net.liftweb.common._

class Gallery {

  /** 
	 * Creates new post.
	 * @param in
	 * @return
	 */
	def add(in: NodeSeq): NodeSeq = {
		var galleryValues = Map[String,String]()

    def submit() = {
  		List("en").foreach(lang => {
  			val name = galleryValues(lang+"name").trim
  			if(name=="") S.error("Name must not be empty")
  			else {
  				val gallery = Gallery.create.name(name).user(User.currentUser)
  				gallery.save
  			}}
  		)
  		if(S.errors.isEmpty) S.redirectTo("/index")
  	}

    ((".post" #> List("en").map( lang =>
  		".name" #> SHtml.text("", parm => galleryValues += ((lang+"name",parm)), ("size","55")) 
  	 )) & ":submit" #> SHtml.submit("Add", submit))(in)
	}
	
	def show(): CssSel = {
	  val galleryTitle = S.param("id") openOr S.redirectTo("/404.html")
	  val gallery = Gallery.find(By(Gallery.name, galleryTitle)) match {
	    case Full(gallery) => gallery
	    case Empty => S.redirectTo("/404.html")
	    case _ => S.error("Error Occurred"); S.redirectTo ("/404.html")
	  }	  
	  ("#name" #> gallery.name )
	}
	
	def list() = {
	  val galleries = Gallery.findAll()
	  "#galleries" #> galleries.map(gallery => "#name" #> <a href={gallery.urlify}>{gallery.name}</a>)  
	} 
}
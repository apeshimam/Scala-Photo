package com.photography.model

import net.liftweb._
import mapper._
import util._
import common._


class Gallery extends LongKeyedMapper[Gallery] with IdPK with CreatedUpdated with ManyToMany {

  def getSingleton = Gallery
  
  
  object name extends MappedString(this, 255) { override def displayName = "Gallery Name"}
  object user extends LongMappedMapper(this, User)
  object photographs extends MappedManyToMany(GalleriesPhotographs, GalleriesPhotographs.gallery, GalleriesPhotographs.photograph, Photograph)
  
  case class SlashString(val str: String) {
		def /(other: String) = str + "/" + other
	}
	
  def urlify = {
    "/gallery/view?" + "user=" + user.toString + "&id=" + name
  }


}
object Gallery extends Gallery with LongKeyedMetaMapper[Gallery]  {
  override def dbTableName = "galleries" 
}
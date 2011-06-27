package com.photography.model

import net.liftweb._
import mapper._
import util._
import common._


class Photograph extends LongKeyedMapper[Photograph] with IdPK with ManyToMany {

  def getSingleton = Photograph

  def screenWrap = Full(<lift:surround with="default" at="content">
             <lift:bind /></lift:surround>)
             
  object image_path extends MappedText(this)
  object mime_type extends MappedString(this, 100) 
  object galleries extends MappedManyToMany(GalleriesPhotographs, GalleriesPhotographs.photograph, GalleriesPhotographs.gallery, Gallery)
  object user extends LongMappedMapper(this, User)
}
object Photograph extends Photograph with LongKeyedMetaMapper[Photograph] with CRUDify[Long, Photograph] {
  override def dbTableName = "photographs" 
  
}
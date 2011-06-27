package com.photography.model

import net.liftweb.mapper._

class GalleriesPhotographs extends Mapper[GalleriesPhotographs] {

  def getSingleton = GalleriesPhotographs
  object gallery extends LongMappedMapper(this, Gallery)
  object photograph extends LongMappedMapper(this, Photograph)


}
object GalleriesPhotographs extends GalleriesPhotographs with MetaMapper[GalleriesPhotographs]
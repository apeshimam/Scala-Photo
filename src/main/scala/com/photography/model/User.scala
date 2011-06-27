

package com.photography.model {

import net.liftweb._
import mapper._
import util._
import common._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with KeyedMetaMapper[Long,  User] with MetaMegaProtoUser[User]   {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
             <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password)
  
  override def skipEmailValidation = true
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User]  
  with CreatedUpdated 
  with OneToMany[Long, User]
  with ManyToMany {
  def getSingleton = User 
  
  object galleries extends MappedOneToMany(Gallery, Gallery.user)
  object photographs extends MappedOneToMany(Photograph, Photograph.user)
}

}
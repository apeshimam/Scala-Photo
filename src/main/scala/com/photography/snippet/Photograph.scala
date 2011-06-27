package com.photography
package snippet


import com.photography.model._

import scala.xml.{NodeSeq, Text, Elem}

import java.io.{File,FileOutputStream}

import net.liftweb._
import util._
import common._
import mapper._

import Helpers._
import http._
import S._
import js.JsCmds.Noop



class Photograph extends Logger {


  object photograph extends RequestVar(Photograph.create)
  object imageFile extends RequestVar[Box[FileParamHolder]](Empty)
  object fileName extends RequestVar[Box[String]](Full(Helpers.nextFuncName))

  private def saveFile(fp: FileParamHolder): Unit = {
    fp.file match {
      case null =>
      case x if x.length == 0 => info("File size is 0")
      case x =>{
        info("We got a file!")
        val filePath = "src/main/webapp/images"
        fileName.is.map{
          name => photograph.is.image_path.set("/images/" + name + fp.fileName.takeRight(4))
        }
        photograph.is.mime_type(fp.mimeType)
        photograph.user(User.currentUser)
        photograph.save
        
        val oFile = new File(filePath,  fileName.is.openOr("BrokenLink") + fp.fileName.takeRight(4))
              val output = new FileOutputStream(oFile)
              output.write(fp.file)
              output.close()
        info("File uploaded!")
        S.notice("Thanks for the upload")
      }
    }
  }

  def add ={
    // process the form
    def process() {

      (imageFile.is) match {
        case Empty => S.error("You forgot to enter a part number")
        case image => {
          info("The RequestVar content is: %s".format(imageFile.is))
          imageFile.is.map{ info("About to start the file upload"); file => saveFile(file)}
          info("Done")
        }
      }

    }

    uploadImg &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }


  def uploadImg: CssBindFunc = {
    (S.get_?, imageFile.is) match {
      case (true, _)  => "name=image" #> SHtml.fileUpload(s => imageFile(Full(s)))
      case (false, _) => "name=image" #> fileName.is.map{ name =>
        SHtml.link(
          "http://127.0.0.1:8080/images/" + //Using open_! because we already made sure it is not Null
            name + imageFile.is.open_!.fileName.takeRight(4) ,
          () => Unit ,
          <span>Click to see image: {name + imageFile.is.open_!.fileName.takeRight(4)}</span>
        )
      }
    }
  }

	def list() = {
	  val photographs = Photograph.findAll(By(Photograph.user, User.currentUser))
	  "#photographs" #> photographs.map(photograph => "#image" #> <img src={photograph.image_path} />)  
	}
}

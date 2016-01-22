package com.kor

import com.typesafe.config.ConfigException.Missing
import com.typesafe.config.{Config, ConfigFactory}

import scalaz.syntax.std.boolean._


class Settings(val config: Config) {

  val appConfig = config.getConfig("votingApp")
  import appConfig._

  val host = getString("host")
  val port = getInt("port")
  val dbHost = getString("db.host")
  val dbPort = getString("db.port")
  val dbName = getString("db.dbName")
  val dropBoxToken = {
    val token = getString("dropBox.token")
    if (token.isEmpty) throw new Missing("dropBox.token") else token
  }
  val dropBoxFolder = hasPath("dropBox.folder").option(getString("dropBox.folder"))
  val dropBoxUploadUrl = s"${getString(s"dropBox.uploadUrl")}${dropBoxFolder.getOrElse("")}"
  val dropBoxMetaDataUrl = s"${getString("dropBox.metaDataUrl")}${dropBoxFolder.getOrElse("")}"

}
object Settings extends Settings(ConfigFactory.load)

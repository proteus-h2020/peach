/*
 * Copyright (C) 2017 The Proteus Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.proteus.peach.server.config

import java.util.function.Function
import java.util.stream.Collectors
import java.util.{List => JList}

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory

import scala.collection.JavaConversions.seqAsJavaList

object PeachServerConfig {
  /**
   * Default config values.
   */
  lazy val DefaultConfig = new PeachServerConfig {
    /**
     * Server actor name.
     */
    override val serverName: String = "PeachServer"

    /**
     * List of contact points.
     */
    override val contactPoints: JList[String] = List("127.0.0.1:2552")
    /**
     * Listening address.
     */
    override val hostname: String = "127.0.0.1"
    /**
     * Listening port.
     */
    override val port: Int = 2552

    /**
     * Akka default config.
     */
    override val akkaConfig: String = "default-server"
  }

  /**
   * Resolve akka config.
   *
   * @param serverConfig The server config.
   * @return The Config value.
   */
  def createAkkaConfig(serverConfig: PeachServerConfig): Config = {
    val fileConfig = ConfigFactory.load().getConfig(serverConfig.akkaConfig)

    val contactPoints = serverConfig.contactPoints.stream().map[String](new Function[String, String] {
      override def apply(t: String): String = {
        s"akka.tcp://${serverConfig.serverName}@$t"
      }
    }).collect(Collectors.toList[String])

    fileConfig
      .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(serverConfig.hostname))
      .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(serverConfig.port))
      .withValue("akka.cluster.seed-nodes", ConfigValueFactory.fromIterable(contactPoints))
  }
}

trait PeachServerConfig {

  /**
   * Akka default config.
   */
  val akkaConfig: String

  /**
   * Listening address.
   */
  val hostname: String


  /**
   * Listening port.
   */
  val port: Int

  /**
   * Server actor name.
   */
  val serverName: String

  /**
   * List of contact points.
   */
  val contactPoints: JList[String]


}

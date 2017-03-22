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

package com.proteus.peach.client.config

import java.util.concurrent.TimeUnit
import java.util.{List => JList}

import scala.collection.JavaConversions.seqAsJavaList
import scala.concurrent.duration.Duration

/**
 * Companion object fo PeachClientConfig.
 */
object PeachClientConfig {
  /**
   * Default config values.
   */
  lazy val DefaultConfig = new PeachClientConfig {
    /**
     * Address of receptor.
     */
    override val receptorAddress: String = "/user/comm"
    /**
     * Request timeout.
     */
    override val timeout: Duration = Duration(1, TimeUnit.SECONDS)

    /**
     * Akka default config.
     */
    override val akkaConfig: String = "default-client"

    /**
     * Client actor system name.
     */
    override val clientName: String = "PeachClient"

    /**
     * Server actor name.
     */
    override val serverName: String = "PeachServer"

    /**
     * Address of the receptionist actor.
     */
    override val receptionistAddress: String = "/user/receptionist"

    /**
     * List of contact points.
     */
    override val contactPoints: JList[String] = List("127.0.0.1:2552")
  }
}


/**
 * Client config.
 */
trait PeachClientConfig {
  /**
   * Client actor system name.
   */
  val clientName: String

  /**
   * Address of receptor.
   */
  val receptorAddress: String

  /**
   * Request timeout.
   */
  val timeout: Duration

  /**
   * Akka default config.
   */
  val akkaConfig: String

  /**
   * Server actor name.
   */
  val serverName: String

  /**
   * Address of the receptionist actor.
   */
  val receptionistAddress: String

  /**
   * List of contact points.
   */
  val contactPoints: JList[String]
}

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

package com.proteus.peach.redis.manager

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

/**
 * Factory to create Redis sessions using HOCON files.
 */
object HoconRedisSessionConfig {

  /**
   * Default path in the Hocon file.
   */
  val DefaultConfigPath: String = "com.proteus.peach.redis.manager.session"

  /**
   * The path for the session identifier.
   */
  val IdPath: String = "id"

  /**
   * The path for the session addresses.
   */
  val AddressesPath: String = "addresses"

  /**
   * The path for the session port.
   */
  val PortPath: String = "port"


  /**
   * Get the redis session described by the configuration.
   *
   * @param config     The HOCON config.
   * @param configPath The configuration path.
   * @return The resulting session.
   */
  def getSession(config: Config = ConfigFactory.load,
    configPath: String = HoconRedisSessionConfig.DefaultConfigPath): BasicRedisSession = {
    val sessionConfig = config.getConfig(configPath)

    val id = sessionConfig.getString(HoconRedisSessionConfig.IdPath)
    val addresses = sessionConfig.getStringList(HoconRedisSessionConfig.AddressesPath)
    val port = sessionConfig.getInt(HoconRedisSessionConfig.PortPath)
    new BasicRedisSession(id, addresses, port)
  }
}

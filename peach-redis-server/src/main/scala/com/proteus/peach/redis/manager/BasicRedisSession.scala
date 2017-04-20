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

import java.util.{Arrays => JArrays}
import java.util.{List => JList}

object BasicRedisSession {

  /**
   * Redis default port.
   */
  val RedisDefaultPort: Int = 6379


  /**
   * Create a redis session using the default port.
   *
   * @param id   The session identifier.
   * @param host The target host.
   * @param port The target port.
   * @return The BasicRedisSession instance.
   */
  def apply(id: String, host: String, port: Int): BasicRedisSession = {
    BasicRedisSession(id, JArrays.asList(host), port)
  }

}

/**
 * Class that contains the information required for establishing a Redis session.
 *
 * @param id        The session identifier.
 * @param addresses The list of addresses.
 * @param port      The port for incoming Redis clients.
 */
case class BasicRedisSession(id: String, addresses: JList[String], port: Int = BasicRedisSession.RedisDefaultPort)

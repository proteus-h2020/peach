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

package com.proteus.peach.server.comm

import akka.actor.Actor
import akka.actor.Props
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.Put
import com.proteus.peach.server.cache.ServerCache

/**
 * Companion object for AkkaServerReceptor class.
 */
object AkkaServerReceptor {
  /**
   * Create props for a AkkaServerReceptor actor.
   *
   * @param cacheServer The associated cache server.
   * @return Prop for a this actor.
   */
  def props(cacheServer: ServerCache): Props = Props(new AkkaServerReceptor(cacheServer))
}

/**
 * Cache reception point.
 *
 * @param cacheServer Cache server implementation.
 */
class AkkaServerReceptor(cacheServer: ServerCache) extends Actor {

  /**
   * This defines the initial actor behavior, it must return a partial function
   * with the actor logic.
   *
   * @return Response message.
   */
  override def receive: Receive = {
    case Put(key, value) => sender ! this.cacheServer.put(key, value)
    case Get(key) => sender ! this.cacheServer.get(key)
  }
}

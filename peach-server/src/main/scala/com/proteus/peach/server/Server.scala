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

package com.proteus.peach.server

import akka.actor.ActorSystem
import akka.contrib.pattern.ClusterReceptionistExtension
import com.proteus.peach.server.cache.MockupServerCache
import com.proteus.peach.server.cache.ServerCache
import com.proteus.peach.server.comm.AkkaServerReceptor
import com.proteus.peach.server.config.ServerConfig

/**
 * Server cache launcher.
 *
 * @param serverCache Instance of the server cache.
 * @param config      Configuration instance.
 */
class Server(serverCache: ServerCache = new MockupServerCache(), config: ServerConfig = ServerConfig.DefaultConfig) {

  /**
   * System actor.
   */
  private val system = ActorSystem(config.serverName)

  /**
   * Init method.
   */
  def init(): Unit = {
    val comm = system.actorOf(AkkaServerReceptor.props(serverCache), "comm")
    ClusterReceptionistExtension(system).registerService(comm)
  }

  /**
   * Stop method.
   */
  def stop(): Unit = {
    system.shutdown()
  }
}

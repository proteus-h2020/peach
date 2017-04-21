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

package com.proteus.peach.client

import java.util.concurrent.TimeUnit

import com.proteus.peach.server.PeachServer
import org.junit.AfterClass
import org.junit.BeforeClass

import scala.concurrent.duration.Duration

object PeachAkkaClientIT {
  /**
   * Server instance.
   */
  lazy val Server: PeachServer = new PeachServer()

  /**
   * Init cache server.
   */
  @BeforeClass
  def beforeAll(): Unit = {
    Server.init()
    Server.run()
  }

  /**
   * Stop cache server.
   */
  @AfterClass
  def afterAll(): Unit = {
    Server.shutdown()
  }
}

class PeachAkkaClientIT extends PeachClientValidator {
  /**
   * Client cache to test.
   */
  override lazy val clientCache: PeachClient = PeachAkkaClient()
}

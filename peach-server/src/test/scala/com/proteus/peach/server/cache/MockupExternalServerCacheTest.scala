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

package com.proteus.peach.server.cache

import org.junit.AfterClass
import org.junit.BeforeClass

object MockupExternalServerCacheTest {
  /**
   * Cache server.
   */
  lazy val CacheServer: ExternalServerCache = new MockupExternalServerCache()

  /**
   * Init cache server.
   */
  @BeforeClass
  def beforeAll(): Unit = {
    CacheServer.init()
  }

  /**
   * Stop cache server.
   */
  @AfterClass
  def afterAll(): Unit = {
    CacheServer.stop()
  }
}

class MockupExternalServerCacheTest extends ExternalCacheValidator {
  /**
   * Cache server instance.
   */
  override val cacheServer: ExternalServerCache = MockupExternalServerCacheTest.CacheServer
}

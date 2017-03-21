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

import org.junit.Assert
import org.junit.Test

/**
 * Cache server logic validator.
 */
trait CacheValidator {

  /**
   * Cache server instance.
   */
  val cacheServer: ServerCache

  /**
   * Simple test put and get key.
   */
  @Test
  def basicTest(): Unit = {
    val key = "basicTestKey"
    val value = "basicTestValue"
    this.cacheServer.put(key, value)
    val response = this.cacheServer.get(key)
    Assert.assertNotNull("Response must no be null", response)
    Assert.assertTrue("Response value must be defined.",response.value.isDefined)
    Assert.assertEquals("Response value must be [value]",response.value.get,value)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyTest():Unit={
    val key = "notExistKeyTestKey"
    val response = this.cacheServer.get(key)
    Assert.assertNotNull("Response must no be null", response)
    Assert.assertTrue("Response value must be empty.",response.value.isEmpty)
  }
}

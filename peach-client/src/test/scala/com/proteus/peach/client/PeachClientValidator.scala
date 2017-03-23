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

import org.junit.After
import org.junit.Assert
import org.junit.Test

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Client cache validator.
 */
trait PeachClientValidator {
  /**
   * Client cache to test.
   */
  val clientCache: PeachClient

  /**
   * Remove al the entries in the cache.
   */
  @After
  def invalidateCache(): Unit = {
    this.clientCache.invalidateAll()
    Assert.assertEquals("The size must be 0", 0L, this.clientCache.size())
  }

  /**
   * Simple test put and get key.
   */
  @Test
  def basicTest(): Unit = {
    val key = "basicTestKey"
    val value = "basicTestValue"
    this.clientCache.put(key, value)
    val response = this.clientCache.get(key)
    Assert.assertNotNull("Response must no be null", response)
    Assert.assertTrue("Response value must be defined.", response.isDefined)
    Assert.assertEquals("Response value must be [value]", response.get, value)
    Assert.assertEquals("The size must be 1", 1L, this.clientCache.size())
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyTest(): Unit = {
    val key = "notExistKeyTestKey"
    val response = this.clientCache.get(key)
    Assert.assertNotNull("Response must no be null", response)
    Assert.assertTrue("Response value must be empty.", response.isEmpty)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def createAndInvalidateTest(): Unit = {
    val key = "createAndInvalidateKey"
    val value = "createAndInvalidateValue"
    this.clientCache.put(key, value)
    val response1 = this.clientCache.get(key)
    Assert.assertNotNull("Response must no be null", response1)
    Assert.assertTrue("Response value must be defined.", response1.isDefined)
    Assert.assertEquals("Response value must be [value]", response1.get, value)
    Assert.assertEquals("The size must be 1", 1L, this.clientCache.size())
    this.clientCache.invalidate(key)
    val response2 = this.clientCache.get(key)
    Assert.assertNotNull("Response must no be null", response2)
    Assert.assertTrue("Response value must be empty.", response2.isEmpty)
    Assert.assertEquals("The size must be 0", 0L, this.clientCache.size())
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test(expected = classOf[IllegalArgumentException])
  def createWithNullKeyTest(): Unit = {
    val key = None.orNull
    val value = "createAndInvalidateValue"
    this.clientCache.put(key, value)
  }

  /**
   * Simple test put and get key.
   */
  @Test
  def basicFutureTest(): Unit = {
    val key = "basicFutureKey"
    val value = "basicFutureValue"
    this.clientCache.put(key, value)
    val response = this.clientCache.getAsync(key)
    val result = Await.result(response, Duration(1, TimeUnit.SECONDS))

    Assert.assertNotNull("Response must no be null", result)
    Assert.assertTrue("Response value must be defined.", result.isDefined)
    Assert.assertEquals("Response value must be [value]", result.get, value)
    Assert.assertEquals("The size must be 1", 1L, this.clientCache.size())
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyWithFutureTest(): Unit = {
    val key = "notExistKeyWithFutureTestKey"
    val response = this.clientCache.getAsync(key)

    val result = Await.result(response, Duration(1, TimeUnit.SECONDS))

    Assert.assertNotNull("Response must no be null", result)
    Assert.assertTrue("Response value must be empty.", result.isEmpty)
    Assert.assertEquals("The size must be 0", 0L, this.clientCache.size())
  }


}

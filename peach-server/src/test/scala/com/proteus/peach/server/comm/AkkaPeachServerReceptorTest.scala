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

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.Invalidate
import com.proteus.peach.common.comm.PeachServerMessage.InvalidateAll
import com.proteus.peach.common.comm.PeachServerMessage.Put
import com.proteus.peach.common.comm.PeachServerMessage.Size
import com.proteus.peach.common.comm.PeachServerMessage.SizeResponse
import com.proteus.peach.server.cache.MockupExternalServerCache
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Test

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
 * Test of peach receptor.
 */
class AkkaPeachServerReceptorTest {
  /**
   * Implicit actor system.
   */
  implicit val system = ActorSystem()

  /**
   * Timeout duration.
   */
  val duration: FiniteDuration = FiniteDuration(1, TimeUnit.SECONDS)

  /**
   * Implicit timeout val.
   */
  implicit val timeout = Timeout(duration)

  /**
   * Mockup cache server.
   */
  val cacheServer = new MockupExternalServerCache()

  /**
   * Tested receptor.
   */
  val receptor = TestActorRef(AkkaPeachServerReceptor.props(cacheServer))

  /**
   * Remove all the entries in the cache.
   */
  @After
  def invalidateCache(): Unit = {
    val invalidateFuture = receptor ? InvalidateAll()
    Await.result(invalidateFuture, this.duration)

    val sizeFuture = receptor ? Size()
    val sizeResponse = Await.result(sizeFuture, this.duration).asInstanceOf[SizeResponse]
    Assert.assertEquals("The size must be 0", 0L, sizeResponse.value)
  }


  /**
   * Simple test put and get key.
   */
  @Test
  def basicTest(): Unit = {
    val key = "basicTestKey"
    val value = "basicTestValue"

    val putResponseFuture = receptor ? Put(key, value)
    val putResponse = Await.result(putResponseFuture, this.duration)

    val getResponseFuture = receptor ? Get(key)
    val getResponse = Await.result(getResponseFuture, this.duration)


    Assert.assertNotNull("ResponsePut must no be null", putResponse)
    Assert.assertNotNull("ResponseGet must no be null", getResponse)
    Assert.assertThat("Must be a ResponseGet", getResponse, CoreMatchers.instanceOf[Any](classOf[GetResponse]))
    Assert.assertTrue("Response value must be defined.", getResponse.asInstanceOf[GetResponse].value.isDefined)
    Assert.assertEquals("Response value must be [value]", getResponse.asInstanceOf[GetResponse].value.get, value)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyTest(): Unit = {
    val key = "notExistKeyTestKey"
    val getResponseFuture = receptor ? Get(key)
    val getResponse = Await.result(getResponseFuture, this.duration)

    Assert.assertNotNull("ResponseGet must no be null", getResponse)
    Assert.assertThat("Must be a ResponseGet", getResponse, CoreMatchers.instanceOf[Any](classOf[GetResponse]))
    Assert.assertTrue("Response value must be empty.", getResponse.asInstanceOf[GetResponse].value.isEmpty)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def createAndInvalidateTest(): Unit = {
    val key = "createAndInvalidateKey"
    val value = "createAndInvalidateValue"
    val putResponseFuture = receptor ? Put(key, value)
    val putResponse = Await.result(putResponseFuture, this.duration)

    val getResponseFuture = receptor ? Get(key)
    val getResponse = Await.result(getResponseFuture, this.duration)

    Assert.assertNotNull("ResponsePut must no be null", putResponse)
    Assert.assertNotNull("ResponseGet must no be null", getResponse)
    Assert.assertThat("Must be a ResponseGet", getResponse, CoreMatchers.instanceOf[Any](classOf[GetResponse]))
    Assert.assertTrue("Response value must be defined.", getResponse.asInstanceOf[GetResponse].value.isDefined)
    Assert.assertEquals("Response value must be [value]", getResponse.asInstanceOf[GetResponse].value.get, value)

    val invalidateFuture = receptor ? Invalidate(key)
    val invalidateResponse = Await.result(invalidateFuture, this.duration)

    Assert.assertNotNull("Invalidate Response must not be null", invalidateResponse)

    val getResponseFuture2 = receptor ? Get(key)
    val getResponse2 = Await.result(getResponseFuture2, this.duration)
    Assert.assertNotNull("ResponseGet must no be null", getResponse2)
    Assert.assertThat("Must be a ResponseGet", getResponse2, CoreMatchers.instanceOf[Any](classOf[GetResponse]))
    Assert.assertTrue("Response value must be empty.", getResponse2.asInstanceOf[GetResponse].value.isEmpty)

  }
}

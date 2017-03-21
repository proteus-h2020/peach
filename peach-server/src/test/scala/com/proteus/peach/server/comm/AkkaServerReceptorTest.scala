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
import akka.testkit.TestActorRef
import org.junit.Assert
import org.junit.Test

import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.Put
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse
import com.proteus.peach.server.cache.MockupServerCache

import scala.util.Success

/**
 * Test of peach receptor.
 */
class AkkaServerReceptorTest {
  /**
   * Implicit actor system.
   */
  implicit val system = ActorSystem()

  /**
   * Implicit timeout val.
   */
  implicit val timeout = Timeout(1,TimeUnit.SECONDS)

  /**
   * Mockup cache server.
   */
  val cacheServer = new MockupServerCache()

  /**
   * Tested receptor.
   */
  val receptor = TestActorRef(AkkaServerReceptor.props(cacheServer))


  /**
   * Simple test put and get key.
   */
  @Test
  def basicTest(): Unit = {
    val key = "basicTestKey"
    val value = "basicTestValue"

    val Success(responsePut:PutResponse) = (receptor ? Put(key,value)).value.get
    val Success(responseGet:GetResponse)= (receptor ? Get(key)).value.get
    Assert.assertNotNull("ResponsePut must no be null", responsePut)
    Assert.assertNotNull("ResponseGet must no be null", responseGet)
    Assert.assertTrue("Response value must be defined.",responseGet.value.isDefined)
    Assert.assertEquals("Response value must be [value]",responseGet.value.get,value)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyTest():Unit={
    val key = "notExistKeyTestKey"
    val Success(responseGet:GetResponse)= (receptor ? Get(key)).value.get
    Assert.assertNotNull("Response must no be null", responseGet)
    Assert.assertTrue("Response value must be empty.",responseGet.value.isEmpty)
  }
}

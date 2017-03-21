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

import java.util.concurrent.TimeUnit

import akka.actor.ActorSelection
import akka.pattern.ask
import akka.util.Timeout
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.Put
import org.hamcrest.CoreMatchers
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.util.Success

object ServerTest {
  /**
   * Server instance.
   */
  lazy val Server: Server = new Server()

  /**
   * Init cache server.
   */
  @BeforeClass
  def beforeAll(): Unit = {
    Server.init()
  }

  /**
   * Stop cache server.
   */
  @AfterClass
  def afterAll(): Unit = {
    Server.stop()
  }
}

class ServerTest {
  /**
   * Selector of the comm actor.
   */
  val commActor: ActorSelection = ServerTest.Server.system.actorSelection("/user/comm")


  /**
   * Timeout duration.
   */
  val duration: FiniteDuration = FiniteDuration(1, TimeUnit.SECONDS)

  /**
   * Implicit timeout val.
   */
  implicit val timeout = Timeout(duration)


  /**
   * Simple test put and get key.
   */
  @Test
  def basicTest(): Unit = {
    val key = "basicTestKey"
    val value = "basicTestValue"

    val putResponseFuture = commActor ? Put(key, value)
    val putResponse = Await.result(putResponseFuture, this.duration)

    val getResponseFuture = commActor ? Get(key)
    val getResponse = Await.result(getResponseFuture, this.duration)


    Assert.assertNotNull("ResponsePut must no be null", putResponse)
    Assert.assertNotNull("ResponseGet must no be null", getResponse)
    Assert.assertThat("Must be a ResponseGet",getResponse, CoreMatchers.instanceOf[Any](classOf[GetResponse]))
    Assert.assertTrue("Response value must be defined.", getResponse.asInstanceOf[GetResponse].value.isDefined)
    Assert.assertEquals("Response value must be [value]", getResponse.asInstanceOf[GetResponse].value.get, value)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyTest(): Unit = {
    val key = "notExistKeyTestKey"
    val getResponseFuture = commActor ? Get(key)
    val getResponse = Await.result(getResponseFuture, this.duration)

    Assert.assertNotNull("ResponseGet must no be null", getResponse)
    Assert.assertThat("Must be a ResponseGet",getResponse, CoreMatchers.instanceOf[Any](classOf[GetResponse]))
    Assert.assertTrue("Response value must be empty.", getResponse.asInstanceOf[GetResponse].value.isEmpty)
  }

}

package com.proteus.peach.server.comm

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.proteus.peach.test.server.MockupCacheServer
import org.junit.Assert
import org.junit.Test

import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.Put
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse

import scala.util.Success

/**
 * Test of peach receptor.
 */
class PeachServerReceptorTest {
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
  val cacheServer = new MockupCacheServer()

  /**
   * Tested receptor.
   */
  val receptor = TestActorRef(PeachServerReceptor.props(cacheServer))


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

package com.proteus.peach.server.comm

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.proteus.peach.test.server.MockupCacheServer

/**
 * Test of peach receptor.
 */
class PeachServerReceptorTest {
  /**
   * Implicit actor system.
   */
  implicit val system = ActorSystem()

  /**
   * Mockup cache server.
   */
  val cacheServer = new MockupCacheServer()

  /**
   * Tested receptor.
   */
  val receptor = TestActorRef(PeachServerReceptor.props(cacheServer))
}

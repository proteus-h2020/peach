package com.proteus.peach.server.comm

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.actor.Props
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.Put
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse
import com.proteus.peach.common.server.Cache

/**
 * Companion object for PeachServerReceptor class.
 */
object PeachServerReceptor{
  /**
   * Create props for a PeachServerReceptor actor.
   * @param cacheServer The associated cache server.
   * @return Prop for a this actor.
   */
  def props(cacheServer: Cache): Props = Props(new PeachServerReceptor(cacheServer))
}

/**
 * Cache reception point.
 *
 * @param cacheServer Cache server implementation.
 */
class PeachServerReceptor(cacheServer: Cache) extends Actor {

  /**
   * This defines the initial actor behavior, it must return a partial function
   * with the actor logic.
   *
   * @return Response message.
   */
  override def receive: Receive = {
    case Put(key, value) => sender ! this.cacheServer.put(key, value)
    case Get(key) => sender ! this.cacheServer.get(key)
  }
}

package com.proteus.peach.client

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.contrib.pattern.ClusterClient
import akka.pattern.ask
import com.proteus.peach.client.config.PeachClientConfig
import com.proteus.peach.common.comm.PeachServerMessage.Get
import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.Put
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.TimeoutException

/**
 * PeachClientCache companion object.
 */
object AkkaClientCache {

  /**
   * Create a PeachClientCache init the actor system.
   *
   * @param config Peach client config.
   * @return The cache object.
   */
  def apply(config: PeachClientConfig = PeachClientConfig.DefaultConfig): PeachClientCache = {
    val system = ActorSystem(config.clientName, ConfigFactory.load().getConfig(config.akkaConfig))

    val contactPoints = config.contactPoints.toArray[String](new Array[String](config.contactPoints.size()))
    val initialContacts = contactPoints.map(point => {
      system.actorSelection(s"akka.tcp://${config.serverName}@${point}${config.receptionistAddress}")
    }).toSet

    val client = system.actorOf(ClusterClient.props(initialContacts))

    new AkkaClientCache(client, PeachClientConfig.DefaultConfig)
  }

}


/**
 * Proxy class to cluster client cache.
 *
 * @param clusterClient Cluster client actor.
 * @param config        Configuration properties.
 */
class AkkaClientCache(clusterClient: ActorRef, config: PeachClientConfig) {

  /**
   * Request timeout.
   */
  private implicit val timeout = this.config.timeout

  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   * @throws InterruptedException if the current thread is interrupted while waiting
   * @throws TimeoutException     if after waiting for the specified time `awaitable` is still not ready
   */
  @throws(classOf[TimeoutException])
  @throws(classOf[InterruptedException])
  def put(key: String, value: String): Unit = {
    clusterClient ? ClusterClient.Send(this.config.receptorAddress, Put(key, value),
      localAffinity = true) match {
      case future: Future[PutResponse] => {
        Await.result(future, timeout)
      }
      case _ => {
        throw new UnsupportedOperationException("Receptor has sent an unexpected message.")
      }
    }

  }

  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   * @throws InterruptedException if the current thread is interrupted while waiting
   * @throws TimeoutException     if after waiting for the specified time `awaitable` is still not ready
   */
  @throws(classOf[TimeoutException])
  @throws(classOf[InterruptedException])
  def get(key: String): Option[String] = {
    clusterClient ? ClusterClient.Send(this.config.receptorAddress, Get(key),
      localAffinity = true) match {
      case future: Future[GetResponse] => {
        val result = Await.result(future, timeout)
        result.value
      }
      case _ => {
        throw new UnsupportedOperationException("Receptor has sent an unexpected message.")
      }
    }
  }
}


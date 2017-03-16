package com.proteus.peach.test.server

import java.util.{HashMap => JHashMap}
import java.util.{Map => JMap}

import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse
import com.proteus.peach.common.server.CacheServer


object MockupCacheServer{
  /**
   * Internal in memory cache.
   */
  val cache: JMap[String, String] = new JHashMap[String, String]()

}

/**
 * Mockup cache server.
 *
 * @param name Name of the server.
 */
class MockupCacheServer(name: String) extends CacheServer {

  /**
   * Constructor without parameters.
   *
   * @return A object with default name.
   */
  def this() = this("default")

  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   */
  override def put(key: String, value: String): PutResponse = {
    MockupCacheServer.cache.put(key, value)
    PutResponse()
  }

  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  override def get(key: String): GetResponse = {
    GetResponse(Option(MockupCacheServer.cache.get(key)))
  }
}


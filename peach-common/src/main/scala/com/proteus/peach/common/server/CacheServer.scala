package com.proteus.peach.common.server

import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse

import scala.concurrent.Future

/**
 * Trait with the cache methods.
 */
trait CacheServer {
  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   */
  def put(key: String, value: String): PutResponse


  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  def get(key: String): GetResponse
}

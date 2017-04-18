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

package com.proteus.peach.redis.server

import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.InvalidateResponse
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse
import com.proteus.peach.common.comm.PeachServerMessage.SizeResponse
import com.proteus.peach.server.cache.ExternalServerCache

class RedisExternalServerCache extends ExternalServerCache {

  /**
   * Redis cache provider.
   */
  var provider: Option[RedisExternalServerProvider] = None

  /**
   * Init signal.
   */
  override def init(): Unit = {

  }

  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   */
  override def put(key: String, value: String): PutResponse = {
    this.checkProvider()
    this.provider.get.put(key, value)
    PutResponse()
  }

  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  override def get(key: String): GetResponse = {
    this.checkProvider()
    val op = this.provider.get.get(key)
    GetResponse(op)
  }

  /**
   * Discards any cached value for key key.
   *
   * @param key Searched key.
   * @return Invalidate response.
   */
  override def invalidate(key: String): InvalidateResponse = {
    this.checkProvider()
    this.provider.get.delete(key)
    InvalidateResponse()
  }

  /**
   * Check if the parameter is defined.
   */
  private def checkProvider(): Unit = {
    if (this.provider.isEmpty) {
      throw new IllegalStateException("The Jedis client is not defined.")
    }
  }

  /**
   * Discards all entries in the cache.
   *
   * @return Invalidate response.
   */
  override def invalidateAll(): InvalidateResponse = {
    this.checkProvider()
    this.provider.get.flush()
    InvalidateResponse()
  }

  /**
   * Returns the approximate number of entries in this cache.
   *
   * @return The approximate number of entries.
   */
  override def size(): SizeResponse = {
    this.checkProvider()
    val size = this.provider.get.size()
    SizeResponse(size.getOrElse(-1L))
  }

  /**
   * Stop signal.
   */
  override def stop(): Unit = {

  }
}

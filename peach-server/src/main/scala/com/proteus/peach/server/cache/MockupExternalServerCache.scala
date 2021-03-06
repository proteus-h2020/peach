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

package com.proteus.peach.server.cache

import java.util.{HashMap => JHashMap}
import java.util.{Map => JMap}

import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.InvalidateResponse
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse
import com.proteus.peach.common.comm.PeachServerMessage.SizeResponse


/**
 * Mockup cache server.
 *
 * @param name Name of the server.
 */
class MockupExternalServerCache(name: String) extends ExternalServerCache {
  /**
   * Internal in memory cache.
   */

  val cache: JMap[String, String] = new JHashMap[String, String]()

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
    if (Option(key).isEmpty) {
      throw new IllegalArgumentException("The key must not be NULL.")
    }
    this.cache.put(key, value)
    PutResponse()
  }

  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  override def get(key: String): GetResponse = {
    GetResponse(Option(this.cache.get(key)))
  }

  /**
   * Init signal.
   */
  override def init(): Unit = {}

  /**
   * Stop signal.
   */
  override def stop(): Unit = {}

  /**
   * Discards any cached value for key key.
   *
   * @param key Searched key.
   * @return Invalidate response.
   */
  override def invalidate(key: String): InvalidateResponse = {
    this.cache.remove(key)
    InvalidateResponse()
  }

  /**
   * Discards all entries in the cache.
   *
   * @return Invalidate response.
   */
  override def invalidateAll(): InvalidateResponse = {
    this.cache.clear()
    InvalidateResponse()
  }

  /**
   * Returns the approximate number of entries in this cache.
   *
   * @return The approximate number of entries.
   */
  override def size(): SizeResponse = {
    SizeResponse(this.cache.size())
  }
}



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
import com.proteus.peach.redis.manager.BasicRedisSession
import com.proteus.peach.redis.manager.HoconRedisSessionConfig
import com.proteus.peach.redis.manager.RedisSessionManager
import com.proteus.peach.redis.server.RedisExternalServerCache.Log
import com.proteus.peach.server.cache.ExternalServerCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object RedisExternalServerCache {

  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)
}

/**
 * Redis external cache server.
 *
 * @param basicRedisSession Basic redis session.
 */
class RedisExternalServerCache(basicRedisSession: BasicRedisSession = HoconRedisSessionConfig.getSession())
  extends ExternalServerCache {

  /**
   * Redis cache provider.
   */
  lazy val provider: RedisExternalServerProvider = new RedisExternalServerProvider(basicRedisSession.id)

  /**
   * Init signal.
   */
  override def init(): Unit = {
    if (RedisSessionManager.init(basicRedisSession)) {
      Log.info(s"Connected to ${basicRedisSession.id}...")
    } else {
      throw new UnknownError("Impossible to connect with REDIS.")
    }
  }


  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   */
  override def put(key: String, value: String): PutResponse = {
    if(Option(key).isEmpty){
      throw new IllegalArgumentException("The key is null.")
    }
    this.provider.put(key, value)
    PutResponse()
  }

  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  override def get(key: String): GetResponse = {
    val op = this.provider.get(key)
    GetResponse(op)
  }

  /**
   * Discards any cached value for key key.
   *
   * @param key Searched key.
   * @return Invalidate response.
   */
  override def invalidate(key: String): InvalidateResponse = {
    this.provider.delete(key)
    InvalidateResponse()
  }

  /**
   * Discards all entries in the cache.
   *
   * @return Invalidate response.
   */
  override def invalidateAll(): InvalidateResponse = {
    this.provider.flush()
    InvalidateResponse()
  }


  /**
   * Returns the approximate number of entries in this cache.
   *
   * @return The approximate number of entries.
   */
  override def size(): SizeResponse = {
    val size = this.provider.size()
    SizeResponse(size.getOrElse(-1L))
  }

  /**
   * Stop signal.
   */
  override def stop(): Unit = {
    RedisSessionManager.close(basicRedisSession.id)
    RedisSessionManager.shutdown()
    Log.info(s"Disconnected to ${basicRedisSession.id}.")
  }
}

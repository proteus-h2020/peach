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

import com.proteus.peach.redis.manager.AbstractRedisProvider

/**
 * Provider to handle the redis cache.
 *
 * @param sessionId The session identifier.
 */
class RedisExternalServerProvider(sessionId: String) extends AbstractRedisProvider(sessionId) {
  /**
   * Put a key/value pair into Redis.
   *
   * @param key   Key parameter.
   * @param value The value.
   */
  def put(key: String,
    value: String): Unit = {
    this.checkClient()
    this.executeOperationWithRetries(() => {
      this.client.get.set(key, value)
    })
  }

  /**
   * Get a value from Redis.
   *
   * @param key Key parameter.
   * @return The value.
   */
  def get(key: String): Option[String] = {
    this.checkClient()
    this.executeOperationWithRetries(() => {
      this.client.get.get(key)
    })
  }

  /**
   * Delete a key in Redis.
   *
   * @param key Key parameter.
   */
  def delete(key: String): Unit = {
    this.checkClient()
    this.executeOperationWithRetries(() => {
      this.client.get.del(key)
    })
  }

  /**
   * Check if the parameter is defined.
   */
  private def checkClient(): Unit = {
    if (this.client.isEmpty) {
      throw new IllegalStateException("The Jedis client is not defined.")
    }
  }

  /**
   * Flush all the key in Redis.
   */
  def flush(): Unit = {
    this.checkClient()
    this.executeOperationWithRetries(() => {
      this.client.get.flushDB()
    })
  }

  /**
   * Number of keys in Redis.
   *
   * @return Number of keys.
   */
  def size(): Option[Long] = {
    this.checkClient()
    this.executeOperationWithRetries(() => {
      this.client.get.dbSize()
    })
  }

}

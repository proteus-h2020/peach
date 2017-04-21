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

package com.proteus.peach.redis.manager

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.exceptions.JedisException

import scala.util.Failure
import scala.util.Success
import scala.util.Try

object RedisHelper {

  /**
   * Environment variable to set the Redis host.
   */
  val HostEnvironmentVariable: String = "REDIS_HOST"

  /**
   * Environment variable to set the Redis port.
   */
  val PortEnvironmentVariable: String = "REDIS_PORT"

  /**
   * Default Redis port.
   */
  val DefaultPort: Int = 6379

  /**
   * Default host.
   */
  val DefaultHost: String = "localhost"
}

/**
 * Class that offers helper methods to write Redis related tests.
 *
 * @param host The host.
 * @param port The port.
 */
class RedisHelper(host: String = RedisHelper.DefaultHost, port: Int = RedisHelper.DefaultPort) {

  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)

  /**
   * The connection pool.
   */
  private var connectionPool: Option[JedisPool] = None

  /**
   * The Redis client.
   */
  private var client: Option[Jedis] = None

  /**
   * Creates an internal connection pool with Redis, and initializes the client that will be
   * used by this helper.
   * {{{
   *   val helper = new RedisHelper()
   *   helper.connect()
   *   ...
   *   helper.close()
   * }}}
   *
   * @return Whether it is possible to connect with Redis.
   */
  def connect(): Boolean = {
    val poolConfig = new JedisPoolConfig()
    this.connectionPool = Some(new JedisPool(poolConfig, this.host, this.port))
    Try {
      this.client = Option(this.connectionPool.get.getResource)
    } match {
      case Success(_) => {
        this.isAlive()
      }
      case Failure(error: JedisException) => {
        Log.error(s"Cannot connect to redis ${this.host}:${this.port}", error)
        false
      }
    }
  }

  /**
   * Check whether Redis is alive.
   *
   * @return Whether is alive or not.
   */
  def isAlive(): Boolean = {
    this.client match {
      case Some(instance) => instance.isConnected
      case None => false
    }
  }

  /**
   * Close the connection with Redis.
   */
  def close(): Unit = {
    this.client.foreach(_.close())

    this.connectionPool.foreach(_.destroy())

  }

  /**
   * Clean all databases in Redis.
   */
  def flushAll(): Unit = {
    this.client.foreach(_.flushAll())
  }

}
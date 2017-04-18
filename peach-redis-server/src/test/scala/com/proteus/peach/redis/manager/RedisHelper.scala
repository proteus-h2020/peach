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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.exceptions.JedisConnectionException

object RedisHelper {

  /**
   * Environment variable to set the Redis host.
   */
  val HostEnvironmentVariable : String = "REDIS_HOST"

  /**
   * Environment variable to set the Redis port.
   */
  val PortEnvironmentVariable : String = "REDIS_PORT"

  /**
   * Default Redis port.
   */
  val DefaultPort : Int = 6379

  /**
   * Default host.
   */
  val DefaultHost : String = "localhost"
}

/**
 * Class that offers helper methods to write Redis related tests.
 * @param host The host.
 * @param port The port.
 */
class RedisHelper(host: String = RedisHelper.DefaultHost, port : Int = RedisHelper.DefaultPort) {

  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)

  /**
   * The connection pool.
   */
  private var connectionPool : Option[JedisPool] = None

  /**
   * The Redis client.
   */
  private var client : Option[Jedis] = None

  /**
   * Creates an internal connection pool with Redis, and initializes the client that will be
   * used by this helper.
   * {{{
   *   val helper = new RedisHelper()
   *   helper.connect()
   *   ...
   *   helper.close()
   * }}}
   * @return Whether it is possible to connect with Redis.
   */
  def connect() : Boolean = {
    val poolConfig = new JedisPoolConfig()
    this.connectionPool = Some(new JedisPool(poolConfig, this.host, this.port))
    try {
      this.client = Option(this.connectionPool.get.getResource)
      this.isAlive()
    }catch {
      case e: JedisConnectionException => {
        Log.error(s"Cannot connect to redis ${this.host}:${this.port}", e)
        false
      }
    }
  }

  /**
   * Close the connection with Redis.
   */
  def close() : Unit = {
    if(this.client.isDefined){
      this.client.get.close()
    }
    if(this.connectionPool.isDefined){
      this.connectionPool.get.destroy()
    }
  }

  /**
   * Check whether Redis is alive.
   * @return Whether is alive or not.
   */
  def isAlive() : Boolean = {
    if(this.client.isDefined){
      this.client.get.isConnected
    }else{
      false
    }
  }

  /**
   * Clean all databases in Redis.
   */
  def flushAll() : Unit = {
    this.client.get.flushAll()
  }

  /**
   * Set a key-value entry.
   * @param key The key.
   * @param value The value.
   */
  def set(key: String, value: String) : Unit = {
    this.client.get.set(key, value)
  }

  /**
   * Get the value associated with a key.
   * @param key The key.
   * @return An option with the value.
   */
  def get(key: String) : Option[String] = {
    Option(this.client.get.get(key))
  }

  /**
   * Get a value from a hash map.
   * @param map The name of the map.
   * @param key The key.
   * @return An option with the value.
   */
  def hget(map: String, key: String) : Option[String] = {
    Option(this.client.get.hget(map, key))
  }

  /**
   * Assert that a key in a hashmap matches the expected value.
   * @param map The name of the map.
   * @param key The key in the map.
   * @param value The expected value.
   */
  def assertKeyEquals(map: String, key: String, value: String) : Unit = {
    val retrieved = this.hget(map, key)
    assertTrue("Map does not exists", retrieved.isDefined)
    assertEquals("Value does not match", value, retrieved.get)
  }

  /**
   * Remove a list of keys from redis.
   * @param keys The keys.
   */
  def removeKeys(keys: Seq[String]) : Unit = {
    keys.foreach(key => {
      this.client.get.del(key)
    })
  }

}
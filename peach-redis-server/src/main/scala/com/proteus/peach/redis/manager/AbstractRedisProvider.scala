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

object AbstractRedisProvider {
  /**
   * Default number of retries.
   */
  val DefaultMaxNumRetries = 5

  /**
   * Default sleeping time.
   */
  val DefaultSleepingTime = 5000
}

/**
 * Abstract Redis provider that contains the client to connect to Redis using a given
 * session.
 *
 * @param sessionId The session identifier.
 */
abstract class AbstractRedisProvider(sessionId: String) {
  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)

  /**
   * Client used to connect to Redis.
   */
  protected var client: Option[Jedis] = RedisSessionManager.getSession(this.sessionId)

  /**
   * Variable to indicate that the connection was fixed.
   */
  protected var fixed: Boolean = false

  /**
   * Execute an operation with retries.
   *
   * @param func          Operation to excecute.
   * @param maxNumRetries Numeber of max retries.
   * @param sleepingTime  Sleep time between retries.
   * @tparam T Return class.
   * @return None if the operation was not executed.
   */
  def executeOperationWithRetries[T](func: () => T,
    maxNumRetries: Int = AbstractRedisProvider.DefaultMaxNumRetries,
    sleepingTime: Int = AbstractRedisProvider.DefaultSleepingTime): Option[T] = {

    var toReturn: Option[T] = None
    var numRetries = 0
    while (numRetries < maxNumRetries) {
      try {
        toReturn = Option(func())
        numRetries=maxNumRetries
      } catch {
        case error: Exception => {
          Log.error("Error executind the Redis command count: " + error.toString)
          numRetries += 1
          Log.error(s"Retry ${numRetries} after ${sleepingTime}")
          setUnfixed()
          Thread.sleep(sleepingTime)
          reconnect()
        }
      }
    }
    toReturn

  }

  /**
   * This method forces the client reconnection.
   *
   * @return Boolean whether the reconnection was possible or not.
   */
  def reconnect(): Boolean = {
    this.synchronized {
      if (!fixed) {
        client = None
        Log.info(s"Reconnect Redis session ${this.sessionId}")
        val conf = RedisSessionManager.getConfiguration(this.sessionId)
        if (conf.isDefined) {
          RedisSessionManager.close(this.sessionId)
          if (RedisSessionManager.init(conf.get)) {
            client = RedisSessionManager.getSession(this.sessionId)
            if (client.isDefined) {
              Log.info("Redis session was successfully restored")
              setFixed()
              true
            } else {
              Log.error(s"Impossible to get a resource from the pool ${this.sessionId}")
              false
            }
          } else {
            Log.error(s"Impossible to initialize ${this.sessionId}")
            false
          }
        } else {
          Log.error(s"Unknown configuration for Redis session ${this.sessionId}.")
          false
        }
      } else {
        Log.info("The connection was already fixed.")
        true
      }
    }
  }

  /**
   * Set this connection as fixed.
   */
  def setFixed(): Unit = {
    this.synchronized {
      fixed = true
    }
  }

  /**
   * Set this connection as broken.
   */
  def setUnfixed(): Unit = {
    this.synchronized {
      fixed = false
    }
  }

}

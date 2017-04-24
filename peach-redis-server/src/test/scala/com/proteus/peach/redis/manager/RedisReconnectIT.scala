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

import org.junit.AfterClass
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import redis.clients.jedis.exceptions.JedisConnectionException

object RedisReconnectIT {
  /**
   * Initialize the Redis session.
   */
  @BeforeClass
  def beforeClassIT(): Unit = {
    RedisSessionManager.init(AbstractRedisManagerHelper.Session)
    assertTrue("Default session must be defined",
      RedisSessionManager.getSession(AbstractRedisManagerHelper.TargetSession).isDefined)
  }

  /**
   * Finish Redis Session Manager.
   */
  @AfterClass
  def afterClassIT(): Unit = {
    RedisSessionManager.close(AbstractRedisManagerHelper.TargetSession)
  }
}

/**
 * Test REDIS reconnection method.
 */
class RedisReconnectIT extends AbstractRedisManagerHelper {

  /**
   * The sleeping time in tests.
   */
  val testSleepingTime: Int = 100
  /**
   * The mockup provider.
   */
  val provider = new ReconnectMockupProvider()


  /**
   * Reconnection finally send the message.
   */
  @Test
  def reconnectionWorkTest(): Unit = {
    val fails = 3
    val reconnection = 5
    val result = this.provider.forceReconnect(fails, reconnection)
    Assert.assertTrue("Result must be defined.", result.isDefined)
  }

  /**
   * Reconnection finally fail.
   */
  @Test
  def reconnectionFailTest(): Unit = {
    val fails = 3
    val reconnection = 2
    val result = this.provider.forceReconnect(fails, reconnection)
    Assert.assertTrue("Result must be empty.", result.isEmpty)
  }


  /**
   * Reconnection mockup.
   */
  class ReconnectMockupProvider extends AbstractRedisProvider(AbstractRedisManagerHelper.TargetSession) {
    /**
     * Method that fails n number of times.
     *
     * @param numTimes    Number of fails.
     * @param maxNumTimes Provider reconnections.
     * @return DBSize
     */
    def forceReconnect(numTimes: Int, maxNumTimes: Int): Option[Long] = {
      var i = 0
      this.executeOperationWithRetries(() => {
        if (i < numTimes) {
          i += 1
          throw new JedisConnectionException("Connection problem.")
        } else {
          val jedis = this.client.getOrElse(throw new IllegalArgumentException("Client is null."))
          jedis.dbSize()
        }
      }, maxNumTimes, testSleepingTime)
    }
  }

}

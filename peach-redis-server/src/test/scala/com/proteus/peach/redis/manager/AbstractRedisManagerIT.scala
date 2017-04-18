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
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AbstractRedisManagerIT{
  /**
   * Target session
   */
  val TargetSession : String = "it-session"

  /**
   * Elastic search session.
   */
  val Session : BasicRedisSession = BasicRedisSession(
    this.TargetSession,
    AbstractRedisIT.getRedisHost(),
    AbstractRedisIT.getRedisPort()
  )

  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)

  /**
   * Initialize the Redis session.
   */
  @BeforeClass
  def beforeClassIT() : Unit = {
    Log.info("Starting Redis Session Manager.")
    RedisSessionManager.init(this.Session)
    assertTrue("Default session must be defined",
      RedisSessionManager.getSession(this.TargetSession).isDefined)
  }

  /**
   * Finish Redis Session Manager.
   */
  @AfterClass
  def afterClassIT() : Unit = {
    Log.info("Stopping Redis Session Manager.")
    RedisSessionManager.close(this.TargetSession)
  }
}

/**
 * Abstract class to be inherited in all Redis integration tests.
 */
abstract class AbstractRedisManagerIT extends AbstractRedisIT

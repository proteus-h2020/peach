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

object AbstractRedisManagerHelper{
  /**
   * Target session
   */
  val TargetSession : String = "it-session"

  /**
   * Elastic search session.
   */
  val Session : BasicRedisSession = BasicRedisSession(
    this.TargetSession,
    AbstractRedisHelper.getRedisHost(),
    AbstractRedisHelper.getRedisPort()
  )

  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)



}

/**
 * Abstract class to be inherited in all Redis integration tests.
 */
abstract class AbstractRedisManagerHelper extends AbstractRedisHelper

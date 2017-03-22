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

import com.proteus.peach.common.comm.PeachServerMessage.GetResponse
import com.proteus.peach.common.comm.PeachServerMessage.PutResponse


/**
 * Trait with the cache methods.
 */
trait ExternalServerCache {

  /**
   * Init signal.
   */
  def init():Unit

  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   */
  def put(key: String, value: String): PutResponse


  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  def get(key: String): GetResponse

  /**
   * Stop signal.
   */
  def stop():Unit
}

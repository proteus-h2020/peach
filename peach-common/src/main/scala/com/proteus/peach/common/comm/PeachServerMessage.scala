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

package com.proteus.peach.common.comm

/**
 * Server messages.
 */
object PeachServerMessage {

  /**
   * Put key/value.
   *
   * @param key   Key.
   * @param value Value key.
   */
  case class Put(key: String, value: String)

  /**
   * Put response.
   */
  case class PutResponse()

  /**
   * Get value sync.
   *
   * @param key Key.
   */
  case class Get(key: String)


  /**
   * Get response.
   *
   * @param value Recover value.
   */
  case class GetResponse(value: Option[String])

}

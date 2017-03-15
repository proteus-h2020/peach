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

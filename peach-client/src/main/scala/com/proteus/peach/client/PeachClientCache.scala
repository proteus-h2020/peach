package com.proteus.peach.client

/**
 * Peach client cache.
 */
trait PeachClientCache {

  /**
   * Put a element in the cache.
   *
   * @param key   Searched key.
   * @param value Value data.
   * @return A put response.
   */
  def put(key: String, value: String): Unit

  /**
   * Recover a element.
   *
   * @param key Searched key
   * @return The value if exist.
   */
  def get(key: String): Option[String]
}

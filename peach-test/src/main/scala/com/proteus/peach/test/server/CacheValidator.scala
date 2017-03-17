package com.proteus.peach.test.server

import com.proteus.peach.common.server.Cache
import org.junit.Assert
import org.junit.Test

/**
 * Cache server logic validator.
 */
trait CacheValidator {
  /**
   * Cache server instance.
   */
  val cacheServer: Cache

  /**
   * Simple test put and get key.
   */
  @Test
  def basicTest(): Unit = {
    val key = "basicTestKey"
    val value = "basicTestValue"
    this.cacheServer.put(key, value)
    val response = this.cacheServer.get(key)
    Assert.assertNotNull("Response must no be null", response)
    Assert.assertTrue("Response value must be defined.",response.value.isDefined)
    Assert.assertEquals("Response value must be [value]",response.value.get,value)
  }

  /**
   * Ask for a key that does not exist.
   */
  @Test
  def notExistKeyTest():Unit={
    val key = "notExistKeyTestKey"
    val response = this.cacheServer.get(key)
    Assert.assertNotNull("Response must no be null", response)
    Assert.assertTrue("Response value must be empty.",response.value.isEmpty)
  }
}

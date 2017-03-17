package com.proteus.peach.test.server
import com.proteus.peach.common.server.Cache

class MockupCacheTest extends CacheValidator{
  /**
   * Cache server instance.
   */
  override val cacheServer: Cache = new MockupCache()
}

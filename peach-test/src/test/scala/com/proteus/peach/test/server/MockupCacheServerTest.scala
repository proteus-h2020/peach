package com.proteus.peach.test.server
import com.proteus.peach.common.server.CacheServer

class MockupCacheServerTest extends CacheServerValidator{
  /**
   * Cache server instance.
   */
  override val cacheServer: CacheServer = new MockupCacheServer()
}

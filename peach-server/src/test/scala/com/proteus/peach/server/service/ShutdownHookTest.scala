package com.proteus.peach.server.service

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Shutdown tests.
 */
class ShutdownHookTest {

  /**
   * Test the listener and hook.
   */
  @Test
  def testListener(): Unit = {
    val listener = new MockupShutdownListener
    val shutdownHook = new ShutdownHook()
    shutdownHook.addListener(listener)
    val executor : ExecutorService = Executors.newSingleThreadExecutor()
    executor.submit(shutdownHook)
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
    assertTrue("Listener shutdown has not been triggered", listener.isCalled())
  }

}

/**
 * Mockup listener for the tests.
 */
class MockupShutdownListener extends ShutdownListener{

  /**
   * Variable to determine if shutdown has been called.
   */
  private var called : Boolean = false

  /**
   * Shutdown the application.
   */
  override def shutdown(): Unit = {
    this.called = true
  }

  /**
   * Returns if the listener has been called.
   * @return Whether it has been called.
   */
  def isCalled() : Boolean = {
    this.called
  }
}

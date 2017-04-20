package com.proteus.peach.server.service

/**
 * Shutdown listener.
 */
trait ShutdownListener {

  /**
   * Shutdown the application.
   */
  def shutdown() : Unit

}

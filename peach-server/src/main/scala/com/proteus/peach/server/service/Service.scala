package com.proteus.peach.server.service

/**
 * Definition of the operations of a given service. The service must provide with a method
 * to start and terminate the execution.
 */
trait Service {

  /**
   * Initialize the internals of the service so it can be launched.
   */
  def init(): Unit

  /**
   * Start the service.
   */
  def run(): Unit

  /**
   * Shutdown the service.
   */
  def shutdown(): Unit
}

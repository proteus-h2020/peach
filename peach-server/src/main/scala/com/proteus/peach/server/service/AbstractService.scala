package com.proteus.peach.server.service

/**
 * Abstract definition of a service. This class manages the calls to start and init, and provides
 * a shutdown hook to gracefully terminate services.
 */
abstract class AbstractService extends Service {

  /**
   * Flag to determine if the service has been initialized.
   */
  private var initialized: Boolean = false

  /**
   * Initialize the shutdown hook of the service.
   */
  override def init(): Unit = {
    this.synchronized {
      if (!this.initialized) {
        sys.addShutdownHook(this.shutdown())
        this.doInit()
        this.initialized = true
      }
    }
  }

  /**
   * Launch the service if initialized.
   */
  override def run(): Unit = {
    this.synchronized {
      if (!this.initialized) {
        throw new UnsupportedOperationException("Init must be called before run")
      }
    }
    this.doRun()
  }

  /**
   * Shutdown method implementation.
   */
  def shutdown(): Unit

  /**
   * Method that is called when the init method is called on the service.
   */
  protected def doInit(): Unit

  /**
   * Method that is called when the run method is called on the service.
   */
  protected def doRun(): Unit

}

package com.proteus.peach.server.service

/**
 * Abstract definition of a service. This class manages the calls to start and init, and provides
 * a shutdown hook to gracefully terminate services.
 */
abstract class AbstractService extends Service{

  /**
   * Flag to determine if the service has been initialized.
   */
  private var initialized : Boolean = false

  /**
   * Initialize the shutdown hook of the service.
   */
  override def init() : Unit = {
    this.synchronized{
      if(!this.initialized){
        val shutdownHook = new ShutdownHook()
        shutdownHook.addListener(this)
        Runtime.getRuntime.addShutdownHook(new Thread(shutdownHook))
        this.doInit()
        this.initialized = true
      }
    }
  }

  /**
   * Method that is called when the init method is called on the service.
   */
  protected def doInit() : Unit

  /**
   * Launch the service if initialized.
   */
  override def run() : Unit = {
    this.synchronized{
      if(!this.initialized){
        throw new UnsupportedOperationException("Init must be called before run")
      }
    }
    this.doRun()
  }

  /**
   * Method that is called when the run method is called on the service.
   */
  protected def doRun() : Unit

  /**
   * Shutdown method implementation.
   */
  def shutdown(): Unit

}

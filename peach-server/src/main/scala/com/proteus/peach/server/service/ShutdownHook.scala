package com.proteus.peach.server.service

import java.util.{ArrayList => JArrayList}
import java.util.{List => JList}

import com.proteus.peach.common.util.JavaCollectionsConversions.scalaToJavaConsumer

import org.slf4j.Logger

object ShutdownHook{

  /**
   * Class logger.
   */
  private val Log: Logger = org.slf4j.LoggerFactory.getLogger(this.getClass.getName)
}

/**
 * Class that implements a shutdown hook that will be called when the application receives the
 * termination signal and informs all required elements that they should gracefully shutdown.
 *
 * @param listeners The shutdown listeners.
 */
class ShutdownHook(listeners : JList[ShutdownListener] = new JArrayList[ShutdownListener]())
  extends Runnable{

  /**
   * Add a new listener to the hook.
   * @param listener The new listener.
   */
  def addListener(listener: ShutdownListener) : Unit = {
    this.listeners.add(listener)
  }

  /**
   * Shutdown all listeners.
   */
  override def run(): Unit = {
    ShutdownHook.Log.warn("Shutting down the application")
    this.listeners.forEach((listener: ShutdownListener) => {
      listener.shutdown()
    })
  }

}

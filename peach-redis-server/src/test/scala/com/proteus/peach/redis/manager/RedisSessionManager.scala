/*
 * Copyright (C) 2017 The Proteus Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.proteus.peach.redis.manager

import com.proteus.peach.common.util.JavaCollectionsConversions.scalaToJavaConsumer

import java.util.concurrent.ConcurrentHashMap
import java.util.{List => JList}
import java.util.Map

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.exceptions.JedisConnectionException

import scala.io.Source

/**
 * Redis session manager. For each session, a pool of available connections is maintained, each
 * thread accessing Redis must request and return the Jedis connection to the pool when it
 * finishes using it.
 */
object RedisSessionManager {
  

  /**
   * Pool maximum size.
   */
  private val PoolSize : Int = 64

  /**
   * Wait time to retrieve elements from the pool.
   */
  private val PoolWaitTime : Int = 10000

  /**
   * Class logger.
   */
  private val Log: Logger = LoggerFactory.getLogger(this.getClass.getName)

  /**
   * Map of available pools indexed by session identifier.
   */
  private val Sessions : Map[String, JedisPool] = new ConcurrentHashMap[String, JedisPool]()

  /**
   * Map of session configurations per identifier.
   */
  private val Configurations: Map[String, BasicRedisSession] = new ConcurrentHashMap[String, BasicRedisSession]()

  /**
   * Map of the scripts hashes indexed by function name.
   */
  private val Scripts : Map[String, String] = new ConcurrentHashMap[String, String]()

  /**
   * Initialize a list of sessions.
   * @param sessions The sessions.
   * @return Whether all the sessions have been initialized.
   */
  def init(sessions: JList[BasicRedisSession]) : Boolean = {
    val it = sessions.iterator()
    var result = true
    while(result && it.hasNext){
      result = RedisSessionManager.init(it.next())
    }
    result
  }

  /**
   * Initialize a session in redis. This method must be called before using the connection.
   * @param redisSession The redis session.
   * @return Whether a session could be established with the redis host.
   */
  def init(redisSession: BasicRedisSession) : Boolean = {
    if(this.Sessions.containsKey(redisSession.id)){
      throw new UnsupportedOperationException(
        s"A pool for session ${redisSession.id} already exists")
    }
    val pool = this.connect(redisSession)
    if(pool.isDefined){
      this.Configurations.put(redisSession.id, redisSession)
      this.Sessions.put(redisSession.id, pool.get)
      val registered = this.registerNoveltiScripts(redisSession.id)
      if(registered) {
        true
      }else{
        this.close(redisSession.id)
        Log.error(s"Not registered Redis scripts for ${redisSession.id}")
        false
      }
    }else{
      false
    }
  }

  /**
   * Establish a connection with redis.
   * @param redisSession The redis session.
   * @return An option with a JedisPool.
   */
  private def connect(redisSession:BasicRedisSession) : Option[JedisPool] = {
    val poolConfig = new JedisPoolConfig()
    poolConfig.setMaxWaitMillis(RedisSessionManager.PoolWaitTime)
    Log.debug("Maximum number of connections: ${poolConfig.getMaxTotal}")
    poolConfig.setMaxTotal(RedisSessionManager.PoolSize)
    poolConfig.setTestOnBorrow(true)
    poolConfig.setTestWhileIdle(true)
    val connectionPool = new JedisPool(
      poolConfig, redisSession.addresses.get(0), redisSession.port)
    try {
      val client = connectionPool.getResource
      if(client.isConnected()){
        client.close()
        Some(connectionPool)
      }else{
        None
      }
    }catch {
      case e: JedisConnectionException => {
        Log.error(s"Cannot connect to redis ${redisSession}", e)
        None
      }
    }
  }

  /**
   * Register the collection of preset scripts in Redis.
   * @param sessionId The session identifier.
   * @return Whether all of them are successfully registered.
   */
  private def registerNoveltiScripts(sessionId: String) : Boolean = {
    val min = this.registerScriptByPath(sessionId, this.UpdateMinScript)
    if(min.isDefined){
      this.Scripts.put(this.UpdateMinFunction, min.get)
      Log.info(s"Registering UpdateMinScript with ${min.get}")
    }
    val max = this.registerScriptByPath(sessionId, this.UpdateMaxScript)
    if(max.isDefined){
      this.Scripts.put(this.UpdateMaxFunction, max.get)
      Log.info(s"Registering UpdateMaxScript with ${max.get}")
    }
    val avg = this.registerScriptByPath(sessionId, this.UpdateAvgScript)
    if(avg.isDefined){
      this.Scripts.put(this.UpdateAvgFunction, avg.get)
      Log.info(s"Registering UpdateAvgScript with ${avg.get}")
    }
    val incrSliding = this.registerScriptByPath(sessionId, this.IncrSlidingCounter1MinScript)
    if(incrSliding.isDefined){
      this.Scripts.put(this.IncrSlidingCounter1Min, incrSliding.get)
      Log.info(s"Registering IncrSlidingCounter1Min with ${incrSliding.get}")
    }
    val getSliding = this.registerScriptByPath(sessionId, this.GetSlidingCounter1MinScript)
    if(getSliding.isDefined){
      this.Scripts.put(this.GetSlidingCounter1Min, getSliding.get)
      Log.info(s"Registering GetSlidingCounter1Min with ${getSliding.get}")
    }
    min.isDefined && max.isDefined && avg.isDefined &&
      incrSliding.isDefined && getSliding.isDefined
  }


  /**
   * Get a Jedis client that can be used to send data to redis. Notice that the client thread
   * must call jedis.close() in order for the client to be returned to the connection pool.
   * @param sessionId The session identifier.
   * @return An option with the Jedis client.
   */
  def getSession(sessionId: String) : Option[Jedis] = {
    if(this.Sessions.containsKey(sessionId)){
      Option(this.Sessions.get(sessionId).getResource)
    }else{
      None
    }
  }

  /**
   * Get the redis session used to identify a session.
   * @param sessionId The session identifier.
   * @return A basic redis session information.
   */
  def getConfiguration(sessionId: String): Option[BasicRedisSession] = {
    if(this.Configurations.containsKey(sessionId)){
      Option(this.Configurations.get(sessionId))
    }else{
      None
    }
  }

  def reconnect(sessionId: String) : Option[Jedis] = {
    if(this.Sessions.containsKey(sessionId)){
      // close the pool
      val oldPool = this.Sessions.get(sessionId)
      if(!oldPool.isClosed){
        oldPool.close()
      }
      val newPool = this.connect(this.Configurations.get(sessionId))
      // replace old pool
      this.registerNoveltiScripts(sessionId)
      this.Sessions.put(sessionId,newPool.get)
      Some(this.Sessions.get(sessionId).getResource)
    }
    None

  }

  /**
   * Get the SHA hash associated with a given script.
   * @param script The name of the script.
   * @return An option with the hash.
   */
  def getScriptSHA(script: String) : Option[String] = {
    Option(this.Scripts.get(script))
  }

  /**
   * Close the connections associated with a given session.
   * @param sessionId The session identifier.
   */
  def close(sessionId: String) : Unit = {
    if(this.Sessions.containsKey(sessionId)){
      val pool : JedisPool = this.Sessions.get(sessionId)
      this.Sessions.remove(sessionId)
      pool.close()
    }
  }

  /**
   * Shutdown all managed sessions.
   */
  def shutdown() : Unit = {
    this.Sessions.keySet().forEach((sessionId: String) => this.close(sessionId))
  }

  /**
   * Load a script in redis for future usage.
   * @param sessionId The session identifier.
   * @param resourcePath The path stream to read the file.
   * @return An option with the script hash.
   */
  def registerScriptByPath(sessionId: String, resourcePath: String) : Option[String] = {
    val script = this.getClass.getResourceAsStream(resourcePath)
    try {
      val lines = Source.fromInputStream(script).getLines()
        .filter(l => {!l.startsWith("--")})
        .mkString(System.lineSeparator())
      this.registerScript(sessionId, lines)
    } catch {
      case e: Exception => {
        Log.error("Cannot register script", e)
        None
      }
    } finally {
      script.close()
    }
  }

  /**
   * Register a script in redis on given session.
   * @param sessionId The session identifier.
   * @param script The script as text.
   * @return An option with the script hash.
   */
  def registerScript(sessionId: String, script: String) : Option[String] = {
    val jedis = this.getSession(sessionId)
    if(jedis.isDefined){
      val result = Option(jedis.get.scriptLoad(script))
      jedis.get.close()
      if(result.isDefined){
        Some(result.get)
      }else{
        None
      }
    }else{
      None
    }
  }

}

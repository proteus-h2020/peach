/*
 *  Copyright (C) 2017 The Proteus Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.proteus.peach.client

/**
 * Sample file.
 */
object ClientApp extends App {
  println("Peach Client")
  /* implicit val timeout = Timeout(1, TimeUnit.SECONDS)

   val system = ActorSystem("PeachClient")

   val initialContacts = Set(
     system.actorSelection("akka.tcp://PeachServer@127.0.0.1:2552/user/receptionist"))

   val c = system.actorOf(ClusterClient.props(initialContacts), "client")



   c ? ClusterClient.Send("/user/comm", Put("hola", "adios"), localAffinity = true)


   val future = c ? ClusterClient.Send("/user/comm", Get("hola"), localAffinity = true)

   future.map(println)*/

}
/*
 * Copyright (c) 2020 Andre Tietz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.andretietz.houston

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The main library class. Initialize the library and send messages to all tracking tools.
 *
 * @param missionControl a set of all tracking tools.
 */
class Houston private constructor(
  private val missionControl: Set<TrackingTool>,
  private val coroutineScope: CoroutineScope
) {

  class Builder internal constructor(private val coroutineScope: CoroutineScope) {
    private val trackingTools: MutableSet<TrackingTool> = HashSet()

    /**
     * Registers a [TrackingTool].
     *
     */
    fun add(trackingTool: TrackingTool): Builder = apply { this.trackingTools.add(trackingTool) }

    /**
     * After adding all [TrackingTool]s, you want to initialize the library.
     */
    fun launch() {
      INSTANCE = Houston(trackingTools, coroutineScope)
    }
  }

  internal fun sendFinally(message: Message) {
    coroutineScope.launch {
      missionControl.forEach { it.send(message) }
    }
  }

  companion object {

    private lateinit var INSTANCE: Houston

    /**
     * Creates a Message in preparation to send.
     *
     * @param id of the message to send.
     */
    @JvmStatic
    fun send(id: String) = Message(id)

    @JvmStatic
    internal fun send(message: Message) {
      if (this::INSTANCE.isInitialized) {
        INSTANCE.sendFinally(message)
      }
    }

    /**
     * Creates a [Builder] to initialize the library.
     */
    @JvmStatic
    @JvmOverloads
    fun init(
      coroutineScope: CoroutineScope
    ) = Builder(coroutineScope)
  }
}

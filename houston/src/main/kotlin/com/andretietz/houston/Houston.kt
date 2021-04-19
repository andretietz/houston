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

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The main library class. Initialize the library and send messages to all tracking tools.
 *
 * @param missionControl a set of all tracking tools.
 */
class Houston private constructor(
  private val missionControl: Set<TrackingTool>,
  private val coroutineScope: CoroutineScope,
  private val errorHandler: CoroutineExceptionHandler,
  private var trackingEnabled: Boolean,
  private val useMessageBuffer: Boolean
) {

  private val messageBuffer by lazy { mutableListOf<Message>() }

  private fun enableTracking(enabled: Boolean) {
    trackingEnabled = enabled
    if (useMessageBuffer) {
      coroutineScope.launch(errorHandler + Dispatchers.IO) {
        messageBuffer.forEach { message ->
          missionControl.forEach { sendSingleMessage(it, message) }
        }
      }
    }
  }

  /**
   * A Builder to setup [Houston].
   */
  class Builder {

    private val trackingTools: MutableSet<TrackingTool> = HashSet()

    /**
     * Registers a [TrackingTool].
     *
     */
    fun add(trackingTool: TrackingTool): Builder = apply { this.trackingTools.add(trackingTool) }

    /**
     * After adding all [TrackingTool]s, you want to initialize the library.
     *
     * @param coroutineScope used for reporting async.
     * @param trackingEnabled if tracking is enabled on after this init call or not. default: `false`
     * @param messageBuffer if a buffer for messages should be used. default: `false`
     * @param errorHandler that handles crashes when sending the event throws an exception.
     */
    @JvmOverloads
    fun launch(
      coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
      trackingEnabled: Boolean = false,
      messageBuffer: Boolean = false,
      errorHandler: CoroutineExceptionHandler = DEFAULT_EXCEPTION_HANDLER
    ) {
      INSTANCE = Houston(
        trackingTools,
        coroutineScope,
        errorHandler,
        trackingEnabled,
        messageBuffer
      )
    }
  }

  private fun sendFinally(message: Message) {
    coroutineScope.launch(errorHandler + Dispatchers.IO) {
      if (trackingEnabled) {
        missionControl.forEach { sendSingleMessage(it, message) }
      } else if (useMessageBuffer) {
        messageBuffer.add(message)
      }
    }
  }

  private fun sendSingleMessage(trackingTool: TrackingTool, message: Message) {
    if (!trackingTool.initialized) trackingTool.initialize()
    trackingTool.send(message)
  }

  companion object {

    private lateinit var INSTANCE: Houston

    private val DEFAULT_EXCEPTION_HANDLER =
      CoroutineExceptionHandler { _, error -> error.printStackTrace() }

    /**
     * Creates a Message in preparation to send.
     *
     * @param id of the message to send.
     */
    @JvmStatic
    fun send(id: String) = Message(id)

    /**
     * enabling/disabling [Houston] will avoid sending events.
     * Can be used to enable/disable tracking when the user wants to disable/enable it.
     */
    @JvmStatic
    fun setEnabled(enabled: Boolean) {
      if (this::INSTANCE.isInitialized) {
        INSTANCE.enableTracking(enabled)
      }
    }

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
    fun init() = Builder()
  }
}

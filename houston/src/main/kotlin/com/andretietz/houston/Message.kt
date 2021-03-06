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

/**
 * Message that can be sent to all tracking tools registered with [Houston.Builder.add] during the initialization.
 */
data class Message internal constructor(
  /**
   * identifier of the message sent to the [TrackingTool]s.
   */
  val id: String
) {

  /**
   * Map containing optional data, that is internally mutable.
   */
  private val _data: MutableMap<String, String> = HashMap()

  /**
   * Map containing optional data.
   */
  val data: Map<String, String> = _data

  /**
   * timestamp when the message was created.
   */
  val timestamp: Long = System.currentTimeMillis()

  /**
   * Add key-value pairs as optional data to the message object.
   *
   * @param key you want to edit
   * @param value if not `null` it will add the key value pair. If it is `null` it removes it.
   */
  fun with(key: String, value: String?): Message = apply {
    if (value != null) {
      _data[key] = value
    } else {
      _data.remove(key)
    }
  }

  /**
   * Forwards this message to all tracking tools provided using [Houston.Builder.add].
   */
  fun over() = Houston.send(this)
}

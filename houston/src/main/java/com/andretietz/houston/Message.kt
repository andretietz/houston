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
 * Message that can be sent to all tracking tools registered with [Houston.add] during the initialization.
 */
data class Message internal constructor(val id: String) {

  /**
   * Map containing optional data, that is internally mutable.
   */
  private val _data: MutableMap<String, String> = HashMap()

  /**
   * Map containing optional data.
   */
  val data: Map<String, String> = _data

  /**
   * Add key-value pairs as optional data to the message object.
   */
  fun with(key: String, value: String): Message = apply {
    _data[key] = value
  }

  /**
   * Forwards this message to all tracking tools provided using [Houston.add].
   */
  fun over() = Houston.send(this)
}

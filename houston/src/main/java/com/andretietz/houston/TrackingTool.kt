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
 * A TrackingTool represents tool you want to track into. So whenever you want to add a new tool, make sure you
 * implement this interface and initialize it, within [Houston.Builder.add].
 */
interface TrackingTool {
  /**
   * Whenever there was a message sent using [Houston.send], this message is forwarded
   * into the implementation of this.
   *
   * @param message containing an [Message.id] and [Message.data].
   */
  fun send(message: Message)
}
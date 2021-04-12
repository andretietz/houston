# Houston
A kotlin library to abstract tracking tools.

## How to use
### Add the libary from `mavenCentral`
```groovy
implementation "com.andretietz.houston:houston:x.y.z"
```
### Snapshot versions

Snapshot versions are available, by adding the mavenCentral snapshot repository

```groovy
maven { url { "https://oss.sonatype.org/content/repositories/snapshots" } }
```

and adding `-SNAPSHOT` to the version number:

```groovy
implementation "com.andretietz.houston:houston:x.y.z-SNAPSHOT'
```
### Initialize Houston
Whenever you forgot to initialize it, no events will be sent. This could be used 
e.g. to avoid tracking on a development environment. 
```kotlin
Houston.init(optionalCoroutineScope)
  .add(someAnalyticsTool1) 
  .add(someAnalyticsTool2)
  .launch(optionalCoroutineExceptionHandler)
```

### Fire events
Example for login success or not:
```kotlin
...
{ loginSuccess: Boolean, data: String? -> 

  // a string to identify the event (id: String)
  Houston.send(LoginTracker.LOGIN) 
    // optional argument(s) (key: String, value: String?)
    .with(LoginTracker.LOGIN_KEY_SUCCESS, loginSuccess.toString())
    // the value can be nullable. in this case it will not be added.
    .with(LoginTracker.LOGIN_KEY_DATA, data)
    ...
    .over()
  ...
}
```

### Implement a tracking tool
This is sample of an non-existing tracking tool called WhateverAnalytics, in which you create an event which you send 
using their analytics instance.
```kotlin
class WhateverAnalyticsTrackingTool(private val whateverAnalytics: WhateverAnalytics) : TrackingTool {
  override fun send(message: Message) {
    val event = WhateverEvent(message.id)
    message.data.forEach { key, value ->
      event.addData(key, value)
    }
    whateverAnalytics.track(event)
  }
}
```

## Intention
Whenever you're working on a product (probably not that much, when working on projects),
from time to time you need to add, update or remove certain tracking tools such as: google analytics, localytics, adjust from your code.
This is a pain because every time you want to achieve one of it, you need to touch every
piece of code wherever you added a tracking event.

To avoid this we created abstractions (in several iterations), so that every tracking tool is implementing a certain interface
and you fire events independent of the actual tool. The library is not a huge master piece nor is it
a lot of code you save to write. It's only to avoid writing those lines all over again.

Initially I used this in android projects, but since it doesn't have any Android dependencies you could use it in
every Java/Kotlin project. 

## About
The name and usage of the library has analogies to NASA Space missions.

A tracking tool relates to a crew-member of mission control and the application relates
to a spaceship that sends data to mission control.
 
## LICENSE
```
Copyrights 2020 André Tietz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

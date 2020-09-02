# Houston
A kotlin library to abstract tracking tools.

## How to use
### Add the libary from `mavenCentral`
```groovy
implement("com.andretietz.houston:houston:x.y.z")
```
### Initialize Houston
Whenever you forgot to initialize it, no events will be sent. This could be used 
e.g. to avoid tracking on a development environment. 
```kotlin
Houston.init()
  .add(someAnalyticsTool1) 
  .add(someAnalyticsTool2)
  .launch()
```

### Fire events
Example for login success or not:
```kotlin
...
{ loginSuccess: Boolean -> 

  // a string to identify the event (id: String)
  Houston.createMessage(LoginTracker.LOGIN) 
    // optional argument(s) (key: String, value: String)
    .with(LoginTracker.LOGIN_KEY_SUCCESS, loginSuccess.toString())
    .with(...)
    ...
    .over()
  ...
}
```

### Implement a tracking tool
This is sample of an non-existing tracking tool called WhateverAnalytics, in which you create an event which you send 
using their analytics instance.
```kotlin
class WhateverAnalyticsCrewMember(private val whateverAnalytics: WhateverAnalytics) : CrewMember {
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
The name and usage of the library have analogies to the NASA Apollo 13 mission. 

A tracking tool relates to a crewmember of mission control and the application relates 
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
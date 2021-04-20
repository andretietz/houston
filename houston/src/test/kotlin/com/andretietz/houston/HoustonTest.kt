package com.andretietz.houston

import io.mockk.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class HoustonTest {

  @Test
  fun `Initializing Message Object, set and reset data`() {
    val msg = Message(ID).with(KEY, VALUE)

    assert(msg.id == ID)
    assert(msg.data[KEY] == VALUE)

    msg.with(KEY, OTHER_VALUE)

    assert(msg.id == ID)
    assert(msg.data[KEY] == OTHER_VALUE)

    msg.with(KEY, null)

    assert(msg.data[KEY] == null)

  }

  @Test
  fun `No message is sent, when Houston not initialized`() {
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } just Runs }
    val williamRPouge = mockk<TrackingTool> { every { send(any()) } just Runs }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } just Runs }

    Houston.send(ID).over()

    verify(exactly = 0) { jackRLousma.send(any()) }
    verify(exactly = 0) { williamRPouge.send(any()) }
    verify(exactly = 0) { vanceDBrand.send(any()) }
  }

  @Test
  fun `Sending a message to multiple receivers`() = runBlockingTest {
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } just Runs }
    val williamRPouge = mockk<TrackingTool> { every { send(any()) } just Runs }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } just Runs }

    Houston.init()
      .add(jackRLousma)
      .add(williamRPouge)
      .add(vanceDBrand)
      .launch(
        coroutineScope = this,
        trackingEnabled = true
      )

    Houston.send(ID)
      .with(KEY, VALUE)
      .over()

    val messageFromJack = slot<Message>()
    val messageFromWilliam = slot<Message>()
    val messageFromVance = slot<Message>()

    verify(exactly = 1) { jackRLousma.send(capture(messageFromJack)) }
    verify(exactly = 1) { williamRPouge.send(capture(messageFromWilliam)) }
    verify(exactly = 1) { vanceDBrand.send(capture(messageFromVance)) }

    assert(messageFromJack.captured.id == ID)
    assert(messageFromJack.captured.data[KEY] == VALUE)
    assert(messageFromWilliam.captured.id == ID)
    assert(messageFromWilliam.captured.data[KEY] == VALUE)
    assert(messageFromVance.captured.id == ID)
    assert(messageFromVance.captured.data[KEY] == VALUE)

  }

  @Test
  fun `Sending a message and crash`() = runBlockingTest {
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } just Runs }
    val williamRPouge =
      mockk<TrackingTool> { every { send(any()) } throws IllegalStateException("An exception appeared") }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } just Runs }

    var errorSlot: Throwable? = null

    Houston.init()
      .add(jackRLousma)
      .add(williamRPouge)
      .add(vanceDBrand)
      .launch(
        coroutineScope = this,
        trackingEnabled = true,
        errorHandler = CoroutineExceptionHandler { _, error -> errorSlot = error }
      )

    Houston.send(ID)
      .with(KEY, VALUE)
      .over()

    // All messages should still be sent
    val messageFromJack = slot<Message>()
    val messageFromWilliam = slot<Message>()
    val messageFromVance = slot<Message>()

    verify(exactly = 1) { jackRLousma.send(capture(messageFromJack)) }
    verify(exactly = 1) { williamRPouge.send(capture(messageFromWilliam)) }
    verify(exactly = 1) { vanceDBrand.send(capture(messageFromVance)) }

    assert(messageFromJack.captured.id == ID)
    assert(messageFromJack.captured.data[KEY] == VALUE)
    assert(messageFromWilliam.captured.id == ID)
    assert(messageFromWilliam.captured.data[KEY] == VALUE)
    assert(messageFromVance.captured.id == ID)
    assert(messageFromVance.captured.data[KEY] == VALUE)

    assert(errorSlot != null)
    assert(errorSlot is IllegalStateException)
    assert(errorSlot?.message == "An exception appeared")
  }

  companion object {
    const val ID = "I believe we've had a problem here."
    const val KEY = "problem"
    const val VALUE = "explosion and rupture of oxygen tank 2"
    const val OTHER_VALUE = "oxygen tank 1 looses oxygen"
  }
}

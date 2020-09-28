package com.andretietz.houston

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } returns Unit }
    val williamRPouge = mockk<TrackingTool> { every { send(any()) } returns Unit }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } returns Unit }

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

    Houston.init(this)
      .add(jackRLousma)
      .add(williamRPouge)
      .add(vanceDBrand)
      .launch()

    Houston.send(ID)
      .with(KEY, VALUE)
      .over()

    val message = slot<Message>()

    verify(exactly = 1) { jackRLousma.send(capture(message)) }
    verify(exactly = 1) { williamRPouge.send(capture(message)) }
    verify(exactly = 1) { vanceDBrand.send(capture(message)) }

    assert(message.captured.id == ID)
    assert(message.captured.data[KEY] == VALUE)

  }

  @Test
  fun `Sending a message and crash`() = runBlockingTest {
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } just Runs }
    val williamRPouge =
      mockk<TrackingTool> { every { send(any()) } throws IllegalStateException("An exception appeared") }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } just Runs }

    Houston.init(this)
      .add(jackRLousma)
      .add(williamRPouge)
      .add(vanceDBrand)
      .launch(CoroutineExceptionHandler { _, _ -> })

    Houston.send(ID)
      .with(KEY, VALUE)
      .over()

    // All messages should still be sent
    val message = slot<Message>()

    verify(exactly = 1) { jackRLousma.send(capture(message)) }
    verify(exactly = 1) { williamRPouge.send(capture(message)) }
    verify(exactly = 1) { vanceDBrand.send(capture(message)) }

    assert(message.captured.id == ID)
    assert(message.captured.data[KEY] == VALUE)
  }

  companion object {
    const val ID = "I believe we've had a problem here."
    const val KEY = "problem"
    const val VALUE = "explosion and rupture of oxygen tank 2"
    const val OTHER_VALUE = "oxygen tank 1 looses oxygen"
  }

  fun foo() = runBlockingTest {
    val jackRLousma = object : TrackingTool {
      override fun send(message: Message) {
        println("jackRLousma - ${message.id}: ${message.data}")
      }
    }

    val williamRPouge = object : TrackingTool {
      override fun send(message: Message) {
        println("williamRPouge")
        throw java.lang.IllegalStateException()
      }
    }
    val vanceDBrand = object : TrackingTool {
      override fun send(message: Message) {
        println("vanceDBrand - ${message.id}: ${message.data}")
      }
    }


    Houston.init()
      .add(jackRLousma)
      .add(williamRPouge)
      .add(vanceDBrand)
      .launch()

    Houston.send(ID)
      .with(KEY, VALUE)
      .over()
  }

}

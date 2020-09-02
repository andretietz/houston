package com.andretietz.houston

import io.mockk.*
import org.junit.Test

class HoustonTest {

  @Test
  fun `Message object`() {
    val msg = Message(ID).with(KEY, VALUE)

    assert(msg.id == ID)
    assert(msg.data[KEY] == VALUE)

    msg.with(KEY, OTHER_VALUE)

    assert(msg.data[KEY] == OTHER_VALUE)
    assert(msg.id == ID)
  }

  @Test
  fun `Missing initialization`() {
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } returns Unit }
    val williamRPouge = mockk<TrackingTool> { every { send(any()) } returns Unit }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } returns Unit }

    Houston.send(ID).over()

    verify(exactly = 0) { jackRLousma.send(any()) }
    verify(exactly = 0) { williamRPouge.send(any()) }
    verify(exactly = 0) { vanceDBrand.send(any()) }
  }

  @Test
  fun `Sending a message to multiple receivers`() {
    val jackRLousma = mockk<TrackingTool> { every { send(any()) } just Runs }
    val williamRPouge = mockk<TrackingTool> { every { send(any()) } just Runs }
    val vanceDBrand = mockk<TrackingTool> { every { send(any()) } just Runs }

    Houston.init()
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

  companion object {
    const val ID = "I believe we've had a problem here."
    const val KEY = "some"
    const val VALUE = "value"
    const val OTHER_VALUE = "other value"
  }

}

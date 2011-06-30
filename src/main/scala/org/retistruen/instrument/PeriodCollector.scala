package org.retistruen.instrument

import akka.actor.Actor._
import akka.actor.{ Actor, ReceiveTimeout }
import org.joda.time.ReadablePeriod
import org.retistruen._

class PeriodCollector[T](val name: String, val period: ReadablePeriod) extends Collector[T] with Start with Stop {

  private val actor = actorOf(new Actor {
    self.receiveTimeout = Some(period.toPeriod.toStandardDuration.getMillis)
    def receive = {
      case ReceiveTimeout ⇒
        emit(buffer)
        clear
    }
  })

  def start = actor.start

  def stop = actor.stop

  override def receive(emitter: Emitter[T], datum: Datum[T]) = {
    if (!actor.isRunning) throw new IllegalStateException("PeriodCollector '" + name + "' must be started before use. Have you started your retistruen model?")
    super.receive(emitter, datum)
  }

}

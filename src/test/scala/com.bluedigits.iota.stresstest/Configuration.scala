package com.bluedigits.iota.stresstest

import java.io.File
import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._
import scala.concurrent.duration.{Duration, FiniteDuration}

object Configuration {
  val conf = ConfigFactory.parseFile(new File("iota.conf"))

  val hosts: List[String] = conf.getStringList("hosts").toList match {
    case Nil => throw new Exception("You have to provide valid IOTA node hosts.")
    case x :: xs => x :: xs
  }
  val seed: String = conf.getString("seed") match {
    case null => throw new Exception("You have to provide a valid seed.")
    case x => x.padTo(81, '9')
  }
  val security: Int = conf.getInt("security") match {
    case x: Int if 1 to 3 contains x => x
    case _ => 2
  }
  val depth: Int = conf.getInt("depth") match {
    case x: Int if 1 to 999 contains x => x
    case _ => 4
  }
  val maxAddressAmount: Int = conf.getInt("addresses") match {
    case x: Int if 1 to 999 contains x => x
    case _ => 20
  }
  val users: Int = conf.getInt("users") match {
    case x: Int if 1 to 500 contains x => x
    case _ => 1
  }
  val repetitions: Int = conf.getInt("repetitions") match {
    case x: Int if 0 to 100000 contains x => x.toInt
    case _ => 1
  }
  val maxDuration: FiniteDuration = conf.getInt("duration") match {
    case x: Int if 0 to 86400 contains x => Duration(x, TimeUnit.MINUTES)
    case _ => Duration(5, TimeUnit.MINUTES)
  }
  val pause: FiniteDuration = conf.getInt("pause") match {
    case x: Int if 0 to 5000 contains x => Duration(x, TimeUnit.MILLISECONDS)
    case _ => Duration(0, TimeUnit.MILLISECONDS)
  }
  val isDebug: Boolean = conf.getBoolean("debug")
}

package com.bluedigits.iota.stresstest

import com.bluedigits.iota.stresstest.Configuration.{pause, repetitions}
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}

object ScenarioHelper {

  /**
    * Creates a scenario by given name and chains.
    *
    * @param name   Scenario name
    * @param chains Chained executables
    * @return
    */
  def createScenario(name: String, chains: ChainBuilder*): ScenarioBuilder = {
    if (repetitions > 0) {
      scenario(name).repeat(repetitions) {
        exec(chains).pause(pause)
      }
    } else {
      // Loop forever until configured 'maxDuration'
      scenario(name).forever() {
        exec(chains).pause(pause)
      }
    }
  }
}

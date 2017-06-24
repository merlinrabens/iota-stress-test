package com.bluedigits.iota.stresstest

import com.bluedigits.iota.stresstest.Configuration.{hosts, maxDuration, users}
import io.gatling.commons.stats.Status
import io.gatling.core.Predef._
import io.gatling.http.Predef.{http, status}
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.ExtraInfo

class IotaNodeTestSimulation extends Simulation {

  private val successStatus = 200
  private val responseTimeMs = 500
  private val responseSuccessPercentage = 99

  private val httpProtocol: HttpProtocolBuilder = http
    .baseURLs(hosts)
    .check(status.is(successStatus))
    .extraInfoExtractor(extraInfo => List(getExtraInfo(extraInfo)))

  private def getExtraInfo(extraInfo: ExtraInfo): String = {
    // Dump request/response in case of error or in debug mode
    if (Configuration.isDebug
      || extraInfo.response.statusCode.get != successStatus
      || extraInfo.status.eq(Status.apply("KO"))) {
      ", URL: " + extraInfo.request.getUrl +
        ", Request: " + extraInfo.request.getStringData +
        ", Response: " + extraInfo.response.body.string
    } else {
      ""
    }
  }

  setUp(
    IotaNodeTasks.scnGetNodeInfo.inject(atOnceUsers(users)),
    IotaNodeTasks.scnSendIota.inject(atOnceUsers(users))
  )
    .protocols(httpProtocol)
    .pauses(constantPauses)
    .maxDuration(maxDuration)
    .assertions(
      global.responseTime.max.lte(responseTimeMs),
      global.successfulRequests.percent.gte(responseSuccessPercentage)
    )
}

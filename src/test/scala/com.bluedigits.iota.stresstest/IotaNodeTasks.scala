package com.bluedigits.iota.stresstest

import com.bluedigits.iota.stresstest.Configuration.{depth, maxAddressAmount, security}
import com.bluedigits.iota.stresstest.IotaApiClient.getAddressByIndex
import io.gatling.core.Predef._
import io.gatling.core.session
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import jota.model.Input

object IotaNodeTasks {

  /* ---------- API test tasks ---------- */

  private val getNodeInfo = exec(http("getNodeInfo")
    .post("/")
    .header("Content-Type", "application/json")
    .body(StringBody("""{"command": "getNodeInfo"}"""))
    .asJSON
  )

  private val getBalances = exec(http("getBalances")
    .post("/")
    .header("Content-Type", "application/json")
    .body(StringBody("""{"command": "getBalances", "addresses": [${address}], "threshold": 100}"""))
    .asJSON
    .check(jsonPath("$.balances[0]").saveAs("balance"))
  )

  private val getTxsToApprove = exec(http("getTxsToApprove")
    .post("/")
    .header("Content-Type", "application/json")
    .body(StringBody(s"""{"command": "getTransactionsToApprove", "depth": $depth}"""))
    .asJSON
    .check(jsonPath("$.trunkTransaction").saveAs("trunkTx"))
    .check(jsonPath("$.branchTransaction").saveAs("branchTx"))
  )

  private val getTxTrytes = exec(s => {
    val input = new Input(s.get("address").as[String], s.get("balance").as[String].toInt, s.get("index").as[String].toInt, security)
    val trytes = IotaApiClient.getTxTrytes(input, getAddressByIndex(s.get("index").as[String].toInt), 1, security)
    s.set("trytes", trytes.get(0))
  })

  private val attachToTangle = exec(http("attachToTangle")
    .post("/")
    .header("Content-Type", "application/json")
    .body(StringBody("""{"command": "attachToTangle", "trunkTransaction": "${trunkTx}", "branchTransaction": "${branchTx}", "minWeightMagnitude": 15, "trytes": ["${trytes}"]}"""))
    .check(jsonPath("$.trytes").saveAs("powedTrytes"))
  )

  private val broadcastTxs = exec(http("broadcastTransactions")
    .post("/")
    .header("Content-Type", "application/json")
    .body(StringBody("""{"command": "broadcastTransactions", "trytes": ${powedTrytes}}"""))
  )

  private val storeTxs = exec(http("storeTransactions")
    .post("/")
    .header("Content-Type", "application/json")
    .body(StringBody("""{"command": "storeTransactions", "trytes": ${powedTrytes}}"""))
  )

  /* ---------- Gatling simulation config stuff ---------- */

  /**
    * Creates an indexed sequence of maps containing addresses generated from random seed index between 0 and max no. of addresses.
    *
    * @return Indexed sequence of maps
    */
  private val addresses: IndexedSeq[Map[String, String]] = for (i <- 0 until maxAddressAmount) yield Map("address" -> getAddressByIndex(i), "index" -> i.toString)

  private val feeder = addresses.circular

  private def initValue(key: String, value: String) = exec(_.set(key, value))

  private val balanceCondition: session.Expression[Boolean] = _.get("balance").as[String].toInt < 1

  private val sendIotaChain = initValue("balance", "0")
    .asLongAs(balanceCondition) {
      feed(feeder).exec(getBalances)
    }
    .exec(getTxsToApprove)
    .exec(getTxTrytes)
    .exec(attachToTangle)
    .exec(broadcastTxs)
    .exec(storeTxs)

  val scnGetNodeInfo: ScenarioBuilder = ScenarioHelper.createScenario("Get node info", getNodeInfo)
  val scnSendIota: ScenarioBuilder = ScenarioHelper.createScenario("Send IOTA to own address", sendIotaChain)
}

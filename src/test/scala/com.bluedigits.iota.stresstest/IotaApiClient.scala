package com.bluedigits.iota.stresstest

import java.time.Instant
import java.util
import java.util.Collections

import com.bluedigits.iota.stresstest.Configuration.{security, seed}
import jota.model.{Bundle, Input}
import jota.pow.JCurl
import jota.utils.IotaAPIUtils

object IotaApiClient {

  /* ---------- Wrapped functionality from Jota lib ---------- */

  private val jCurl = new JCurl

  def getAddressByIndex(index: Int) = IotaAPIUtils.newAddress(seed, security, index, false, jCurl)

  def getTxTrytes(input: Input, destAddress: String, iotaAmount: Long, security: Int): util.List[String] = {
    val timestamp = Instant.now().getEpochSecond
    val message = "IOTASTRESSTESTRANSACTIONMESSAGE".padTo(2187, '9')
    val fragments = Collections.singletonList(message)
    val tag = "IOTASTRESSTESTRANSFER".padTo(27, '9')
    val bundle = new Bundle()
    bundle.addEntry(1, destAddress, iotaAmount, tag, timestamp)
    IotaAPIUtils.signInputsAndReturn(seed, Collections.singletonList(input), bundle, fragments, jCurl)
  }
}

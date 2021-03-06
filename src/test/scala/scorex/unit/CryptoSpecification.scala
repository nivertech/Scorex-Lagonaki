package scorex.unit

import org.scalatest.FunSuite
import scorex.account.PrivateKeyAccount
import scorex.crypto.{Base58, Crypto}

import scala.util.Random

class CryptoSpecification extends FunSuite {
  test("base58 roundtrip"){
    val b58 = "1AGNa15ZQXAZUgFiqJ2i7Z2DPU2J6hW62i"
    Base58.encode(Base58.decode(b58).get) == b58
  }

  test("sign then verify") {
    val acc = new PrivateKeyAccount(Random.nextString(20).getBytes)
    val data = Random.nextString(30).getBytes

    val sig = Crypto.sign(acc, data)
    val rightKey = acc.publicKey
    assert(Crypto.verify(sig, data, rightKey))

    val wrongKey = new PrivateKeyAccount(Random.nextString(20).getBytes).publicKey
    assert(!Crypto.verify(sig, data, wrongKey))
  }
}
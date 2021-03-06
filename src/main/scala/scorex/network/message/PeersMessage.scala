package scorex.network.message

import java.net.{InetAddress, InetSocketAddress}
import java.util

import com.google.common.primitives.{Bytes, Ints}
import scorex.settings.Settings


case class PeersMessage(peers: Seq[InetSocketAddress]) extends Message {

  import scorex.network.message.PeersMessage._

  override val messageType = Message.PEERS_TYPE

  override lazy val dataBytes = {
    val length = peers.size
    val lengthBytes = Bytes.ensureCapacity(Ints.toByteArray(length), DATA_LENGTH, 0)

    peers.foldLeft(lengthBytes) { case (bytes, peer) =>
      Bytes.concat(bytes, peer.getAddress.getAddress)
    }
  }
}


object PeersMessage {
  private val ADDRESS_LENGTH = 4
  private val DATA_LENGTH = 4

  def apply(data: Array[Byte]): PeersMessage = {
    //READ LENGTH
    val lengthBytes = util.Arrays.copyOfRange(data, 0, DATA_LENGTH)
    val length = Ints.fromByteArray(lengthBytes)

    //CHECK IF DATA MATCHES LENGTH
    if (data.length != DATA_LENGTH + (length * ADDRESS_LENGTH))
      throw new Exception("Data does not match length")

    val peers = (0 to length - 1).map { i =>
      val position = lengthBytes.length + (i * ADDRESS_LENGTH)
      val addressBytes = util.Arrays.copyOfRange(data, position, position + ADDRESS_LENGTH)
      val address = InetAddress.getByAddress(addressBytes)
      new InetSocketAddress(address, Settings.Port)
    }

    new PeersMessage(peers)
  }
}
/*
 * Copyright (C) 2017  Fluence Labs Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fluence.multiaddr

import enumeratum._

import scala.collection.immutable

sealed abstract class Protocol(
  val code: Int,
  val size: Int,
  override val entryName: String
) extends EnumEntry

object Protocol extends Enum[Protocol] {

  val values: immutable.IndexedSeq[Protocol] = findValues

  val LengthPrefixedVarSize: Int = -1

  private val byCode = values.map(p => p.code -> p).toMap

  def withCodeOption(code: Int): Option[Protocol] = byCode.get(code)

  case object IP4 extends Protocol(4, 32, "ip4")
  case object TCP extends Protocol(6, 16, "tcp")
  case object UDP extends Protocol(17, 16, "udp")
  case object DCCP extends Protocol(33, 16, "dccp")
  case object IP6 extends Protocol(41, 128, "ip6")
  case object DNS extends Protocol(53, LengthPrefixedVarSize, "dns")
  case object DNS4 extends Protocol(54, LengthPrefixedVarSize, "dns4")
  case object DNS6 extends Protocol(55, LengthPrefixedVarSize, "dns6")
  case object DNSADDR extends Protocol(56, LengthPrefixedVarSize, "dnsaddr")
  case object SCTP extends Protocol(132, 16, "sctp")
  case object UDT extends Protocol(301, 0, "udt")
  case object UTP extends Protocol(302, 0, "utp")
  case object UNIX extends Protocol(400, LengthPrefixedVarSize, "unix")
  case object P2P extends Protocol(421, LengthPrefixedVarSize, "p2p") //preferred over /ipfs
  case object ONION extends Protocol(444, 96, "onion")
  case object QUIC extends Protocol(460, 0, "quic")
  case object HTTP extends Protocol(480, 0, "http")
  case object HTTPS extends Protocol(443, 0, "https")
  case object WS extends Protocol(477, 0, "ws")
  case object WSS extends Protocol(478, 0, "wss")
  case object P2PWebsocketStar extends Protocol(479, 0, "p2p-websocket-star")
  case object P2PWebrtcStar extends Protocol(275, 0, "p2p-webrtc-star")
  case object P2PWebrtcDirect extends Protocol(276, 0, "p2p-webrtc-direct")
  case object P2PCircuit extends Protocol(290, 0, "p2p-circuit")
}

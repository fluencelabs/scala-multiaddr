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

import fluence.multiaddr.Multiaddr.ErrorMessage

/**
  * Multiaddress representation by string and list of protocols.
  */
case class Multiaddr private(address: String, protoParameters: List[ProtoParameter]) {

  /**
    * Encapsulates a Multiaddr in another Multiaddr.
    *
    * Spec about encapsulate:
    * https://github.com/multiformats/multiaddr#encapsulation-based-on-context
    *
    * val m = Multiaddr("/ip4/127.0.0.1/tcp/1234")
    * println(m.encapsulate(Multiaddr.unsafe("/sctp/5678")).right.get.address)
    * "/ip4/127.0.0.1/tcp/1234/sctp/5678"
    *
    * @param addr Multiaddr to add into this Multiaddr
    * @return new Multiaddr
    */
  def encapsulate(addr: Multiaddr): Either[ErrorMessage, Multiaddr] = Multiaddr(address + addr.address)

  /**
    * Decapsulates a Multiaddr from another Multiaddr.
    *
    * Spec about encapsulate and decapsulate:
    * https://github.com/multiformats/multiaddr#encapsulation-based-on-context
    *
    * val m = Multiaddr("/ip4/127.0.0.1/tcp/1234/sctp/5678")
    * println(m.decapsulate(Multiaddr.unsafe("/sctp/5678")).right.get.address)
    * "/ip4/127.0.0.1/tcp/1234"
    *
    * @param addr Multiaddr to remove from this Multiaddr
    *
    * @return decapsulated Multiaddr
    */
  def decapsulate(addr: Multiaddr): Either[ErrorMessage, Multiaddr] = decapsulate(addr.address)

  /**
    * Possibility to decapsulate by part of address.
    *
    * Spec about encapsulate and decapsulate:
    * https://github.com/multiformats/multiaddr#encapsulation-based-on-context
    *
    * val m = Multiaddr("/ip4/127.0.0.1/tcp/1234/sctp/5678")
    * println(m.decapsulate("/sctp").right.get.address)
    * "/ip4/127.0.0.1/tcp/1234"
    *
    * @param addr Multiaddr to remove from this Multiaddr
    * @return decapsulated Multiaddr
    */
  def decapsulate(addr: String): Either[ErrorMessage, Multiaddr] = {
    val lastIndex = address.lastIndexOf(addr)
    if (lastIndex < 0)
      Right(this)
    else
      Multiaddr(address.slice(0, lastIndex))
  }

}

object Multiaddr {

  type ErrorMessage = String

  /**
    * Parse and validate multiaddr string.
    */
  def apply(addr: String): Either[ErrorMessage, Multiaddr] = MultiaddrParser.parse(addr).map {
    case (trimmed, protoParameters) => new Multiaddr(trimmed, protoParameters)
  }

  def unsafe(addr: String): Multiaddr = apply(addr).right.get
}

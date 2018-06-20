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

case class Multiaddr private(address: String, protoParameters: List[ProtoParameter]) {
  /**
    * Wraps a given Multiaddr, returning the resulting joined Multiaddr.
    *
    * @return new joined Multiaddr
    */
  def encapsulate(addr: Multiaddr): Either[ErrorMessage, Multiaddr] = Multiaddr(address + addr.address)

  /**
    * Decapsulate unwraps Multiaddr up until the given Multiaddr is found.
    *
    * @return decapsulated Multiaddr
    */
  def decapsulate(addr: Multiaddr): Either[ErrorMessage, Multiaddr] = decapsulate(addr.address)


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

  def apply(addr: String): Either[ErrorMessage, Multiaddr] = MultiaddrParser.parse(addr).map {
    case (trimmed, protoParameters) => new Multiaddr(trimmed, protoParameters)
  }

  def unsafe(addr: String): Multiaddr = apply(addr).right.get
}

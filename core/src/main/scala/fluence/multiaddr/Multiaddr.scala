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

case class Multiaddr private(stringAddress: String, protocolsWithParameters: List[(Protocol, Option[String])]) {
  override def toString: String = stringAddress

  /**
    * Wraps a given Multiaddr, returning the resulting joined Multiaddr.
    *
    * @return new joined Multiaddr
    */
  def encapsulate(addr: Multiaddr): Either[Throwable, Multiaddr] = {
    Multiaddr(stringAddress + addr.toString)
  }

  /**
    * Decapsulate unwraps Multiaddr up until the given Multiaddr is found.
    *
    * @return decapsulated Multiaddr
    */
  def decapsulate(addr: Multiaddr): Either[Throwable, Multiaddr] = {
    val strAddr = addr.toString
    val lastIndex = stringAddress.lastIndexOf(strAddr)
    if (lastIndex < 0)
      Right(this.copy())
    else
      Multiaddr(stringAddress.slice(0, lastIndex))
  }

}

object Multiaddr {

  def apply(addr: String): Either[Throwable, Multiaddr] = MultiaddrParser.parse(addr).map {
    case (trimmed, protocols) => new Multiaddr(trimmed, protocols)
  }
}

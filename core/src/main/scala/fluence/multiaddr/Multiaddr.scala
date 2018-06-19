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

import scala.annotation.tailrec

case class Multiaddr private(protocols: List[(Protocol, Option[String])])

object Multiaddr {

  def apply(addr: String): Either[Throwable, Multiaddr] = {
    if (!addr.startsWith("/")) {
      Left(new IllegalArgumentException("Address must be started with '/'."))
    } else {
      val parts = addr.stripPrefix("/").stripSuffix("/").split("/").toList

      if (parts.isEmpty) {
        Left(new IllegalArgumentException("Address must be non-empty."))
      } else {

        parse(parts).map(protocols ⇒ new Multiaddr(protocols))
      }
    }
  }

  private def parse(list: List[String]): Either[Throwable, List[(Protocol, Option[String])]] = {

    @tailrec
    def parseRec(
      list: List[String],
      res: Either[Throwable, List[(Protocol, Option[String])]]
    ): Either[Throwable, List[(Protocol, Option[String])]] = {
      list match {
        case Nil ⇒ res
        case head :: tail ⇒
          val protocolOp = Protocol.withNameOption(head)

          protocolOp match {
            case None ⇒
              Left(new IllegalArgumentException(s"There is no protocol with name '$head'."))
            case Some(protocol) ⇒
              protocol.size match {
                case 0 ⇒ parseRec(tail, res.map(els ⇒ els :+ (protocol, None)))
                case _ ⇒
                  tail match {
                    case Nil ⇒
                      Left(new IllegalArgumentException(s"There is no parameter for protocol with name '$head'."))
                    case innerHead :: innerTail ⇒
                      parseRec(innerTail, res.map(els ⇒ els :+ (protocol, Some(innerHead))))
                  }
              }
          }
      }
    }

    parseRec(list, Right(List.empty))
  }
}

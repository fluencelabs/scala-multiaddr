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
import fluence.multiaddr.Protocol._

import scala.annotation.tailrec
import scala.util.Try

private[multiaddr] object MultiaddrParser {

  def parse(addr: String): Either[ErrorMessage, (String, List[ProtoParameter])] = {
    if (!addr.startsWith("/")) {
      Left("Address must be started with '/'.")
    } else {
      val parts = addr.stripPrefix("/").stripSuffix("/").split("/").toList

      if (parts.isEmpty) {
        Left("Address must be non-empty.")
      } else {

        parsePrepared(parts).map(protocols ⇒ (addr.stripSuffix("/"), protocols))
      }
    }
  }

  private def parseParameter(parameter: String, protocol: Protocol): Either[String, ProtoParameter] = {
    protocol match {
      case TCP | UDP ⇒
        Try(parameter.toInt).toEither.right
          .map(n ⇒ IntProtoParameter(protocol, n))
          .left
          .map(_ ⇒ s"Parameter for protocol $protocol must be a number.")
      case _ ⇒
        Right(StringProtoParameter(protocol, parameter))
    }
  }

  private def parsePrepared(list: List[String]): Either[ErrorMessage, List[ProtoParameter]] = {

    @tailrec
    def parseRec(
      list: List[String],
      accum: Either[ErrorMessage, List[ProtoParameter]]
    ): Either[ErrorMessage, List[ProtoParameter]] = {
      list match {
        case Nil ⇒ accum
        case head :: tail ⇒
          //todo per-protocol validation
          val protocolOp = Protocol.withNameOption(head)

          protocolOp match {
            case None ⇒
              Left(s"There is no protocol with name '$head'.")
            case Some(protocol) ⇒
              protocol.size match {
                case 0 ⇒
                  parseRec(tail, accum.map(els ⇒ els :+ EmptyProtoParameter(protocol)))
                case _ ⇒
                  tail match {
                    case Nil ⇒
                      Left(s"There is no parameter for protocol with name '$head'.")
                    case parameter :: innerTail ⇒
                      val partialResult =
                        for {
                          elements ← accum
                          parameter ← parseParameter(parameter, protocol)
                        } yield elements :+ parameter

                      parseRec(
                        innerTail,
                        partialResult
                      )
                  }
              }
          }
      }
    }

    parseRec(list, Right(List.empty))
  }
}

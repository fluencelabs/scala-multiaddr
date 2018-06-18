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

import org.scalatest.{Matchers, WordSpec}

class ProtocolsEnumSpec extends WordSpec with Matchers {
  "protocols enum" should {
    "get correct enum by name" in {
      Protocol.withNameOption(Protocol.IP4.entryName) shouldBe Option(Protocol.IP4)

      Protocol.withNameInsensitiveOption("QuIc") shouldBe Option(Protocol.QUIC)

      Protocol.withNameOption("something-wrong") shouldBe None
    }

    "get correct enum by code" in {
      Protocol.withCodeOption(Protocol.DCCP.code) shouldBe Option(Protocol.DCCP)

      Protocol.withCodeOption(12345) shouldBe None
    }
  }
}

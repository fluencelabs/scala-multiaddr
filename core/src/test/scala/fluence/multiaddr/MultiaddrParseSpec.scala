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

class MultiaddrParseSpec extends WordSpec with Matchers {

  import Protocol._

  "multiaddr" should {
    "throw exception if there is no leading '/'" in {
      val m = Multiaddr("ip4/127.0.0.1/tcp/123")
      m.isLeft shouldBe true
      m.left.get.getMessage shouldBe "Address must be started with '/'."
    }

    "parse correct multiaddresses right" in {
      val m1 = Multiaddr("/ip4/127.0.0.1/tcp/123")
      m1.isRight shouldBe true
      m1.right.get.protocols shouldBe List((IP4, Some("127.0.0.1")), (TCP, Some("123")))

      val m2 = Multiaddr("/ip6/2001:8a0:7ac5:4201:3ac9:86ff:fe31:7095/udp/5000/https")
      m2.isRight shouldBe true
      m2.right.get.protocols shouldBe List((IP6, Some("2001:8a0:7ac5:4201:3ac9:86ff:fe31:7095")), (UDP, Some("5000")), (HTTPS, None))
    }

    "throw exception if there is no protocol" in {
      val m = Multiaddr("/ip4/127.0.0.1/tc/123")
      m.isLeft shouldBe true
      m.left.get.getMessage shouldBe "There is no protocol with name 'tc'."
    }

    "throw exception if there is no parameter in protocol with parameter" in {
      val m = Multiaddr("/ip4/127.0.0.1/tcp/")
      m.isLeft shouldBe true
      m.left.get.getMessage shouldBe "There is no parameter for protocol with name 'tcp'."
    }
  }
}

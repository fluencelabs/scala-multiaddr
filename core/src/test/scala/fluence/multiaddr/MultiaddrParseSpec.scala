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
      m.left.get shouldBe "Address must be started with '/'."
    }

    "parse correct multiaddresses right" in {
      val addr1 = "/ip4/127.0.0.1/tcp/123"
      val m1Either = Multiaddr(addr1)
      m1Either.isRight shouldBe true
      val m1 = m1Either.right.get

      m1.protoParameters shouldBe List(StringProtoParameter(IP4, "127.0.0.1"), IntProtoParameter(TCP, 123))
      m1.address shouldBe addr1

      val addr2 = "/ip6/2001:8a0:7ac5:4201:3ac9:86ff:fe31:7095/udp/5000/https"
      val m2Either = Multiaddr(addr2)
      m2Either.isRight shouldBe true
      val m2 = m2Either.right.get

      m2.protoParameters shouldBe List(
        StringProtoParameter(IP6, "2001:8a0:7ac5:4201:3ac9:86ff:fe31:7095"),
        IntProtoParameter(UDP, 5000),
        EmptyProtoParameter(HTTPS)
      )
      m2.address shouldBe addr2
    }

    "throw exception if there is no protocol" in {
      val m = Multiaddr("/ip4/127.0.0.1/tc/123")
      m.isLeft shouldBe true
      m.left.get shouldBe "There is no protocol with name 'tc'."
    }

    "throw exception if there is no parameter in protocol with parameter" in {
      val m = Multiaddr("/ip4/127.0.0.1/tcp/")
      m.isLeft shouldBe true
      m.left.get shouldBe "There is no parameter for protocol with name 'tcp'."
    }

    "encapsulate and decapsulate correct multiaddr" in {
      val addr1 = "/ip4/127.0.0.1/tcp/123"
      val m1Either = Multiaddr(addr1)
      m1Either.isRight shouldBe true
      val m1 = m1Either.right.get

      val addr2 = "/ip6/2001:8a0:7ac5:4201:3ac9:86ff:fe31:7095/udp/5000/https"
      val m2Either = Multiaddr(addr2)
      m2Either.isRight shouldBe true
      val m2 = m2Either.right.get

      val m3Either = m1.encapsulate(m2)
      m3Either.isRight shouldBe true
      val m3 = m3Either.right.get

      val result = List(
        StringProtoParameter(IP4, "127.0.0.1"),
        IntProtoParameter(TCP, 123),
        StringProtoParameter(IP6, "2001:8a0:7ac5:4201:3ac9:86ff:fe31:7095"),
        IntProtoParameter(UDP, 5000),
        EmptyProtoParameter(HTTPS)
      )
      m3.protoParameters shouldBe result
      m3.address shouldBe (addr1 + addr2)

      m3.decapsulate(m2).right.get.address shouldBe addr1
    }

    "decapsulate correct multiaddr" in {
      val addr1 = "/ip4/127.0.0.1/udp/1234/sctp/5678"
      val m1Either = Multiaddr(addr1)
      m1Either.isRight shouldBe true
      val m1 = m1Either.right.get

      val decapsulated = m1.decapsulate(Multiaddr("/sctp/5678").right.get).right.get
      decapsulated.address shouldBe "/ip4/127.0.0.1/udp/1234"
    }
  }
}

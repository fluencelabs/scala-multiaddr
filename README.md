# scala-multiaddr

> [multiaddr](https://github.com/multiformats/multiaddr) implementation in scala

Multiaddr is a standard way to represent addresses that:

- Support any standard network protocols.
- Self-describe (include protocols).
- Have a binary packed format.
- Have a nice string representation.
- Encapsulate well.

## Install

```scala
// Bintray repo is used so far. Migration to Maven Central is planned
resolvers += Resolver.bintrayRepo("fluencelabs", "releases")

libraryDependencies += "one.fluence" %%% "scala-multiaddr" % "0.0.1"
```

## Usage

### Example

#### Simple

```scala
import fluence.multiaddr.Multiaddr

val addr = "/ip4/127.0.0.1/tcp/1234"
// construct from a string (ErrorMessage return error message in ther parsing process)
val mEither: Either[ErrorMessage, Multiaddr] = Multiaddr(addr)
val m = mEither.right.get

// true
m.address == "/ip4/127.0.0.1/tcp/1234"
```

#### Protocols

```scala
// get the multiaddr protocol description objects
m.protoParameters

//List(
//  StringProtoParameter(IP4, "127.0.0.1"),
//  IntProtoParameter(TCP, 1234),
//  EmptyProtoParameter(HTTP)
//)
```

#### En/decapsulate

```scala
m.encapsulate(Multiaddr.unsafe("/sctp/5678"))
// Multiaddr(/ip4/127.0.0.1/tcp/1234/sctp/5678,List(StringProtoParameter(IP4,127.0.0.1), IntProtoParameter(TCP,1234), IntProtoParameter(SCTP,5678)))
m.decapsulate("/tcp") // up to + inc last occurrence of subaddr
// Multiaddr(/ip4/127.0.0.1,List(StringProtoParameter(IP4,127.0.0.1)))
```

#### Tunneling

Multiaddr allows expressing tunnels very nicely.

```scala
val addr = Multiaddr.unsafe("/ip4/192.168.0.13/tcp/80")
val proxy = Multiaddr.unsafe("/ip4/10.20.30.40/tcp/443")
val addrOverProxy := proxy.encapsulate(m)
// /ip4/10.20.30.40/tcp/443/ip4/192.168.0.13/tcp/80

val proxyAgain = addrOverProxy.decapsulate(addr)
// /ip4/10.20.30.40/tcp/443
```
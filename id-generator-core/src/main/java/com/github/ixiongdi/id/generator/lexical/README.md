# LexicalUUID

Twitter, "Cassie", commit f6da4e0, November 2012, <https://github.com/twitter-archive/cassie>.

## 算法

[原文](https://github.com/twitter-archive/cassie#generating-unique-ids)

如果您要在 Cassandra 中存储数据，但没有自然唯一的数据作为密钥，那么您可能已经研究过 UUID。UUID 的唯一问题是它们是精神上的，需要访问 MAC 地址或公历或 POSIX ID。一般来说，人们想要的 UUID 是：

在一大批工作人员中具有独特性，无需协调。
部分按时间排序。
Cassie 的LexicalUUID满足这些条件。它们的长度为 128 位。最高 64 位是时间戳值（来自 Cassie 的严格递增Clock实现）。最低 64 位是工作器 ID，默认值是机器主机名的哈希值。

当使用 Cassandra 的 进行排序时LexicalUUIDType，LexicalUUIDs 将按时间进行部分排序 - 也就是说，在单个进程上按顺序生成的 UUID 将完全按时间排序；同时生成的 UUID（即，在同一个时钟滴答内，给定时钟偏差）将没有确定的顺序；在单个进程之间按顺序生成的 UUID（即，在不同的时钟滴答中，给定时钟偏差）将完全按时间排序。

请参阅Lamport。分布式系统中的时间、时钟和事件排序。ACM 通讯 (1978) 第 21 (7) 卷第 565 页和Mattern。分布式系统的虚拟时间和全局状态。并行和分布式算法 (1989) 第 215-226 页，了解更深入的讨论。


## 实现

`cassie-core/src/main/scala/com/twitter/cassie/types/LexicalUUID.scala`

```scala
// Copyright 2012 Twitter, Inc.

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.twitter.cassie.types

import com.twitter.cassie.clocks.Clock
import com.twitter.cassie.FNV1A
import java.net.InetAddress.{ getLocalHost => localHost }
import java.nio.ByteBuffer
import org.apache.commons.codec.binary.Hex.decodeHex

object LexicalUUID {
  private val defaultWorkerID = FNV1A(localHost.getHostName.getBytes)

  /**
   * Given a clock, generates a new LexicalUUID, using a hash of the machine's
   * hostname as a worker ID.
   */
  def apply(clock: Clock): LexicalUUID =
    new LexicalUUID(clock, LexicalUUID.defaultWorkerID)

  /**
   * Given a UUID formatted as a hex string, returns it as a LexicalUUID.
   */
  def apply(uuid: String): LexicalUUID = {
    val buf = ByteBuffer.wrap(decodeHex(uuid.toCharArray.filterNot { _ == '-' }))
    new LexicalUUID(buf.getLong(), buf.getLong())
  }
}

/**
 * A 128-bit UUID, composed of a 64-bit timestamp and a 64-bit worker ID.
 */
case class LexicalUUID(timestamp: Long, workerID: Long) extends Ordered[LexicalUUID] {

  /**
   * Given a worker ID and a clock, generates a new LexicalUUID. If each node
   * has unique worker ID and a clock which is guaranteed to never go backwards,
   * then each generated UUID will be unique.
   */
  def this(clock: Clock, workerID: Long) = this(clock.timestamp, workerID)

  /**
   * Given a clock, generates a new LexicalUUID, using a hash of the machine's
   * hostname as a worker ID.
   */
  def this(clock: Clock) = this(clock.timestamp, LexicalUUID.defaultWorkerID)

  /**
   * Sort by timestamp, then by worker ID.
   */
  def compare(that: LexicalUUID) = {
    val res = timestamp.compare(that.timestamp)
    if (res == 0) {
      workerID.compare(that.workerID)
    } else {
      res
    }
  }

  override def toString = {
    val hex = "%016x".format(timestamp)
    "%s-%s-%s-%016x".format(hex.substring(0, 8),
      hex.substring(8, 12),
      hex.substring(12, 16), workerID)
  }
}
```

`cassie-core/src/main/scala/com/twitter/cassie/FNV1A.scala`

```scala
// Copyright 2012 Twitter, Inc.

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.twitter.cassie

/**
 * The FNV1-A 64-bit hashing algorithm.
 */
object FNV1A extends (Array[Byte] => Long) {
  private val offsetBasis = 0xcbf29ce484222325L
  private val prime = 0x100000001b3L

  def apply(ary: Array[Byte]): Long = {
    var n = offsetBasis
    var i = 0
    while (i < ary.length) {
      n = (n ^ ary(i)) * prime
      i += 1
    }
    n
  }
}
```

`cassie-core/src/main/scala/com/twitter/cassie/clocks/StrictlyIncreasingClock.scala`
```scala
// Copyright 2012 Twitter, Inc.

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.twitter.cassie.clocks

import java.util.concurrent.atomic.AtomicLong

/**
 * A concurrent, strictly-increasing clock.
 */
abstract class StrictlyIncreasingClock extends Clock {
  private val counter = new AtomicLong(tick)

  def timestamp: Long = {
    var newTime: Long = 0
    while (newTime == 0) {
      val last = counter.get
      val current = tick
      val next = if (current > last) current else last + 1
      if (counter.compareAndSet(last, next)) {
        newTime = next
      }
    }
    return newTime
  }

  protected def tick: Long
}
```
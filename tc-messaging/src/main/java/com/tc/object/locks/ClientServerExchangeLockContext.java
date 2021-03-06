/*
 *
 *  The contents of this file are subject to the Terracotta Public License Version
 *  2.0 (the "License"); You may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at
 *
 *  http://terracotta.org/legal/terracotta-public-license.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.
 *
 *  The Covered Software is Terracotta Core.
 *
 *  The Initial Developer of the Covered Software is
 *  Terracotta, Inc., a Software AG company
 *
 */
package com.tc.object.locks;

import com.tc.io.TCByteBufferInput;
import com.tc.io.TCByteBufferOutput;
import com.tc.io.TCSerializable;
import com.tc.net.NodeID;
import com.tc.net.groups.NodeIDSerializer;
import com.tc.object.locks.ServerLockContext.State;
import com.tc.object.locks.ServerLockContext.Type;

import java.io.IOException;

public class ClientServerExchangeLockContext implements TCSerializable<ClientServerExchangeLockContext> {
  private static final State[] STATE_VALUES = State.values();
  
  private LockID   lockID;
  private NodeID   nodeID;
  private ThreadID threadID;
  private State    state;
  private long     timeout;

  public ClientServerExchangeLockContext() {
    // to make TCSerializable happy
  }

  public ClientServerExchangeLockContext(LockID lockID, NodeID nodeID, ThreadID threadID, State state) {
    this(lockID, nodeID, threadID, state, -1);
  }

  public ClientServerExchangeLockContext(LockID lockID, NodeID nodeID, ThreadID threadID, State state, long timeout) {
    this.lockID = lockID;
    this.nodeID = nodeID;
    this.threadID = threadID;
    this.state = state;
    this.timeout = timeout;
  }

  public NodeID getNodeID() {
    return nodeID;
  }

  public LockID getLockID() {
    return lockID;
  }

  public State getState() {
    return this.state;
  }

  public ThreadID getThreadID() {
    return threadID;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ClientServerExchangeLockContext)) return false;
    ClientServerExchangeLockContext cmp = (ClientServerExchangeLockContext) o;
    return lockID.equals(cmp.lockID) && threadID.equals(cmp.threadID) && state.equals(cmp.state)
           && nodeID.equals(cmp.nodeID);
  }

  @Override
  public int hashCode() {
    return (11 * lockID.hashCode()) ^ (7 * threadID.hashCode()) ^ (3 * state.hashCode()) ^ (13 * nodeID.hashCode());
  }

  @Override
  public void serializeTo(TCByteBufferOutput output) {
    LockIDSerializer ls = new LockIDSerializer(lockID);
    ls.serializeTo(output);
    NodeIDSerializer ns = new NodeIDSerializer(this.nodeID);
    ns.serializeTo(output);
    output.writeLong(threadID.toLong());
    output.writeInt(state.ordinal());
    if (state.getType() == Type.WAITER || state.getType() == Type.TRY_PENDING) {
      output.writeLong(timeout);
    }
  }

  @Override
  public ClientServerExchangeLockContext deserializeFrom(TCByteBufferInput input) throws IOException {
    LockIDSerializer ls = new LockIDSerializer();
    ls.deserializeFrom(input);
    this.lockID = ls.getLockID();
    NodeIDSerializer ns = new NodeIDSerializer();
    ns.deserializeFrom(input);
    nodeID = ns.getNodeID();
    threadID = new ThreadID(input.readLong());
    state = STATE_VALUES[input.readInt()];
    if (state.getType() == Type.WAITER || state.getType() == Type.TRY_PENDING) {
      this.timeout = input.readLong();
    } else {
      timeout = -1;
    }
    return this;
  }

  public long timeout() {
    return this.timeout;
  }

  @Override
  public String toString() {
    return "ClientServerExchangeLockContext [lockID=" + lockID + ", nodeID=" + nodeID + ", state=" + state
           + ", threadID=" + threadID + "]";
  }
}

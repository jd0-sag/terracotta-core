/*
 * All content copyright (c) 2003-2009 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.objectserver.tx;


public interface TransactionFilter {

  public void addTransactionBatch(TransactionBatchContext transactionBatchContext);

}

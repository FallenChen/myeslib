package org.myeslib.hazelcast;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionContext;

public class HzStringTxMapFactory<K> {
	
	public TransactionalMap<K, String> get(final TransactionContext context, final String mapId) {
		TransactionalMap<K, String> aggregateRootHistoryMap = context.getMap(mapId);
		return aggregateRootHistoryMap;
	}

}
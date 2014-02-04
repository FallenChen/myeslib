package org.myeslib.storage;

import org.myeslib.core.AggregateRoot;
import org.myeslib.core.Command;
import org.myeslib.core.CommandHandler;
import org.myeslib.data.UnitOfWork;

public interface CommandHandlerInvoker<K, A extends AggregateRoot> {

	public abstract UnitOfWork invoke(K id, Long version, Command command,
			CommandHandler<A> commandHandler) throws Throwable;

}
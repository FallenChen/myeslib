package org.myeslib.example.jdbi;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.myeslib.example.jdbi.modules.CamelModule;
import org.myeslib.example.jdbi.modules.DatabaseModule;
import org.myeslib.example.jdbi.modules.HazelcastModule;
import org.myeslib.example.jdbi.modules.InventoryItemModule;
import org.myeslib.example.jdbi.routes.JdbiConsumeCommandsRoute;
import org.myeslib.example.jdbi.routes.JdbiConsumeEventsRoute;
import org.myeslib.util.example.ReceiveCommandsAsJsonRoute;
import org.myeslib.util.hazelcast.HzCamelComponent;
import org.myeslib.util.jdbi.ArTablesMetadata;
import org.skife.jdbi.v2.DBI;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Slf4j
public class JdbiExample {
	
    final Main main;
	final SimpleRegistry registry;
	final CamelContext context;

	static int jettyMinThreads;
	static int jettyMaxThreads;
	static int dbPoolMinThreads;
	static int dbPoolMaxThreads;

	public static void main(String[] args) throws Exception {

		log.info("starting...");
		
		jettyMinThreads = args.length ==0 ? 10 : new Integer(args[0]);  
		jettyMaxThreads = args.length <=1 ? 100 : new Integer(args[1]);  
		dbPoolMinThreads = args.length <=2 ? 10 : new Integer(args[2]);  
		dbPoolMaxThreads = args.length <=3 ? 100 : new Integer(args[3]);  
		
		log.info("jettyMinThreads = {}", jettyMinThreads);
		log.info("jettyMaxThreads = {}", jettyMaxThreads);
		log.info("dbPoolMinThreads = {}", dbPoolMinThreads);
		log.info("dbPoolMaxThreads = {}", dbPoolMaxThreads);
		
		Injector injector = Guice.createInjector(new CamelModule(jettyMinThreads, jettyMaxThreads), 
				                                 new DatabaseModule(dbPoolMinThreads, dbPoolMaxThreads), 
				                                 new HazelcastModule(), 
				                                 new InventoryItemModule());
		
	    JdbiExample example = injector.getInstance(JdbiExample.class);
		example.main.run();
		
	}
		
	@Inject
	JdbiExample(HzCamelComponent justAnotherHazelcastComponent, 
				ReceiveCommandsAsJsonRoute receiveCommandsRoute, 
				JdbiConsumeCommandsRoute consumeCommandsRoute, 
				JdbiConsumeEventsRoute consumeEventsRoute,
				ArTablesMetadata metadata,
				DBI dbi) throws Exception  {
		
		//new ArhCreateTablesHelper(metadata, dbi).createTables();
		
		main = new Main() ;
		main.enableHangupSupport();
		registry = new SimpleRegistry();
		context = new DefaultCamelContext(registry);
		
		context.addComponent("hz", justAnotherHazelcastComponent);
		context.addRoutes(receiveCommandsRoute);
		context.addRoutes(consumeCommandsRoute);
		context.addRoutes(consumeEventsRoute);
		
		main.getCamelContexts().clear();
		main.getCamelContexts().add(context);
		main.setDuration(-1);
		main.start();
		
		
	}

}


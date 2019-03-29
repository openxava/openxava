package org.openxava.test.tests;

import javax.persistence.EntityManager;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.CallBackLog;
import org.openxava.tests.ModuleTestBase;

/**
 * Tests callback methods.
 * @author Federico Alcantara
 *
 */
public class CallBackLogTest extends ModuleTestBase {
	public CallBackLogTest (String nameTest) {
		super(nameTest, "CallBackLog");
	}
	
	public void setUp() throws Exception {
		super.setUp();
		XPersistence.getManager().createQuery("delete from CallBackLog").executeUpdate();
		XPersistence.commit();
	}
	
	public void testCRUDWithCallBacks() throws Exception {
		execute("CRUD.new");
		assertNoErrors();
		setValue("id", "1");
		setValue("testName", "CREATE_TEST");
		execute("CRUD.save");
		assertNoErrors();
		execute("CRUD.new");
		setValue("id", "1");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("testName", "CREATE_TEST");
		assertValue("callbacks", "PRE_CREATE,PRE_PERSIST,POST_CREATE");
		execute("CRUD.delete");
		execute("CRUD.new");
		setValue("id", "101");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("testName", "ON_PRE_REMOVE");
		assertValue("callbacks", "PRE_REMOVE,PRE_CREATE,PRE_PERSIST,POST_CREATE");
	}
	
	public void testTransactionFailureInCallBack() throws Exception {
		execute("CRUD.new");
		assertNoErrors();
		setValue("id", "1");
		setValue("testName", "CREATE_TEST");
		setValue("callbacks", "ERROR_ON_POST_PERSIST");
		execute("CRUD.save");
		assertErrorsCount(1); 
		execute("Mode.list");
		assertListRowCount(0);
	}
	
	public void testTransactionFailureOnRemove() throws Exception {
		execute("CRUD.new");
		setValue("id", "1");
		setValue("testName", "CREATE_TEST");
		setValue("callbacks", "ERROR_ON_POST_REMOVE");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		execute("CRUD.new");
		setValue("id", "1");
		execute("CRUD.refresh");
		assertNoErrors();
		execute("CRUD.delete");
		assertErrorsCount(1);
		execute("Mode.list");
		assertListRowCount(1);
	}
	
	public void testCreateCallbackWithDefaultManager() throws Exception {
		testManagedCreateCallback(XPersistence.getManager());
	}

	public void testDeleteCallbackWithDefaultManager() throws Exception {
		testManagedDeleteCallback(XPersistence.getManager());
	}

	public void testCreateCallbackWithNewManager() throws Exception {
		testManagedCreateCallback(XPersistence.getManager());
	}

	public void testDeleteCallbackWithNewManager() throws Exception {
		testManagedDeleteCallback(XPersistence.getManager());
	}
	
	private void testManagedCreateCallback(EntityManager em) throws Exception {
		CallBackLog log = new CallBackLog();
		log.setId(1);
		log.setTestName("CREATE_TEST");
		em.persist(log);
		
		CallBackLog savedLog = em.find(CallBackLog.class, new Long(1l));
		assertEquals("Test name wrongfully saved", 1l, savedLog.getId());
		assertEquals("Callbacks failed", "PRE_CREATE,PRE_PERSIST,POST_CREATE", savedLog.getCallbacks());
	}
	
	public void testManagedDeleteCallback(EntityManager em) throws Exception {
		CallBackLog log = new CallBackLog();
		log.setId(1);
		log.setTestName("DELETE_TEST");
		em.persist(log);
		// Let's Delete it
		log = em.find(CallBackLog.class, new Long(1l));
		em.remove(log);
		
		CallBackLog deletedLog = em.find(CallBackLog.class, new Long(101l));
		assertEquals("Test name wrongfully saved", 101l, deletedLog.getId());
		assertEquals("Callbacks failed", "PRE_REMOVE,PRE_CREATE,PRE_PERSIST,POST_CREATE", deletedLog.getCallbacks());
	}
	
}

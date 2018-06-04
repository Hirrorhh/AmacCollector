package com.shjn.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	public static SessionFactory sf;
	public static Session session;
	public static Transaction tx;
	
	public static final Session getSession() {
		Configuration cfg = new Configuration();
		sf = cfg.configure().buildSessionFactory();
		session = sf.openSession();
		tx = session.beginTransaction();

		return session;
	}

	public static final void closeSession() {
		tx.commit();
		session.close();
		sf.close();
	}
}

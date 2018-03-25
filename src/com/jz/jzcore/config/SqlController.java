package com.jz.jzcore.config;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;

public class SqlController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int TRANSACTION_READ_COMMITTED   = 2;
	    
	    protected int getTransactionLevel(com.jfinal.plugin.activerecord.Config config) {
	        return TRANSACTION_READ_COMMITTED;
	    }
	    
	    public Config config=null;
        public Boolean autoCommit = null;

	    public Connection bengin() throws SQLException{
	 	    if (config == null)
	 	        config = DbKit.getConfig();
	 	    
	 	    Connection conn = config.getThreadLocalConnection();
	 	    // 下面这段支持嵌套事务，可以忽略不看
	 	    if (conn != null) {
	 	        try {
	 	            if (conn.getTransactionIsolation() < getTransactionLevel(config))
	 	                conn.setTransactionIsolation(getTransactionLevel(config));
	 	        } catch (SQLException e) {
	 	            throw new ActiveRecordException(e);
	 	        }
	 	    }
		        // 1. 建立数据库连接
		        conn = config.getConnection();
		        autoCommit = conn.getAutoCommit();
		        config.setThreadLocalConnection(conn);
		        // 2. 设置事务隔离级别
		        conn.setTransactionIsolation(getTransactionLevel(config));    // conn.setTransactionIsolation(transactionLevel);
		        // 3. 设置事务手动提交
		        conn.setAutoCommit(true);
		        // 4. 反射机制调用 savePost()
		        
		        return conn;
	 	    
	    }
	 public Config getConfig() {
	    	  Config config=null;
	  	    if (config == null)
	  	    config = DbKit.getConfig();
			return config;
		}
	    
	  public void sql(){ 
	    Config config=null;
	    if (config == null)
	        config = DbKit.getConfig();
	    
	    Connection conn = config.getThreadLocalConnection();
	    // 下面这段支持嵌套事务，可以忽略不看
	    if (conn != null) {
	        try {
	            if (conn.getTransactionIsolation() < getTransactionLevel(config))
	                conn.setTransactionIsolation(getTransactionLevel(config));
	            return ;
	        } catch (SQLException e) {
	            throw new ActiveRecordException(e);
	        }
	    }
	  
	    
	    Boolean autoCommit = null;
	    try {
	        // 1. 建立数据库连接
	        conn = config.getConnection();
	        autoCommit = conn.getAutoCommit();
	        config.setThreadLocalConnection(conn);
	        // 2. 设置事务隔离级别
	        conn.setTransactionIsolation(getTransactionLevel(config));    // conn.setTransactionIsolation(transactionLevel);
	        // 3. 设置事务手动提交
	        conn.setAutoCommit(false);
	        // 4. 反射机制调用 savePost()	        
	        // 5. 事务提交
	        conn.commit();
	    } catch (NestedTransactionHelpException e) {
	        if (conn != null) try {conn.rollback();} catch (Exception e1) {LogKit.error(e1.getMessage(), e1);}
	        LogKit.logNothing(e);
	    } catch (Throwable t) {
	        // 6. 若有异常就回滚
	        if (conn != null) try {conn.rollback();} catch (Exception e1) {LogKit.error(e1.getMessage(), e1);}
	        throw t instanceof RuntimeException ? (RuntimeException)t : new ActiveRecordException(t);
	    }
	    finally {
	        try {
	            if (conn != null) {
	                if (autoCommit != null)
	                    conn.setAutoCommit(autoCommit);
	                conn.close();
	            }
	        } catch (Throwable t) {
	            LogKit.error(t.getMessage(), t);   
	        }
	        finally {
	            config.removeThreadLocalConnection();   
	        }
	      }
	   }
	  
}

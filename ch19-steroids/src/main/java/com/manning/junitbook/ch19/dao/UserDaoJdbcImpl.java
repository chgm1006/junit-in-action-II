/* 
 * ========================================================================
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package com.manning.junitbook.ch19.dao;

import com.manning.junitbook.ch19.model.User;

import java.sql.*;

public class UserDaoJdbcImpl implements UserDao {

    private Connection connection;

    private interface DaoMethod {
        Object doIt() throws SQLException;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void addUser(final User user) {
        wrapSqlException(new DaoMethod() {
            public Object doIt() throws SQLException {
                _addUser(user);
                return null;
            }
        });

    }

    private void _addUser(User user) throws SQLException {
//    PreparedStatement pstmt = connection.prepareStatement("INSERT INTO users VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
        connection.setAutoCommit(false);
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO users (username, first_name, last_name) VALUES (?,?,?)");
        try {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.executeUpdate();
            long id = getLastIdentity();
            connection.commit();
            user.setId(id);
        } finally {
            close(pstmt);
            connection.setAutoCommit(true);
        }
    }

    private long getLastIdentity() throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("CALL IDENTITY()");
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            rs.next();
            long id = rs.getLong(1);
            return id;
        } finally {
            close(rs, pstmt);
        }
    }

    public User getUserById(final long id) {
        return (User) wrapSqlException(new DaoMethod() {
            public Object doIt() throws SQLException {
                return _getUserById(id);
            }
        });
    }

    public User _getUserById(long id) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
        ResultSet rs = null;
        User user = null;
        try {
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                fill(user, rs);
            }
        } finally {
            close(rs, pstmt);
        }
        return user;
    }

    public void deleteUser(final long id) {
        wrapSqlException(new DaoMethod() {
            public Object doIt() throws SQLException {
                _deleteUser(id);
                return null;
            }
        });
    }

    private void _deleteUser(long id) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement pstmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
        try {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            connection.commit();
        } finally {
            close(pstmt);
            connection.setAutoCommit(true);
        }
    }

    private void fill(User user, ResultSet rs) throws SQLException {
        user.setUsername(rs.getString("username"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setId(rs.getLong("id"));
    }

    void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            // NOTE: phones table is not used by UserDaoJdbcImpl, only by UserDaoJpaImpl.
            // But as both are tested in the same test suite, it's necessary to create it.
            stmt.execute("DROP TABLE phones IF EXISTS; DROP TABLE users IF EXISTS");
            stmt.execute("CREATE TABLE users (id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1), username VARCHAR(10), first_name VARCHAR(10), last_name VARCHAR(10) )");
            stmt.execute("CREATE TABLE phones (id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1), number VARCHAR(255), type INTEGER, user_id INTEGER)");
            stmt.execute("ALTER TABLE phones ADD CONSTRAINT fk_telephones_users FOREIGN KEY (user_id) REFERENCES users");
        } finally {
            close(stmt);
        }
    }

    private void close(ResultSet rs, Statement pstmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(pstmt);
    }

    private void close(Statement stmt) {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Object wrapSqlException(DaoMethod method) {
        try {
            return method.doIt();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

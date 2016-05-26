/*
 * Copyright (C) 2014 Sunny Patel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package frontoffice;

import frontoffice.base.DBConnection;
import frontoffice.usa.frontoffice;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sunny Patel
 */
public class frontofficeTest extends DBConnection {
    
    String unStr = "7777777";
    String pwStr = "7777777";
    int userid = 0;
    
    public frontofficeTest() {
        try {
            String sql = "insert into users(`firstname`, `lastname`, `loginID`, " +
                    "`loginpass`, `canlogin`) values('TEST', 'USER'," +
                    "'7777777', '7777777', '1')";
            conn = getConnection();
            stmt = conn.createStatement();
            int success = stmt.executeUpdate(sql);
            sql = "select * from users where loginpass='7777777' and loginID='7777777'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                userid = rs.getInt("id");
            }
            sql = "insert into applicationright (`apid`, `userid`, `r`, `w`, `c`, `d`) " +
                    "values('1', '" + userid + "', '1', '1', '1', '1')";
            success = stmt.executeUpdate(sql);
        } catch (Exception a) {
            a.printStackTrace();
            fail("Cannot Create test user");
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        try {
            String sql = "delete from users where id='" + userid + "'";
            int success = stmt.executeUpdate(sql);
            sql = "delete from applicationright where userid='" + userid + "'";
            success = stmt.executeUpdate(sql);
        } catch (Exception a) {
            a.printStackTrace();
            fail("failed to delete user");
        }
    }

    /**
     * Test of authenticate method, of class frontoffice.
     */
    @Test
    public void testAuthenticate() {
        System.out.println("authenticate");
        frontoffice instance = new frontoffice();
        boolean allowed = instance.authenticate(unStr, pwStr);
        System.out.println(allowed);
        assertTrue(allowed);
    }
    
}

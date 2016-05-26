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
import frontoffice.usa.Customer;
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
public class CustomerTest extends DBConnection {
    int customerid = 0;
    
    public CustomerTest() {
        // We need to create a customer id
        try {
            String sql = "insert into customer (`first_name`, `last_name`, `address1`, " +
                    "`postcode`, `phone1`, `limit`, `created`) values(" +
                    "'SUNNY', 'PATEL', 'Test Road', 'W1 1AB', '08451478585','101.01', CURRENT_TIMESTAMP())";
            conn = getConnection();
            stmt = conn.createStatement();
            int success = stmt.executeUpdate(sql);
            sql = "select * from customer where address1='Test Road'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                customerid = rs.getInt("id");
            }
        } catch(Exception a) {
            a.printStackTrace();
            fail("Unable to create customer");
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
            String sql = "delete from customer where id='" + customerid + "'";
            int success = stmt.executeUpdate(sql);
        } catch(Exception a) {
            a.printStackTrace();
            fail("Teardown failed");
        }
    }

    /**
     * Test of getID method, of class Customer.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        Customer instance = new Customer(customerid);
        int expResult = customerid;
        int result = instance.getID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCustomerName method, of class Customer.
     */
    @Test
    public void testGetCustomerName() {
        System.out.println("getCustomerName");
        Customer instance = new Customer(customerid);
        String expResult = "SUNNY PATEL";
        String result = instance.getCustomerName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCustomerLimit method, of class Customer.
     */
    @Test
    public void testGetCustomerLimit() {
        System.out.println("getCustomerLimit");
        Customer instance = new Customer(customerid);
        float expResult = 101.01f;
        float result = instance.getCustomerLimit();
        assertEquals(expResult, result, 0.0);
    }
    
}

//5000264000100
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

/**
 *
 * @author Sunny Patel
 */
package frontoffice.usa;

import frontoffice.base.DBConnection;
import frontoffice.event.MiscButtonEvent;
import frontoffice.event.NumPadListener;
import frontoffice.event.QuickKeyEvent;
import frontoffice.util.UPCConverter;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Sunny Patel
 */

public class salesWindow extends DBConnection implements ActionListener,
        FocusListener, TableModelListener, KeyListener, QuickKeyEvent,
        MiscButtonEvent, NumPadListener, MouseListener {

    CustomerDisplayUI CDUI;
    
    JFrame frame = new JFrame("Sales Window");
    JTextField searchFld;
    JButton searchProduct, hold, unhold, plusTool, minusTool, delete;
    JLabel safesale;
    JLabel infoLbl;
    JPanel searchPnl;
    // Payments
    JButton payment;
    JTextField total, subtotal, taxtotal, discount, qty;
    JPanel paymentSuperPanel, paymentAmountPnl;
    // Customer Info
    JLabel customerNameLbl = new JLabel("CUSTOMER NAME");
    // For the misc options layout
    CardLayout miscOptLayout = new CardLayout();
    int currentcard = 1;
    JPanel miscOptDispPnl, miscOptCallPnl, numberPnlSuperParent;
    JPanel nonTaxBtnPnl, taxBtnPnl, payOutBtnPnl;
    JButton miscTax, miscNonTax, payOut, menu, quickKeys;
    JButton[] nonTaxBtns;
    JButton[] taxBtns;
    JButton[] payOutBtns;
    JTextField inputPrice;
    /* Expremental START*/
    JButton addnew = new JButton("Add Row");
    /* Expremental END*/

    JPanel mainCategoryParent = new JPanel();
    JPanel subCategoryParent = new JPanel();
    JPanel productParent = new JPanel();
    JButton[] mainCatBtns = new JButton[50];
    JButton[] subCatBtns = new JButton[50];
    JButton[] quickProducts = new JButton[150];
    // for keeping track of buttons issued
    static int SC = 0;
    static int PR = 0;
    static int productPanel = 0;
    CardLayout quickKeysTab;
    CardLayout quickKeysTabSub;
    int qkCurrentCard = 1;
    int qkCurrentCardSub = 1;

    // Main GUI
    Font fontBold = new Font("Sans", Font.BOLD, 14);
    Font fontItalic = new Font("Sans", Font.ITALIC, 14);
    Font fontRegular = new Font("Sans", Font.PLAIN, 14);

    // Main Panels
    JPanel rightPnl, leftPnl;
    JPanel tablePnl, paymentPnl, optionsPnl, quickBtnsPnl,
    numPadPnl, quickFldPnl, toolsPnl, numberPnlParent;

    // Render Table
    public static Vector salesData = new Vector(14, 14);
    public static SalesTableModel model = new SalesTableModel(salesData);
    SaleListSelectionModel slsm = new SaleListSelectionModel();
    public JTable table = new JTable(model);

    /**
     * Handles all the table things like setting font
     * setting the width and any other formatting we might want
     * Typically high level formatting is handled by the model
     */
    void table() {
        tablePnl = new JPanel();
        // Set the table GUI
        table.setRowHeight(25);
        // Add to scrollPane
        JScrollPane jsp = new JScrollPane(table);
        tablePnl.add(jsp);
        jsp.setPreferredSize(new Dimension(500, 400));
        model.addTableModelListener(this);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionModel(slsm);
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(172);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(45);
        table.getColumnModel().getColumn(4).setPreferredWidth(45);
        // Selection issues
        // INSTINCT: I think this is causing problems in updating table
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // NO Idea how else to do this
        table.addMouseListener(this);
    }

    /**
     * Renders the payment buttons and the totals formatting
     */
    void payment() {
        paymentSuperPanel = new JPanel();
        paymentAmountPnl = new JPanel();
        payment = new JButton("Payment", new ImageIcon("Icons/pay.png"));
        payment.setBackground(new Color(217, 30, 24));
        payment.setForeground(Color.WHITE);
        payment.addActionListener(this);
        total = new JTextField(9);
        total.setFont(h1);
        subtotal = new JTextField(9);
        taxtotal = new JTextField(9);
        discount = new JTextField(9);
        qty = new JTextField(9);
        qty.setFont(h1);
        subtotal.setFont(h3);
        taxtotal.setFont(h3);
        discount.setFont(h3);
        JLabel totalLbl = new JLabel("Total");
        JLabel taxTotalLbl = new JLabel("Tax");
        JLabel subtotalLbl = new JLabel("Sub-Total");
        JLabel discountLbl = new JLabel("Discount");
        JLabel qtyLbl = new JLabel("Quantity");
        paymentAmountPnl.add(subtotalLbl);
        paymentAmountPnl.add(subtotal);
        paymentAmountPnl.add(taxTotalLbl);
        paymentAmountPnl.add(taxtotal);
        paymentAmountPnl.add(totalLbl);
        paymentAmountPnl.add(total);
        paymentAmountPnl.add(discountLbl);
        paymentAmountPnl.add(discount);
        paymentAmountPnl.add(qtyLbl);
        paymentAmountPnl.add(qty);
        paymentAmountPnl.setLayout(new GridLayout(5, 2));
        // Add to the superPanel
        paymentSuperPanel.add(paymentAmountPnl);
        paymentSuperPanel.add(payment);
        paymentSuperPanel.setLayout(new GridLayout(1, 2));
    }

    /**
     * This has now moved to the MainMenu class or it needs to be
     * in it's own class this is because we want to handle it via a call back
     * function so that products can be selected at will and searched for
     */
    void searchProducts() {
        searchFld = new JTextField(11);
        searchFld.addKeyListener(this);
        searchFld.setFont(h1);
        searchProduct = new JButton("Search");
        searchProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                SearchProduct sp = new SearchProduct();

            }
        });
        searchPnl = new JPanel();
        searchPnl.add(searchFld);
        //searchPnl.add(searchProduct);
        searchPnl.setPreferredSize(new Dimension(200, 10));
        searchPnl.setLayout(new GridLayout(1, 2));
    }

    /**
     * Handles the main handling of the salesWindow it Tries to organise
     * everything this might need some rework later on
     */
    void render() {
        CDUI = new CustomerDisplayUI();
        
        // Make sure that we remove the frame
        frame.setUndecorated(true);
        // Panels
        rightPnl = new JPanel(); /* The right master panel*/
        leftPnl = new JPanel(); /* The Left Master Panel*/
        /* Compononents based */
        tablePnl = new JPanel(); /**/
        paymentPnl = new JPanel();
        optionsPnl = new JPanel();
        quickBtnsPnl = new JPanel();
        paymentSuperPanel = new JPanel();
        numPadPnl = new JPanel();
        numberPnlSuperParent = new JPanel(); /* The Super Number panel*/
        numberPnlParent = new JPanel(); /* The panel Containing imput box and pad */
        quickFldPnl = new JPanel();
        searchPnl = new JPanel();
        searchPnl.setPreferredSize(new Dimension(300, 50));
        searchProducts();
        addnew.addActionListener(this);
        table();
        payment();
        toolsPanel();
        miscOptions();
        //loadLineDisplay();
        // The Left Pnl
        leftPnl.add(searchPnl);
        leftPnl.add(tablePnl);
        leftPnl.add(toolsPnl);
        leftPnl.add(customerNameLbl);
        leftPnl.add(paymentSuperPanel);
        // The Right Panl
        rightPnl.add(miscOptCallPnl);
        rightPnl.add(miscOptDispPnl);

        // Set the panels layout
        leftPnl.setLayout(new BoxLayout(leftPnl, BoxLayout.PAGE_AXIS));
        rightPnl.setLayout(new BoxLayout(rightPnl, BoxLayout.PAGE_AXIS));
        JPanel MasterPnl = new JPanel();
        // Set the Panels
        MasterPnl.add(leftPnl);
        MasterPnl.add(rightPnl);
        MasterPnl.setLayout(new BoxLayout(MasterPnl, BoxLayout.LINE_AXIS));
        MasterPnl.setPreferredSize(new Dimension(1014, 711));
        frame.add(MasterPnl);
        frame.setSize(1024, 766);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * For adding, deleting, subtracting and holding the order
     */
    void toolsPanel() {
        /* Check ID, Hold, unhold, plus minus buttons*/
        hold = new JButton(new ImageIcon("Icons/out.png"));
        hold.addActionListener(this);
        unhold = new JButton(new ImageIcon("Icons/in.png"));
        unhold.addActionListener(this);
        plusTool = new JButton(new ImageIcon("Icons/list-add.png"));
        plusTool.addActionListener(this);
        minusTool = new JButton(new ImageIcon("Icons/list-remove.png"));
        minusTool.addActionListener(this);
        delete = new JButton(new ImageIcon("Icons/window-close.png"));
        delete.addActionListener(this);

        // Setup the colors of these buttons
        hold.setBackground(new Color(37, 116, 169));
        hold.setForeground(Color.WHITE);
        unhold.setBackground(new Color(37, 116, 169));
        unhold.setForeground(Color.WHITE);
        plusTool.setBackground(new Color(37, 116, 169));
        plusTool.setForeground(Color.WHITE);
        minusTool.setBackground(new Color(37, 116, 169));
        minusTool.setForeground(Color.WHITE);
        delete.setBackground(new Color(37, 116, 169));
        delete.setForeground(Color.WHITE);

        safesale = new JLabel("SAFE SALE");
        toolsPnl = new JPanel();
        toolsPnl.add(hold);
        toolsPnl.add(unhold);
        toolsPnl.add(plusTool);
        toolsPnl.add(minusTool);
        toolsPnl.add(delete);
        toolsPnl.add(safesale);
        toolsPnl.setPreferredSize(new Dimension(300, 40));
        toolsPnl.setLayout(new GridLayout(1, 6));
    }

    /**
     * This will render the misc options
     */
    void miscOptions() {
        miscOptDispPnl = new JPanel();
        miscOptCallPnl = new JPanel();
        JPanel menuAndQKPnl = new JPanel();

        // Set Layout for MISC PANELS
        miscOptDispPnl.setLayout(miscOptLayout);

        MiscButtons mb = new MiscButtons(this); // All the Misc Buttons

        miscOptCallPnl.add(mb.getCallButtons());
        //payOut.addForeground(COlir)
        menu = new JButton("Menu");
        menu.setBackground(new Color(68, 108, 179));
        menu.setForeground(Color.WHITE);
        menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentcard = 2;
                miscOptLayout.show(miscOptDispPnl, "" +(currentcard));
            }
        });
        menuAndQKPnl.add(menu);
        //menu.setForeground(Color.BLUE);
        quickKeys = new JButton("Quick Keys");
        quickKeys.setBackground(new Color(68, 108, 179));
        quickKeys.setForeground(Color.WHITE);
        quickKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentcard = 3;
                miscOptLayout.last(miscOptDispPnl);
            }
        });
        // Do we have any quick keys
        QuickKeys qk = new QuickKeys(this);
        if ( qk.canLoadQuickKeys() ) {
            miscOptCallPnl.add(quickKeys);
            menuAndQKPnl.add(quickKeys);
        }
        // Now add some layout and sizing
        menuAndQKPnl.setLayout(new GridLayout(1, 2)); // A quick Hack to
        menuAndQKPnl.setPreferredSize(new Dimension(200, 70)); // Allow the buttons to disp properly
        miscOptCallPnl.add(menuAndQKPnl);
        miscOptCallPnl.setLayout(new GridLayout(1, 5));
        miscOptCallPnl.setPreferredSize(new Dimension(500, 70));

        MainMenu m = new MainMenu();

        // Add the panel to the super panel
        miscOptDispPnl.add(mb.getMiscPanel(), "1");
        miscOptDispPnl.add(m.getMenu(), "2");
        miscOptDispPnl.add(qk.loadQuickKeys(), "3"); if(qk.canLoadQuickKeys());
        miscOptDispPnl.setPreferredSize(new Dimension(300, 250));
    }
    
    /**
     * Grabs Actual product info
     * @param barcode
     */
    private Object[] grabProductInfo(String barcode) {
        String sql = "select * from searchproducts where barcode = ? ";
        Object[] row = new Object[9];
        
        try {        
            // NORMALLY WE WANT TO SET TIMEOUT
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, barcode);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                row[0] = rs.getString(1);
                row[1] = rs.getString(2);
                row[2] = "1";
                row[3] = "0.00";
                row[4] = rs.getString("taxrate");
                row[5] = rs.getString(5);
                row[6] = "0.00"; 
            }
            
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("select * from product where id = ?");
            pstmt.setObject(1, row[0]);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                row[7] = rs.getString("notes");
                row[8] = rs.getString("notifytype");
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
        
        return row;
    }

    /**
     * When we scan a barcode we call fetch because it has a lot
     * of logic that we want to apply at a later stage and now
     */
    // Move this somewhere else
    void fetchProduct(String barcode) {
        boolean foundproduct = false;
        Object[] pdata = grabProductInfo(barcode);

        // Did we find the product?
        if(pdata[0] == null) {
            // Convert this barcode
            UPCConverter upc = new UPCConverter();
            String UPCABarcode = upc.convertUPCEtoA(barcode);
            pdata = grabProductInfo(UPCABarcode);

            // Did we find the product?
            if(pdata[0] != null) {
                foundproduct = true;
            }
        } else {
            foundproduct = true;
        }

        if (foundproduct) {
            boolean updatedQty = false;
            
            // Can we sell on a sunday?
            if(pdata[8] != null && pdata[8].equals("nosunday")) {
                Calendar cal = Calendar.getInstance();
                if(cal.DAY_OF_WEEK == 1) {
                    JOptionPane.showMessageDialog(frame, "Cannot sell wine on sunday!", 
                        "Not on Sunday", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                setSafeSale("warn", "Check ID");
            }
            
            if (pdata[8] != null && pdata[8].equals("age")) {
                String ageMsg = pdata[7].equals("") ? "Check ID" : pdata[7].toString();
                setSafeSale("warn", ageMsg);
            }
            
            // If there is a notes field show the notes message
            if(pdata[8] != null && pdata[8].equals("shownotes")) {
                JOptionPane.showMessageDialog(frame, pdata[7].toString(), "Notes",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Create a new Row Object
            Object[] row = new Object[7];
            for(int i = 0; i < 7; i++) {
                row[i] = pdata[i];
            }
            
            for(int i = 0; i < model.getRowCount(); i++) {
                String curPRID = model.getValueAt(i, 0).toString();
                Integer Qty = Integer.parseInt(model.getValueAt(i, 2).toString());
                
                if (curPRID.equals(row[0])) {
                    Qty++;
                    model.setValueAt(Qty, i, 2);
                    model.fireTableDataChanged();
                    updateTablePrice();
                    model.fireTableDataChanged();
                    updatedQty = true;

                    // I want to color a duplicate scanned barcode
                    final int finalIInt = i;
                    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable tableOther,
                                Object value, boolean isSelected, boolean hasFocus,
                                int row, int column) {
                            Component c = super.getTableCellRendererComponent(tableOther,
                                    value, isSelected, hasFocus, finalIInt, column);
                            if(row == finalIInt) {
                                c.setBackground(Color.ORANGE);
                            } else {
                                c.setBackground(null);
                            }

                            return this;
                        }
                    });

                    break;
                }
            }
            if (updatedQty == false) {
                insertRow(row);
            }
        } else {
            UIManager UI=new UIManager();
            UI.put("OptionPane.background",new Color(255,0,0));
            UI.put("Panel.background",new Color(255,0,0));
            String productNotFoundMessage = "<html><body style='background: red;'>"
                    + "<h1 style='font-size: 82px;color: white;'>Product Not Found!</h1>"
                    + "</body></html>";
            JOptionPane.showMessageDialog(frame, productNotFoundMessage,
                "ERROR", JOptionPane.PLAIN_MESSAGE);

            // Replace the panel background to normal
            //r=238,g=238,b=238]
            UI.put("OptionPane.background",new Color(238,238,238));
            UI.put("Panel.background",new Color(238,238,238));
        }   
    }

    /**
     * When we want to add products via the Quick Key
     */
    public void quickKeyClick( String productIDArg ) {
        try {
            // TODO: move all this to the qickkeys view
            // This horrible join is needed because qk
            // products don't need a barcode to be products
            String sql = "select pr.name, pp.price, tx.taxpercentage"
                    + " from product pr "
                    + "left join productprice pp on pr.id=pp.prid "
                    + "left join taxcode tx on tx.id = pr.taxid "
                    + "where pr.id='" + productIDArg + "'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Object[] row = {
                    productIDArg,
                    rs.getString("name"),
                    "1",
                    "0.00",
                    rs.getString("taxpercentage"),
                    rs.getString("price"),
                    "0.00"
                };
            insertRow(row);
            }

        } catch(Exception a) {
            a.printStackTrace();
        }
    }

    /**
     * A Standard place to insert data in to the sales table everyone can use
     */
    public boolean insertRow(Object[] args) {
        boolean returnval = false;
        if ( args.length == 7 ) {
            // Format the values before entry
            String arg1 = args[0].toString();
            String arg2 = args[1].toString();
            Integer arg3 = Integer.parseInt(args[2].toString());
            double arg4 = Double.parseDouble(args[3].toString());
            double arg5 = Double.parseDouble(args[4].toString());
            double arg6 = Double.parseDouble(args[5].toString());
            double arg7 = Double.parseDouble(args[6].toString());

            // Add to the table
            salesData.addElement(new SalesData(arg1, arg2, arg3, arg4, arg5, arg6, arg7));
            slsm.setSelection(table.getRowCount());
            updateTablePrice();
            updateCustomerDisplay();
            model.fireTableDataChanged();
        } else {
            Object[] msg = {"Cannot add values to row"};
            errorMessagePopup(msg);
        }
        return returnval;
    }

    /**
     * This is a place when we want to have warning, restriction and other
     * things on the sale. This is a standard place to do all that
     */
    void setSafeSale(String typeArg, String MessageArg) {
        // sets the Icons
        if( typeArg.equals("warn") ) {
            safesale.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
            safesale.setText(MessageArg);
        } else if ( typeArg.equals("info") ) {
            safesale.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            safesale.setText(MessageArg);
        } else if( typeArg.equals("err") ) {
            safesale.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
            safesale.setText(MessageArg);
        }
    }

    /**
     * Renders a JOptionPane dialog this is a place where we can make that a bit
     * easier and it can be tied to the frame of this class
     */
    void errorMessagePopup( Object[] args ) {
        String titleMessage = "Message Box";
        if ( args.length > 1 ) {
            titleMessage = args[1].toString();
        }
        if ( args.length > 2 && args[2].equals("error") ) {
            JOptionPane.showMessageDialog(frame, args[0].toString(), titleMessage,
                    JOptionPane.ERROR_MESSAGE);
        } else if ( args.length > 2 && args[2].equals("warning") ) {
            JOptionPane.showMessageDialog(frame, args[0].toString(), titleMessage,
                    JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, args[0].toString(), titleMessage,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * TODO: MOVE THIS SOMEWHERE.....
     */
    void holdsale() {
        int currentRowCount = table.getRowCount();
        if (currentRowCount > 0) {
            try {
                // Try and reuse a hold id
                Object holdidObj = Settings.get("holdid");
                Integer holdID = 0;
                HoldOrder h;
                if( holdidObj != null ) {
                    holdID = Integer.parseInt(holdidObj.toString());
                    h = new HoldOrder(holdID);
                } else {
                    h = new HoldOrder();
                }

                // Save The order
                h.save(getSaleTable());
                Object[] msg = {"Order on hold\n ID:" + h.getID(), "Order on Hold", ""};
                errorMessagePopup(msg);
                cleanup();
                Settings.remove("holdid");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Object[] msg = {"Cannot hold an empty order", "Empty Order Hold Error", "error"};
            errorMessagePopup(msg);
        }
    }

    // TODO: MOVE THIS TO IT'S CLASS
    void unholdWindow() {
        int currentRowCount = table.getRowCount();
        if ( currentRowCount < 1 ) {
            HoldOrder h = new HoldOrder("unhold");
            h.getHeldOrders();
        } else {
            Object[] msg = {"Cannot unhold on an existing order", "unhold Error", "error"};
            errorMessagePopup(msg);
        }
    }

    // TODO: MOVE THIS TO IT'S CLASS
    void unhold(HoldOrder h) {
        for(int i = 0; i < h.getRowCount(); i++) {
            insertRow(h.getRow(i));
        }
        Settings.put("holdid", h.getID());
    }

    /**
     * This method cleans up the class ready for the next class
     */
    void cleanup() {
        /*THIS IS FOR THE MAIN CLEAN UP ROUTINE SO WHEN A NEW SALE IS TO BE DONE*/
        try {
            int rowCount = model.getRowCount();
            // Main place
            for(int i = 0; i < rowCount; i++) {
                final int finalIInt = i;
                table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable tableOther,
                            Object value, boolean isSelected, boolean hasFocus,
                            int row, int column) {
                        Component c = super.getTableCellRendererComponent(tableOther,
                                value, isSelected, hasFocus, finalIInt, column);
                        c.setBackground(null);
                        return this;
                    }
                });
                model.removeRow(0);
            }
            ldg.updateDisplay("NEXT CUSTOMER PLEASE                    ");
        } catch(Exception a) {
            //a.printStackTrace();
            Object[] error = {"Cannot clean up " + a.getMessage(), "Clean Up Error", "error"};
            errorMessagePopup(error);
        }
    }

    /**
     * When a new item is added the totals fields need to
     * be update so this class can handle that
     */
    void updateTotals() {
        // TODO: Just pass the entire table vector
        Total t = new Total();
        for(int i = 0; i < table.getRowCount(); i++) {
            // Set the Qty
            t.setQty(getQty(i));
            // Set the Total
            t.setTax(model.getValueAt(i, 4), getQty(i));
            t.setTotal(model.getValueAt(i, 5), getQty(i));
            // Set the Discount
            t.setDiscount(model.getValueAt(i, 3));
        }
        // It's imposible to cast this to string so make it an object and pass it
        Object ttotal = t.getTotal();
        Object tsubtotal = t.getSubTotal();
        Object ttaxtotal = t.getTaxTotal();
        Object tqty = t.getQty();
        Object tdiscount = t.getDiscount();
        DecimalFormat formatterDec = (DecimalFormat)NumberFormat.getNumberInstance(Locale.UK);
        formatterDec.applyPattern("###,##0.00");

        total.setText(formatterDec.format(ttotal));
        subtotal.setText(formatterDec.format(tsubtotal));
        taxtotal.setText(formatterDec.format(ttaxtotal));
        qty.setText(tqty.toString());
        discount.setText(formatterDec.format(tdiscount));

        // Lets update the line display if there is any data
        if (table.getRowCount() > 0) {
            updateCustomerDisplay();
        }
        // Set the focus to the barcode field
        searchFld.requestFocus();
    }

    /**
     * This is to be called from anywhere in this class
     * This will update the customer display
     * It will identify the focused column and update the totals
     */
    private void updateCustomerDisplay() {
        try {
            String lineone = Settings.get("lineDisp").toString();
            String linetwo = Settings.get("lineDisp2").toString();
            // Get the selectedRow or the last Row.
            int row = slsm.getSelectedIndex();
            int rowcount = table.getRowCount();
            // Do we have any items in the table then we need to
            // Display the default from settings object
            if(rowcount == 0) {
                row = table.getRowCount();
                if (row == 0) {
                    ldg.updateDisplay(lineone + linetwo);
                    return;
                }
            }
            // If selected Row is -1 then make it the last row
            if (row == -1 ) {
                row = rowcount - 1;
            }

            String selectedProductName = model.getValueAt(row, 1).toString();
            String priceStr = getLikeCurrency(model.getValueAt(row, 5).toString());
            int selectedQty = getQty(row);
            lineone = selectedQty + " * " + selectedProductName + " " + priceStr;

            // Make it exactly 20 chars
            if(lineone.length() > 20 ) {
                // Work out amount to trim the name by
                int excessAmount = lineone.length() - 20;
                String mandatoryStr = selectedQty + " * ";
                int amountToTrim = selectedProductName.length() - excessAmount;
                selectedProductName = selectedProductName.substring(0, amountToTrim);
                lineone = mandatoryStr + selectedProductName + " " + priceStr;
            } else if (lineone.length() < 20) {
                // Find out spaces to add
                int amountToAdd = 20 - lineone.length();
                String mandatoryStr = selectedQty + " * ";
                lineone = mandatoryStr + selectedProductName;
                // Lets just add the spaces
                for(int i = 0; i < amountToAdd; i++) {
                    lineone = new StringBuffer(lineone).append(" ").toString();
                }

                lineone = new StringBuffer(lineone).append(" ").append(priceStr).toString();
            }

            String totalAmount = total.getText();
            linetwo = "Total " + totalAmount;
            // Trim to standards
            if(linetwo.length() > 20 ) {
                // Let the length to trim
                int excessAmount = linetwo.length() - 20;
                String mandatoryStr = " " + totalAmount;
                int amountToTrim = "Total".length() - excessAmount;
                linetwo = linetwo.substring(0, amountToTrim);
                linetwo += mandatoryStr;

            } else if(linetwo.length() < 20) {
                int amountToAdd = 20 - linetwo.length();
                String mandatoryStr = " " + totalAmount;
                linetwo = "Total";
                // Lets just add the spaces
                for(int i = 0; i < amountToAdd; i++) {
                    linetwo = new StringBuffer(linetwo).append(" ").toString();
                }

                linetwo += mandatoryStr;
            }

            ldg.updateDisplay(lineone + linetwo);
        } catch(Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(salesWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * We want to get the quantity of the sale table this is an way
     */
    int getQty(int rowArg) {
        int retval = 0;
        try {
            Object val = model.getValueAt(rowArg, 2);
            retval = Integer.parseInt(val.toString());
        } catch( Exception a ) {
            a.printStackTrace();
        }

        return retval;
    }

    /**
     * We can also set the quantity of the sale item
     */
    void setQty(int rowArg, int qtyArg) {
        try {
            model.setValueAt(qtyArg, rowArg, 2);
            model.setValueAt(22.54f, rowArg, 5);
        } catch ( Exception a ) {
            a.printStackTrace();
        }
    }

    /**
     * This is where we aggregate duplicate products and
     * we also want to apply special offers and other features
     */
    /**
     * TODO: DO YOU WANT THIS
     */
    void updateTableCheck() {
        /*
        final int rowCount = table.getRowCount();
        HashMap products = new HashMap();
        ArrayList<Integer> rowToDelete = new ArrayList<>();
        // Aggregate the duplicates
        for(int i = 0; i < rowCount; i++) {
            // Get the product ID and the Qty
            String prid = model.getValueAt(i, 0).toString();
            int prQty = Integer.parseInt(model.getValueAt(i, 2).toString());

            // Does a previous count with this already exist?
            if( products.containsKey(prid) ) {
                // Update the product count
                int currentPRQty = Integer.parseInt(products.get(prid).toString());
                int updateQty = prQty + currentPRQty;
                products.put(prid, updateQty);
                model.setValueAt(updateQty, i, 2);
                rowToDelete.add(i);
            } else {
                products.put(prid, prQty);
            }
        }
        // Lets Remove the rows
        for (Integer rowNo : rowToDelete) {
            model.removeRow(rowNo);
        }

        // Lets update the rows
        for(int i = 0; i < table.getRowCount(); i++) {
            String prid = model.getValueAt(i, 0).toString();
            if(products.containsKey(prid)) {
                // Lets Update the table with this
                Object qty = products.get(prid);
                model.setValueAt(qty, i, 2);
            }
        }
        */
    }

    /**
     * Some times we want to export the current sale table to a class
     * this is a clean way to do that
     */
    // Why can't this be model.getTableData();
    public Vector getSaleTable() {
        Vector LocalSaleData = new Vector();
        for(int i = 0; i < table.getRowCount(); i++) {
            Vector row = new Vector();
            row.addElement(model.getValueAt(i, 0).toString());
            row.addElement(model.getValueAt(i, 1).toString());
            row.addElement(model.getValueAt(i, 2).toString());
            row.addElement(model.getValueAt(i, 3).toString());
            row.addElement(model.getValueAt(i, 4).toString());
            row.addElement(model.getValueAt(i, 5).toString());
            row.addElement(model.getValueAt(i, 6).toString());
            LocalSaleData.addElement(row);
        }
        return LocalSaleData;
    }

    /**
     * This should really have a call to functions so that it is easier
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object caller = ae.getSource();
        if ( caller == hold ) {
            holdsale();
        } else if ( caller == unhold ) {
            unholdWindow();
        } else if ( caller == plusTool ) {
            // Increment quantity by 1
            int row = slsm.getSelectedIndex();
            if ( row == -1 ) { // if not row is selected update the lastone
                int lastRow = table.getRowCount();
                lastRow--;
                int curQty = getQty(lastRow);
                curQty++;
                setQty(lastRow, curQty++);
            } else {
                int curQty = getQty(row);
                curQty++;
                setQty(row, curQty++);
            }
        } else if ( caller == minusTool ) {
            // Decrement quantity by 1
            int row = slsm.getSelectedIndex();
            if ( row == -1 ) { // if not row is selected update the lastone
                int lastRow = table.getRowCount();
                lastRow--;
                int curQty = getQty(lastRow);
                curQty--;
                setQty(lastRow, curQty--);
            } else {
                int curQty = getQty(row);
                curQty--;
                setQty(row, curQty);
            }
        } else if ( caller == payment ) {
            try {
                // TODO: CHOOSE bETWWEN DOUBLE OR FLOAT
                float totalArg = Float.parseFloat(total.getText());
                float subtotalArg = Float.parseFloat(subtotal.getText());
                float discountArg = Float.parseFloat(discount.getText());
                float taxamountArg = Float.parseFloat(taxtotal.getText());
                int qtyArg = Integer.parseInt(qty.getText());
                // If there is nothing there then there is absolutely no point
                if ( table.getRowCount() > 0 ) {
                    Payment p = new Payment(totalArg, subtotalArg, discountArg, taxamountArg, qtyArg);
                    // Is a customer checking out
                    if ( Settings.containsKey("customerid") ) {
                        String custIDStr = Settings.get("customerid").toString();
                        int custid = Integer.parseInt(custIDStr);
                        p.setCustomer(custid);
                    }
                    // Now all the essentials of the payment
                    p.addSaleData(getSaleTable());
                    p.render();
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
        } else if ( caller == delete ) {
            try {
                int SelectedRow = slsm.getSelectedIndex();
                int RowCount = table.getRowCount() - 1;
                if ( SelectedRow == -1 ) {
                    model.removeRow(RowCount);
                } else {
                    model.removeRow(SelectedRow);
                }
            } catch(Exception a) {
                warn(a.getMessage() + " @ Delete item");
            }
        }
        updateTablePrice();
        model.fireTableDataChanged();
        updateCustomerDisplay();
    }

    @Override
    public void focusGained(FocusEvent fe) {
    }

    @Override
    public void focusLost(FocusEvent fe) {
    }

    /**
     * We want to do a discount check and update the prices
     * for the qtys of products.
     * This will fire a table data changed trigger to update the
     * totals correctly
     */
    // FIXME:: DO YOU NEED THIS?
    private void updateTablePrice() {
        DiscountCheck dc = new DiscountCheck(getSaleTable());
        String lastProdName = "No Product Scanned";
        for(int i = 0; i < model.getRowCount(); i++) {
            String prid = model.getValueAt(i, 0).toString();
            int qty = getQty(i);
            String price = dc.getPrice(prid, qty);
            // If the item is a misc item then skip it
            if (price.equals("")) {
                continue;
            }

            float priceFlt = 0.000f;
            priceFlt = Float.parseFloat(price);
            model.setValueAt(priceFlt, i, 5);
        }
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        // make Sure the totals are correct
        updateTotals();
        updateTableCheck();
    }

    /**
     * A Perl like warn statement so to avoid the System.out.println();
     */
    void warn(Object msg) {
        System.out.println(msg.toString());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * When a product is scanned it does a return key after the end of the barcode
     * this is so that we can pick that up and process the scanned barcode
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == 10 ) {
            // We have scanned it and saved it
            String barcode = searchFld.getText();
            // Clear it to stop further triggers
            searchFld.setText("");
            if(!barcode.equals("")) {
                fetchProduct(barcode);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * The QucikKeys class callback so that we can added everything to the table
     */
    @Override
    public void quickKeyEvent(String quickKey) {
        // Tell the table what happened
        quickKeyClick(quickKey);
    }

    @Override
    public void miscButtonEvent(Object[] rowArg) {
        // Every row is a new row
        insertRow(rowArg);
        //ListSelectionModel tableLSM = table.getSelectionModel();
        //tableLSM.setAnchorSelectionIndex(table.getRowCount());
    }

    @Override
    public void miscButtonFocus(String buttonName) {
        currentcard = 1;
        miscOptLayout.first(miscOptDispPnl);
    }

    @Override
    public void editSaleTableData(String value) {
        int rowArg = slsm.getSelectedIndex();
        int colArg = table.getSelectedColumn();
        // We know it's the Unit price or the discount
        if(colArg == 5 || colArg == 3) {
            // Value in pence
            float unitPrice = Float.parseFloat(value) / 100;
            model.setValueAt(unitPrice, rowArg, colArg);
            model.setValueAt(unitPrice, rowArg, 6);
        // Edit the quantity
        } else if ( colArg == 2) {
            int qtyInt = Integer.parseInt(value);
            model.setValueAt(qtyInt, rowArg, colArg);
        }
        model.fireTableDataChanged();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int columnSelected = table.getSelectedColumn();
        // Only Qty, UnitPrice and discount
        if(columnSelected == 2 || columnSelected == 3 || columnSelected == 5) {
            NumPad np = new NumPad(this);
            updateTablePrice();
            model.fireTableDataChanged();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

/*
----------N O T E S----------
ActionEvent => Action Listeners can be Overridden on their own loops
    i.e.
    for(int i = 0; i < buttons.length; i++) {
        final int j = i;
        buttons[i] = new Jbutton("Button " + i);
        buttons[i].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.out.println("Buttons " + j);
            }
        });
    }

    // This is the preferred method as it is easy to follow

object[] items = select sum(qty), prid from saleitem group by prid;

select sum(qty), prid from saleitem where saleid in (select saleid from saleitem where prid=items[0]);

*/

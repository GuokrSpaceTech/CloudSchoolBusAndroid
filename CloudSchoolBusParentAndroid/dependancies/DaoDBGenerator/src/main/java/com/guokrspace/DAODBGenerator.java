package com.guokrspace;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class DAODBGenerator {


    /**
     * Generates entities and DAOs for the example project DaoExample.
     * <p/>
     * Run it as a Java application (not Android).
     *
     * @author Markus
     */

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.guokrspace.daodb");

        addConfig(schema);
        addStudent(schema);

        new DaoGenerator().generateAll(schema, "src-gen");
    }

    private static void addConfig(Schema schema) {
        Entity note = schema.addEntity("ConfigEntity");
        note.addIdProperty();
        note.addStringProperty("sid");
        note.addShortProperty("currentStudent");
        note.addStringProperty("username");
        note.addStringProperty("password");
    }


//        private static void addCustomerOrder(Schema schema) {
//            Entity customer = schema.addEntity("Customer");
//            customer.addIdProperty();
//            customer.addStringProperty("name").notNull();
//
//            Entity order = schema.addEntity("Order");
//            order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
//            order.addIdProperty();
//            Property orderDate = order.addDateProperty("date").getProperty();
//            Property customerId = order.addLongProperty("customerId").notNull().getProperty();
//            order.addToOne(customer, customerId);
//
//            ToMany customerToOrders = customer.addToMany(order, customerId);
//            customerToOrders.setName("orders");
//            customerToOrders.orderAsc(orderDate);
//        }

    private static void addStudent(Schema schema) {
        Entity student = schema.addEntity("StudentEntity");
        student.addStringProperty("uid_student").notNull();
        student.addStringProperty("uid_class");
        student.addStringProperty("inactive");
        student.addStringProperty("birthday");
        student.addStringProperty("cnname");
        student.addStringProperty("nikename");
        student.addStringProperty("sex");
        student.addStringProperty("classname");
        student.addStringProperty("schoolid");
    }

}

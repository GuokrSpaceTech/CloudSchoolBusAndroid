package com.guokrspace;

import java.util.List;

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
        addAriticle(schema);

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

    private static void addAriticle(Schema schema)
    {
        Entity article = schema.addEntity("ArticleEntity");
        article.addStringProperty("articlekey").notNull().primaryKey();
        article.addStringProperty("tag");
        article.addStringProperty("articleid");
        article.addStringProperty("title");
        article.addStringProperty("content");
        article.addStringProperty("publishtime");
        article.addStringProperty("addtime");
        article.addStringProperty("upnum");
        article.addStringProperty("commentnum");
        article.addStringProperty("havezan");


        Entity image = schema.addEntity("ImageEntity");
        image.addStringProperty("filename").notNull().primaryKey();
        image.addStringProperty("source");
        image.addStringProperty("fext");
        image.addStringProperty("size");
        image.addStringProperty("isCloud");
        Property ariticle_id_image = image.addStringProperty("articleId").notNull().getProperty();

        image.addToOne(article,ariticle_id_image);
        ToMany ariticleToImages = article.addToMany(image, ariticle_id_image);
        ariticleToImages.setName("images");

        Entity tag = schema.addEntity("TagEntity");
        tag.addStringProperty("tagid").notNull().primaryKey();
        tag.addStringProperty("tagName");
        tag.addStringProperty("tagnamedesc");
        tag.addStringProperty("tagname_en");
        tag.addStringProperty("tagnamedesc_en");
        Property ariticle_id_tag = tag.addStringProperty("articleId").notNull().getProperty();

        tag.addToOne(article,ariticle_id_tag);
        ToMany articleToTags = article.addToMany(tag, ariticle_id_tag);
        articleToTags.setName("tags");

    }

}
